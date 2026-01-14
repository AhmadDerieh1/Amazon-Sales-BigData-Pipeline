package pipeline

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.functions._
import pipeline.probabilistic.{ProductBloomFilter, TopProductsTracker, UniqueProductCounter}
import java.time.Instant

object MongoSink {

  def writeToMongoWithProbabilistic(
                                     df: DataFrame,
                                     checkpoint: String,
                                     mongoUri: String,
                                     database: String,
                                     collectionClean: String,
                                     collectionStats: String,
                                     collectionNewProducts: String
                                   ): StreamingQuery = {
    df.writeStream
      .outputMode("append")

      .option("checkpointLocation", checkpoint)
      .trigger(Trigger.ProcessingTime("5 seconds"))
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>

        if (!batchDF.isEmpty) {
          val spark: SparkSession = batchDF.sparkSession
          import spark.implicits._

          println(s"[MongoSink] START batch=$batchId rows=${batchDF.count()}")

          batchDF.write
            .format("mongodb")
            .mode("append")
            .option("uri", mongoUri)
            .option("database", database)
            .option("collection", collectionClean)
            .save()


          val perProduct = batchDF
            .select($"Product_Name", $"Quantity_int")
            .where($"Product_Name".isNotNull && length(trim($"Product_Name")) > 0)
            .groupBy(trim($"Product_Name").as("product_name"))
            .agg(sum($"Quantity_int").as("qty_sum"))
            .as[(String, Long)]
            .collect()
          val newProducts = scala.collection.mutable.ListBuffer[(Long, String, String)]()

          perProduct.foreach { case (p, qtySum) =>
            TopProductsTracker.addProduct(p, qtySum.toInt)

            if (!ProductBloomFilter.mightContain(p)) {
              ProductBloomFilter.add(p)
              println(s"🆕 New Product Detected: $p")
              newProducts += ((batchId, p, Instant.now().toString))
            }
          }

          if (newProducts.nonEmpty) {
            val newProductsDF = newProducts.toSeq.toDF("batch_id", "product_name", "first_seen_at")

            newProductsDF.write
              .format("mongodb")
              .mode("append")
              .option("uri", mongoUri)
              .option("database", database)
              .option("collection", collectionNewProducts)
              .save()

            println(s"[MongoSink] wrote new_products=${newProducts.size} to $collectionNewProducts")
          } else {
            println(s"[MongoSink] new_products=0 (no new items this batch)")
          }

          val uniqueApprox: Long = UniqueProductCounter.countUniqueProducts(batchDF)

          val top10Str: String =
            TopProductsTracker.topN(10)
              .map { case (p, c) => s"$p:$c" }
              .mkString(", ")

          val statsDF = Seq((batchId, uniqueApprox, top10Str, Instant.now().toString))
            .toDF("batch_id", "unique_products_approx", "top10_products_approx", "created_at")

          statsDF.write
            .format("mongodb")
            .mode("append")
            .option("uri", mongoUri)
            .option("database", database)
            .option("collection", collectionStats)
            .save()

          println(s"[MongoSink] DONE batch=$batchId | unique≈$uniqueApprox | newProducts=${newProducts.size}")
        }
      }
      .start()
  }
}

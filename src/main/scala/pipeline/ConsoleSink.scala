package pipeline

import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.streaming.StreamingQuery

import pipeline.Probabilistic.TopProductsTracker
import pipeline.Probabilistic.ProductBloomFilter
import pipeline.Probabilistic.UniqueProductCounter
import pipeline.Probabilistic.ProductLSH

object ConsoleSink {

  def writeToConsole(df: DataFrame, checkpoint: String): StreamingQuery = {

    df.writeStream
      .outputMode("append")
      .option("checkpointLocation", checkpoint)
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>

        println(s"\n======= Batch: $batchId =======")

        batchDF.rdd.foreachPartition { (it: Iterator[Row]) =>
          it.foreach { (row: Row) =>
            println(row.toSeq.mkString(" | "))
          }
        }

        batchDF.collect().foreach { row =>
          val product  = row.getAs[String]("Product_Name")
          val quantity = row.getAs[Int]("Quantity_int")

          TopProductsTracker.addProduct(product, quantity)

          if (!ProductBloomFilter.mightContain(product)) {
            println(s"New Product Detected: $product")
            ProductBloomFilter.add(product)
          }
        }

        println(s"\nTop 10 Products (Approx - Global) after batch $batchId:")
        TopProductsTracker.topN(10).foreach {
          case (product, count) =>
            println(s"$product -> $count")
        }

        val uniqueCount =
          UniqueProductCounter.countUniqueProducts(batchDF)

        println(s"\nUnique Products (Approx): $uniqueCount")

        if (batchId % 5 == 0) {

          println(s"\nRunning LSH Similarity Detection (batch $batchId)...")

          val similarProducts =
            ProductLSH.findSimilarProducts(
              batchDF,
              batchDF.sparkSession,
              distanceThreshold = 0.6
            )

          similarProducts.show(false)
        }
      }
      .start()
  }
}

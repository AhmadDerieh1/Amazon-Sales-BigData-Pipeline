package pipeline.Probabilistic

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.ml.feature.{HashingTF, MinHashLSH}
import org.apache.spark.sql.functions._

object ProductLSH {

  def findSimilarProducts(
                           df: DataFrame,
                           spark: SparkSession,
                           distanceThreshold: Double = 0.6
                         ): DataFrame = {

    import spark.implicits._

    val tokensDF = df
      .select($"Product_Name")
      .filter($"Product_Name".isNotNull)
      .distinct()
      .withColumn(
        "tokens",
        split(regexp_replace(lower($"Product_Name"), "[^a-z0-9 ]", ""), " ")
      )
      .filter(size($"tokens") > 0)

    val hashingTF = new HashingTF()
      .setInputCol("tokens")
      .setOutputCol("features")
      .setNumFeatures(1024)

    val featurizedDF = hashingTF.transform(tokensDF)

    val lsh = new MinHashLSH()
      .setInputCol("features")
      .setOutputCol("hashes")
      .setNumHashTables(5)

    val model = lsh.fit(featurizedDF)

    model
      .approxSimilarityJoin(
        featurizedDF,
        featurizedDF,
        distanceThreshold,
        "JaccardDistance"
      )
      .filter(col("datasetA.Product_Name") =!= col("datasetB.Product_Name"))
      .select(
        col("datasetA.Product_Name").alias("Product_A"),
        col("datasetB.Product_Name").alias("Product_B"),
        col("JaccardDistance")
      )
      .distinct()
  }
}

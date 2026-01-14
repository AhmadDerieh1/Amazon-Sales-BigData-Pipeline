package pipeline

import org.apache.spark.sql.SparkSession

object SparkConfig {
  def createSpark(appName: String): SparkSession = {
    System.setProperty("hadoop.home.dir", "C:\\hadoop-3.3.6")

    SparkSession.builder()
      .appName(appName)
      .master("local[*]")
      .getOrCreate()
  }
}

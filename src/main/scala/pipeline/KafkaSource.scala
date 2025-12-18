package pipeline

import org.apache.spark.sql.{DataFrame, SparkSession}

object KafkaSource {
  def readKafka(spark: SparkSession,
                bootstrap: String,
                topic: String): DataFrame = {

    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrap)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .option("failOnDataLoss", "false")
      .load()
  }
}

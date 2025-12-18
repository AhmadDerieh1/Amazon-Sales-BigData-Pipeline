package pipeline

object MainApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkConfig.createSpark("Kafka Spark Streaming Clean")
    spark.sparkContext.setLogLevel("WARN")

    val kafkaDF   = KafkaSource.readKafka(spark, "localhost:9092", "sales-topic")
    val rawValue  = Parser.extractJsonValue(kafkaDF)
    val parsed    = Parser.parse(rawValue)
    val cleaned   = Cleaner.clean(parsed)

    val query = ConsoleSink.writeToConsole(
      cleaned,
      "C:/Users/ahmad/spark-checkpoints/sales-topic-clean-run1"
    )
    query.awaitTermination()
  }
}
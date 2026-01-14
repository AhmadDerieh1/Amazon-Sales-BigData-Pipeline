package pipeline

object MainApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkConfig.createSpark("Kafka Spark Streaming Clean")
    spark.sparkContext.setLogLevel("WARN")

    val kafkaDF  = KafkaSource.readKafka(spark, "localhost:9092", "sales-topic")
    val rawValue = Parser.extractJsonValue(kafkaDF)
    val parsed   = Parser.parse(rawValue)
    val cleaned  = Cleaner.clean(parsed)

    val query = MongoSink.writeToMongoWithProbabilistic(
      df              = cleaned,
      checkpoint      = s"C:/Users/ahmad/spark-checkpoints/mongo_${System.currentTimeMillis()}",
      mongoUri        = "mongodb://localhost:27017",
      database        = "amazon_sales",
      collectionClean = "sales_clean",
      collectionStats = "sales_stats",
      collectionNewProducts = "sales_new_products"
    )

    query.awaitTermination()
  }
}

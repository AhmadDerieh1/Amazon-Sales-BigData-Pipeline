import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

object KafkaSparkStreaming {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "C:\\hadoop-3.3.6")

    val spark = SparkSession.builder()
      .appName("Kafka Spark Streaming")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "sales-topic")
      .option("startingOffsets", "earliest")
      .load()

    val valueDf = df
      .selectExpr("CAST(value AS STRING) as value")
    val query = valueDf.writeStream
      .format("console")
      .outputMode("append")
      .option("truncate", "false")
      .option("numRows", "1000")
      .option("checkpointLocation",
        "C:/Users/ahmad/spark-checkpoints/sales-topic-run3")
      .trigger(Trigger.ProcessingTime("2 seconds"))
      .start()

    query.awaitTermination()
  }
}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types._

object KafkaSparkStreaming {
  def main(args: Array[String]): Unit = {

    // 1) Hadoop winutils path (لازم قبل SparkSession)
    System.setProperty("hadoop.home.dir", "C:\\hadoop-3.3.6")

    // 2) Spark Session
    val spark = SparkSession.builder()
      .appName("Kafka Spark Streaming")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // 3) Read from Kafka (Streaming)
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "sales-topic")
      .option("startingOffsets", "latest")

      // .option("startingOffsets", "earliest")
      .load()

    // 4) Get value as STRING + Filter only JSON records (remove hello, hello2, commands...)
    val rawValueDF = kafkaDF
      .selectExpr("CAST(value AS STRING) AS value")
      .filter(trim(col("value")).startsWith("{"))
      .filter(col("value").contains("\"Order_ID\""))   // زيادة تأكيد إنه JSON تبعنا

    // 5) Define JSON Schema (حسب الداتا تبعتك)
    val schema = new StructType()
      .add("Order_ID", StringType)
      .add("Date", StringType)
      .add("Customer_ID", StringType)
      .add("Product_Category", StringType)
      .add("Product_Name", StringType)
      .add("Quantity", StringType)
      .add("Unit_Price_INR", StringType)
      .add("Total_Sales_INR", StringType)
      .add("Payment_Method", StringType)
      .add("Delivery_Status", StringType)
      .add("Review_Rating", StringType)
      .add("Review_Text", StringType)
      .add("State", StringType)
      .add("Country", StringType)

    // 6) Parse JSON -> columns
    val parsedDF = rawValueDF
      .withColumn("json", from_json(col("value"), schema))
      .select("json.*")

    // 7) Cleaning/Casting (حوّل الأرقام) + فلترة nulls
    val cleanedDF = parsedDF
      .withColumn("Quantity_int", col("Quantity").cast(IntegerType))
      .withColumn("Unit_Price_INR_double", col("Unit_Price_INR").cast(DoubleType))
      .withColumn("Total_Sales_INR_double", col("Total_Sales_INR").cast(DoubleType))
      .withColumn("Review_Rating_int", col("Review_Rating").cast(IntegerType))
      .filter(col("Order_ID").isNotNull && col("Quantity_int").isNotNull)

    // 8) Print each record ALONE (not as table)
    import org.apache.spark.sql.{Dataset, Row}

    val query = cleanedDF.writeStream
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>
        println(s"\n======= Batch: $batchId =======")

        batchDF
          .toJSON
          .toLocalIterator()
          .forEachRemaining(println)
      }
      .option("checkpointLocation", "C:/Users/ahmad/spark-checkpoints/sales-topic-run5")
      .trigger(Trigger.ProcessingTime("2 seconds"))
      .start()

    query.awaitTermination()

  }
}

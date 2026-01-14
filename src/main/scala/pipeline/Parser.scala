package pipeline

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object Parser {
  def extractJsonValue(kafkaDF: DataFrame): DataFrame = {
    kafkaDF
      .selectExpr("CAST(value AS STRING) AS value")
      .filter(trim(col("value")).startsWith("{"))
      .filter(col("value").contains("\"Order_ID\""))
  }

  def parse(rawValueDF: DataFrame): DataFrame = {
    rawValueDF
      .withColumn("json", from_json(col("value"), SalesSchema.schema))
      .select("json.*")
  }
}

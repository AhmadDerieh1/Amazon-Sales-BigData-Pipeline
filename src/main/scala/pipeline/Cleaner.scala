package pipeline

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object Cleaner {

  def clean(df: DataFrame): DataFrame = {
    df
      .withColumn("Quantity_int", col("Quantity").cast(IntegerType))
      .withColumn("Unit_Price_INR_double", col("Unit_Price_INR").cast(DoubleType))
      .withColumn("Total_Sales_INR_double", col("Total_Sales_INR").cast(DoubleType))
      .withColumn("Review_Rating_int", col("Review_Rating").cast(IntegerType))
      .filter(col("Order_ID").isNotNull)
      .filter(col("Quantity_int") > 0)
      .filter(col("Unit_Price_INR_double") > 0)
      .filter(col("Total_Sales_INR_double") >= 0)
      .withColumn("Order_Date", to_date(col("Date"), "yyyy-MM-dd"))
      .filter(col("Order_Date").isNotNull)
      .withColumn("Product_Name", trim(col("Product_Name")))
      .withColumn("Customer_ID", trim(col("Customer_ID")))
      .drop("Quantity", "Unit_Price_INR", "Total_Sales_INR", "Review_Rating")
  }
}
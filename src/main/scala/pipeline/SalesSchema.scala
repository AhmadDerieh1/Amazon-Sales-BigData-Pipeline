package pipeline

import org.apache.spark.sql.types._

object SalesSchema {
  val schema: StructType = new StructType()
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
}

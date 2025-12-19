package pipeline.Probabilistic

import org.apache.spark.sql.{DataFrame, Row}

object UniqueProductCounter {

  def countUniqueProducts(df: DataFrame): Long = {
    df.selectExpr("approx_count_distinct(Product_Name) as unique_products")
      .collect()(0)
      .getAs[Long]("unique_products")
  }
}

package pipeline.probabilistic

import org.apache.spark.util.sketch.CountMinSketch
import scala.collection.mutable

object TopProductsTracker extends Serializable {

  private val seed  = 42
  private val depth = 10
  private val width = 1000

  private val cms = CountMinSketch.create(depth, width, seed)
  private val productSet = mutable.Set[String]()

  def addProduct(productName: String, quantity: Int): Unit = {
    if (productName != null && quantity > 0) {
      cms.add(productName, quantity)
      productSet += productName
    }
  }

  def topN(n: Int = 10): Seq[(String, Long)] = {
    productSet.toSeq
      .map(p => p -> cms.estimateCount(p))
      .sortBy(-_._2)
      .take(n)
  }

  def reset(): Unit = {
    productSet.clear()
  }
}
package pipeline.probabilistic

import com.google.common.hash.{BloomFilter, Funnels}
import java.nio.charset.StandardCharsets

object ProductBloomFilter extends Serializable {

  private val bloom: BloomFilter[CharSequence] =
    BloomFilter.create(
      Funnels.stringFunnel(StandardCharsets.UTF_8),
      100000,
      0.01
    )

  def mightContain(product: String): Boolean = {
    if (product == null) false
    else bloom.mightContain(product)
  }

  def add(product: String): Unit = {
    if (product != null) {
      bloom.put(product)
    }
  }
}
package pipeline

import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.streaming.StreamingQuery

object ConsoleSink {

  def writeToConsole(df: DataFrame, checkpoint: String): StreamingQuery = {
    df.writeStream
      .outputMode("append")
      .option("checkpointLocation", checkpoint)
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>
        println(s"\n======= Batch: $batchId =======")

        batchDF.foreachPartition { it: Iterator[Row] =>
          it.foreach { row =>
            println(row.mkString(" | "))
          }
        }
      }
      .start()
  }
}
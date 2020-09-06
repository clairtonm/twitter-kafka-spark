import org.apache.spark._
import org.apache.spark.streaming._

object SparkStreamingApp extends App {

  val conf = new SparkConf().setMaster("local[2]").setAppName("SparkStreamingTwitter")
  val ssc = new StreamingContext(conf, Seconds(1))


}
import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Level, Logger}

object SparkStreamingApp extends App {

  LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).
    asInstanceOf[Logger].setLevel(Level.ERROR)

  val topic = "UCL_tweets"
  val conf = new SparkConf().setMaster("local[2]").setAppName("SparkStreamingTwitter")
  val spark = SparkSession.builder().config(conf).getOrCreate()
  import spark.implicits._

  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", topic)
    .option("startingOffsets", "latest")
    .load()

  val query = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    .as[(String, String)]
    .groupBy("value").count()
    .writeStream
    .format("console")
    .outputMode("complete")
    .start()

  query.awaitTermination()
}
import java.io.{BufferedReader, File, FileReader}
import java.util.{Properties, stream}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Level, Logger}

object TwitterSimulator extends App {

  LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).
    asInstanceOf[Logger].setLevel(Level.DEBUG)

  val path = "UCL.json"
  val topic = "UCL_tweets"

  def getTweets(path: String): stream.Stream[String] = {
    val file = new File(path)
    val br = new BufferedReader(new FileReader(file))

    br.lines()
  }

  def producerTweets(topic: String): Unit ={
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("ack", "all")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    try {
      val lines = getTweets(path)
      lines.forEach(line => {
        producer.send(new ProducerRecord[String, String](topic,  "UCL", line))
      })

    } catch {
      case e: Exception => println(e)
    }

    producer.close()
  }

  producerTweets(topic)
}

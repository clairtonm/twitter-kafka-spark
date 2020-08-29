import java.io.{BufferedWriter, File, FileWriter}

import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}
import com.typesafe.scalalogging.{LazyLogging, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object KafkaApp extends App with LazyLogging {

  def main(): Unit ={

    val consumerToken = new ConsumerToken("oljfafa14fqd", "asdfafa f")
    val accessToken = new AccessToken("asdf134123412a", "adfadfa")

    val restClient = TwitterRestClient(consumerToken, accessToken)

    val query = "?q=UCL2"

//    val streamClient = TwitterStreamingClient(consumerToken, accessToken)

    // Saving Tweets on a File
    def writeToFile(text: String): Unit ={
      val file = new File("UCL.json")
      val bw = new BufferedWriter(new FileWriter(file, true))

      bw.write(text)
      bw.newLine()
      bw.close()
    }

    def printAndSaveTwitter(tweet: Tweet): Unit = {

      // place and user could be empty, then let`s treat them to get country and user values
      val (country, countryCode, placeName, placeType) = if (tweet.place.isDefined) {
        (tweet.place.get.country, tweet.place.get.country_code, tweet.place.get.name,
          tweet.place.get.place_type)
      } else {
        ("Country Not Found!", "Country Code Not Found!", "Place Name Not Found!", "Place Type Not Found!")
      }
      val lang = tweet.lang.getOrElse("Language Not Found!")
      val full_text = if (tweet.extended_tweet.isDefined) {
        tweet.extended_tweet.get.full_text.replaceAll("\"", """\\"""")
      } else {
        "Full Text Not Found!"
      }
      val (userScreenName, verified, name) = if (tweet.user.isDefined) {
        (tweet.user.get.screen_name, tweet.user.get.verified, tweet.user.get.name)
      } else {
        ("Screen Name Not Found!", "Verified Not Found!", "User Name Not Found!")
      }
      val tweetTexFormatted = tweet.text.replaceAll("\"", """\\"""")

      //Not the best practice
      val tweetJsonString = s"{" +
        s"""user_screen_name: "$userScreenName",""" +
        s"""user_name: "$name",""" +
        s"""user_verified: "$verified",""" +
        s"""country_code: "$countryCode",""" +
        s"""country: "$country",""" +
        s"""place_name: "$placeName",""" +
        s"""place_type: "$placeType",""" +
        s"""text: "$tweetTexFormatted",""" +
        s"""retweet_count: ${tweet.retweet_count},""" +
        s"""favorite_count: ${tweet.favorite_count},""" +
        s"""lang: ${lang},""" +
        s"""full_text: "${full_text}" """ +
        s"}".stripMargin

      val tweetJsonFormatted = tweetJsonString.replaceAll("\n", " ").replaceAll("'", "\'")
      println(tweetJsonFormatted)

      try {
        writeToFile(tweetJsonFormatted)
      } catch {
        case e: Exception => {
          println(e)
          println(e.getStackTrace)
          logger.error(e.getMessage)
          logger.error(e.getStackTrace.toString)
        }
      }
    }

    def searchTweets(query: String, maxId: Option[Long] = None): Future[Seq[Tweet]] = {
      def extractNextMaxId(params: Option[String]): Option[Long] = {
        //example: "?max_id=658200158442790911&q=%23scala&include_entities=1&result_type=mixed"
        params.getOrElse("").split("&").find(_.contains("max_id")).map(_.split("=")(1).toLong)
      }
      try {

        restClient.searchTweet(query, count=100).flatMap { ratedData =>
          val result = ratedData.data
          val limit = ratedData.rate_limit
          logger.info(limit.remaining.toString)
          val nextMaxID = extractNextMaxId(result.search_metadata.next_results)

          val tweets = result.statuses
          if (tweets.nonEmpty) searchTweets(query, nextMaxID).map(_ ++ tweets)
          else Future(tweets.sortBy((_.created_at)))
        } recover { case _ => Seq.empty}
      }
    }

    searchTweets(query).map { tweets =>
      tweets.map(tweet => printAndSaveTwitter(tweet))
    }
  }

  main()

}

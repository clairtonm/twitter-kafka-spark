import java.io.{BufferedWriter, File, FileWriter}

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}

import scala.concurrent.ExecutionContext.Implicits.global

object KafkaApp extends App {

  def main(): Unit ={

    // Tokens
    val consumerToken = ConsumerToken(key="", secret="")
    val accessToken = AccessToken(key="378933214-", secret="")

    val streamClient = TwitterStreamingClient(consumerToken, accessToken)

    // Saving Tweets on a File
    val file = new File("tweetsCblol.txt")
    val bw = new BufferedWriter(new FileWriter(file))

    def printAndSaveTwitter(tweet: Tweet): Unit ={

      // place and user could be empty, then let`s treat them to get country and user values
      val (country, countryCode, placeName, placeType) = if (tweet.place.isDefined) {
        (tweet.place.get.country, tweet.place.get.country_code, tweet.place.get.name,
        tweet.place.get.place_type)
      } else {
        ("Country Not Found!", "Country Code Not Found!", "Place Name Not Found!", "Place Type Not Found!")
      }

      val (userScreenName, verified, name) = if (tweet.user.isDefined) {
        (tweet.user.get.screen_name, tweet.user.get.verified, tweet.user.get.name)
      } else {
        ("Screen Name Not Found!", "Verified Not Found!", "User Name Not Found!")
      }

      //Not the best practice
      val tweetJsonString = s"{" +
                              s"user_screen_name: '$userScreenName'" +
                              s"user_name: '$name'," +
                              s"user_verified: '$verified'," +
                              s"country_code: '$countryCode'," +
                              s"country: '$country'," +
                              s"place_name: '$placeName'," +
                              s"place_type: '$placeType'," +
                              s"text: '${tweet.text}'," +
                              s"retweet_count: ${tweet.retweet_count}," +
                              s"favorite_count: ${tweet.favorite_count}" +
                          s"}"


      println(tweetJsonString)
      bw.write(tweetJsonString)
      bw.close()
    }

    try {
      streamClient.filterStatuses(tracks = Seq("yakisoba")) {
        case tweet: Tweet => printAndSaveTwitter(tweet)
      }
    } catch {
      case e: Exception => {
        println(e)
        bw.close()
      }
    }
  }

  main()

}

package ch.uzh.ifi.mamato.crowdSA.hcomp.crowdsa

import ch.uzh.ifi.mamato.crowdSA.model.{Answer, Question}
import ch.uzh.ifi.mamato.crowdSA.persistence.QuestionDAO
import ch.uzh.ifi.mamato.crowdSA.util.{HttpRestClient, LazyLogger}
import java.util.Date
import ch.uzh.ifi.pdeboer.pplib.hcomp.{HCompAnswer, HCompQueryProperties, HCompQuery}
import ch.uzh.ifi.pdeboer.pplib.util.GrowingTimer
import com.typesafe.config.ConfigFactory
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.message.BasicNameValuePair
import org.apache.http.{NameValuePair, HttpEntity}
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.util.EntityUtils
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone, Seconds}

import scala.collection
import scala.collection.parallel.mutable
import scala.concurrent.duration._

/**
 * Created by Mattia on 20.01.2015.
 */
class CrowdSAManager(val service: CrowdSAService, val qu: CrowdSAQuery) extends LazyLogger {

  var questionId : Long= 0
  var cancelled: Boolean = false

  def waitForResponse() : Option[Answer] = {
    def durationIn(unit: TimeUnit): FiniteDuration = {
      durationIn(SECONDS)
    }

    var answer: Option[Answer] = None
    try {
      (1 to 100000).view.foreach(i => {
        Thread.sleep(ConfigFactory.load("application.conf").getInt("pollTimeMS"))
        answer = poll()
        if (cancelled || answer.isDefined){
          throw new Exception("I'm actually not an Exception")
        }
      })
    }
    catch {
      case e: Exception => {
        /*hopefully we land here*/
      }
    }
    answer
  }

  /**POST a question to the server
   * @return question id
   */
  def createQuestion() : Long = {
    questionId = service.CreateQuestion(qu)
    questionId
  }

  def cancelQuestion(): Unit = {
    service.DisableQuestion(questionId)
    cancelled = true
  }

  def poll(): Option[Answer] = {
		val answers = service.GetAnswersForQuestion(questionId)
		answers.lastOption match {
      case None => None
      case Some(a: Answer) => handleAnswerResult(a)
		}
	}

  def handleAnswerResult(a: Answer): Option[Answer] = {
    logger.debug("Got an answer: " + a.answer)
    try {
      //We approve all NON EMPTY answers by default.
      if(a.answer != null && a.answer!="") {
        service.ApproveAnswer(a)

      } else {
        service.RejectAnswer(a)
      }

      a.acceptTime = Option(new DateTime(new Date(service.getAssignmentForAnswerId(a.id).created_at)))
      a.submitTime = Option(new DateTime(new Date(a.created_at)))

      Some(a)
    }
    catch {
      case e: Exception => {
        logger.error("could not approve assignment", e)
        None
      }
    }
  }

}

package ch.uzh.ifi.mamato.crowdSA.util

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

trait LazyLogger {
  @transient protected lazy val logger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName))
}
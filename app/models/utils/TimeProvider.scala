package models.utils

import java.util.Date

/**
 * @author kamekoopa
 */
object TimeProvider {

  implicit def now: NowProvider = {
    new NowProvider {
      def now(): Date = new Date()
    }
  }

  trait NowProvider {
    def now(): Date
  }
}


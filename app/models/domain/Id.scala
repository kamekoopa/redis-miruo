package models.domain

import scala.util.Random
import java.security.MessageDigest

/**
 * @author kamekoopa
 */
case class Id(underlying: String) {
  def get = underlying
}

object Id {

  def generate = {

    val now = System.currentTimeMillis().toString
    val random = Random.alphanumeric.take(Random.nextInt(256)).mkString

    val digest = MessageDigest.getInstance("SHA-512")
      .digest( (now+random).getBytes )
      .map(b => b & 0xFF)
      .map(i => i.toHexString)
      .mkString

    new Id(digest)
  }
}

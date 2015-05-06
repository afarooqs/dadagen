package org.inosion.datdom

import org.inosion.datdom.randomtypes.{RegexGenerator, GenderGenerator, TitleGenerator}
import org.scalatest.{Matchers, FlatSpec}

/**
 * @author rbuckland
 */
class RegexGeneratorTest extends FlatSpec with Matchers {
  "Supplying a Regular Expression" should "generate data that matches" in {
    val emailRegexp = """my\.test\.email\.[a-f]{6}\.[0-9]{10}\@foo\.com"""
    val ramondom = SeqOfSeqOfStringsGenerator(100,List(
      TitleGenerator("title",Some("gender")),
      GenderGenerator("gender"),
      RegexGenerator("email_address",emailRegexp)
    ))

    val result = ramondom.generate()
    for (row <- result._2) {
      // look in the 3rd element at the email. :: eg my.test.email.ddbcda.4828430423@foo.com
      emailRegexp.r.findFirstIn(row(2)).isDefined should be (true)
    }
    println("regexp generator --> " + result._2.head)
  }
}


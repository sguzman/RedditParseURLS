package com.github.sguman.scala.reddit

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._

import scalaj.http.Http

object Main {
  def http(url: String) =
    Http(url).asString

  def doc(str: String) = JsoupBrowser().parseString(str)

  def main(args: Array[String]): Unit = {
    val host = "https://www.reddit.com"
    val url = s"$host/r/gadgets/comments/82qx88/amazon_admits_alexa_is_creepily_laughing_at/"
    doc(http(url).body)
      .>>(elementList("a[href]"))
      .map(_.attr("href"))
      .filter(!_.startsWith("javascript"))
      .map(t => if (t.startsWith("/")) s"$host$t" else t)
      .map(t => if (t.startsWith("#")) s"$url$t" else t) foreach println
  }
}

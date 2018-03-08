package com.github.sguman.scala.reddit

import java.net.URL

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import com.github.sguzman.scala.html.Config

import scalaj.http.Http

object Main {
  def http(url: URL) = Http(url.toString).asString
  def doc(str: String) = JsoupBrowser().parseString(str)
  def argParse(arg: Array[String]) = url(new URL(Config().parse(arg, Cmd()).get.url))
  def slash(u: URL, path: String) = s"${u.getProtocol}://${u.getAuthority}$path"
  def hash(u: URL, hashy: String) = s"${u.getProtocol}://${u.getAuthority}${u.getPath}$hashy"

  def url(u: URL) = doc(http(u).body)
    .>>(elementList("a[href]"))
    .map(_.attr("href"))
    .filter(!_.startsWith("javascript"))
    .map(t => if (t.startsWith("/")) slash(u, t) else t)
    .map(t => if (t.startsWith("#")) hash(u, t) else t)

  def links(l: List[String]) = {
    val items = l map {
      case s: String if s.startsWith("https://www.reddit.com/r/") => (1, s)
      case t: String if t.startsWith("https://www.reddit.com/user/") => (2, t)
      case u: String => (3, u)
    }
    (items filter (_._1 == 1) map (_._2), items filter (_._1 == 2) map (_._2), items filter (_._1 == 3) map (_._2))
  }

  def main(args: Array[String]): Unit =
    println(links(argParse(args)))
}

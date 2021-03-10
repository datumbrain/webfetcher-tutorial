package com.datumbrain.webfetcher

import java.io.File
import java.io.PrintWriter

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.json._
import play.api.libs.json.Writes._
import scala.collection.JavaConversions._

import org.joda.time.DateTime

object MainClass {

	def main(args: Array[String]): Unit = {

		if (args.size != 1) {
			println("Invalid number of arguments..")
		  System.exit(1)
		}

		try {
			//Use Jsoup to connect
			println("Connecting to `"+args(0)+"`..")
			val doc: Document = Jsoup.connect(args(0)).get

			val curdate = DateTime.now()
			val folder_fmt = "yyyy-MM-dd-hh-mm"
			val crawl_fmt = "yyyy-MM-dd HH:mm:ss.S"
		  	val crawl_date = curdate.toString(crawl_fmt)
		  
			// Setup directory
			var folder = "./output"
			println("Checking output path..")
			val out_dir = new File(folder)
			if (!out_dir.exists())
				out_dir.mkdir();
			folder = folder + "/" + curdate.toString(folder_fmt)
			val cur_dir = new File(folder)
			if (!cur_dir.exists())
				cur_dir.mkdir();


			// Links
			println("Parsing links..")
			val a = doc.select("a")
			val domain = getDomain(args(0))

			def inboundLinks = for {
				link <- a
				if (hasDomain(link.attr("href")) && compareDomains(getDomain(link.attr("href")), domain))
			} yield link
			def outboundLinks = for {
				link <- a
				if (hasDomain(link.attr("href")) && !compareDomains(getDomain(link.attr("href")), domain))
			} yield link


			// Write .url file
			println("Creating output.url..")
			val urlFile = new File(folder + "/output.url")
			val urlWriter = new PrintWriter(urlFile)
			//urlWriter.write("[InternetShortcut]\n")
			//urlWriter.write("URL="+ args(0) +"\n")
			urlWriter.write(args(0))
			urlWriter.close()


			// Write .json file
			println("Creating output.json..")
			val jsonFile = new File(folder + "/output.json")
			val jsonWriter = new PrintWriter(jsonFile)

			val jsonObject = Json.obj(
				"time_of_crawl"  -> crawl_date,
				"page_url" -> args(0),
				"inbound_links" -> inboundLinks.size,
				"outbound_links" -> outboundLinks.size,
				"content" -> doc.toString
			)

			jsonWriter.write(jsonObject.toString())
			jsonWriter.close()

		  	println("All done..!\n")

		} catch {
			case ex: java.net.UnknownHostException => {
				println("Error: cannot connect to `"+args(0)+"`")
				System.exit(1)
			}
			case ex: javax.net.ssl.SSLException => {
				println("Error: cannot connect to `"+args(0)+"`")
				System.exit(1)
			}
			case ex: javax.net.ssl.SSLHandshakeException => {
				println("Error: connection to `"+args(0)+"` was reset..")
				System.exit(1)
			}

		}

	}


	def hasDomain(url: String) = (url.split("//").length == 2)

	def getDomain(url: String) = url.split("//")(1).split("/")(0)

	def cleanURL(url: String) = url.replaceAll("[^a-zA-Z0-9 ]", "")

	def printElementsTextWithTag(doc: org.jsoup.nodes.Document, tag: String, file: PrintWriter) =  {

		val arr = doc.select(tag)

		file.write("<h3>"+tag+" (" + arr.size + ")</h3><ul>")
		for (e <- arr) file.write("<li>" + e.text() + "</li>\n")
		file.write("</ul>")
	}

	def compareDomains(dom1: String, dom2: String): Boolean = {
		if (dom1==dom2)
			true
		else
		{
			val ar1 = dom1.split("\\.")
			val ar2 = dom2.split("\\.")
			/*
			println(dom1)
			println(ar1(0)+"\t"+ar1(1)+"\t"+ar1(2))
			println(dom2)
			println(ar2(0)+"\t"+ar2(1)+"\t"+ar2(2))
			*/
			if (ar1.size==3 && ar2.size ==3 && ar1(1) == ar2(1) && ar1(2) == ar2(2))
				true
			else
				false
		}
	}
}

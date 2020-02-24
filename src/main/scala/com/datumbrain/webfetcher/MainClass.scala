package com.datumbrain.webfetcher
import java.io.File
import java.io.PrintWriter

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.collection.JavaConversions._

object MainClass {

	def main(args: Array[String]): Unit = {

		if (args.size != 1)
			println("Invalid number of arguments..")

		try {
			//Use Jsoup to connect
			println("Connecting to `"+args(0)+"`..")
			val doc: Document = Jsoup.connect(args(0)).get


			// Setup directory
			println("Checking output path..")
			val out_dir = new File("./output")
			if (!out_dir.exists())
				out_dir.mkdir();
			val cur_dir = new File("./output/" + cleanURL(args(0)))
			if (!cur_dir.exists())
				cur_dir.mkdir();


			// Write .url file
			println("Creating output.url..")
			val urlFile = new File("output/" + cleanURL(args(0)) + "/output.url")
			val urlWriter = new PrintWriter(urlFile)
			//urlWriter.write("[InternetShortcut]\n")
			//urlWriter.write("URL="+ args(0) +"\n")
			urlWriter.write(args(0))
			urlWriter.close()


			// Write .html file
			println("Creating output.html..")
			val htmlFile = new File("output/" + cleanURL(args(0)) + "/output.html")
			val htmlWriter = new PrintWriter(htmlFile)


			htmlWriter.write(
				s"""<html>
				  |<head>
				  |	<title>Web Fetcher Results</title>
				  |</head>
				  |<body>
				  |	<h1>Datum Brain — Web Fetcher</h1>
				  | <b>${doc.title()}</b><br/>
				  |	<small>${args(0)}</small><br/>
				  | <br/>""".stripMargin)

			// Headings
			println("Parsing headings..")
			printElementsTextWithTag(doc,"h1",htmlWriter)
			printElementsTextWithTag(doc,"h2",htmlWriter)
			printElementsTextWithTag(doc,"h3",htmlWriter)


			// Links
			println("Parsing links..")
			val a = doc.select("a")
			htmlWriter.write("<h3>Links (" + a.size + ")</h3><ul>")
			val domain = getDomain(args(0))

			def inboundLinks = for {
				link <- a
				if (hasDomain(link.attr("href")) && compareDomains(getDomain(link.attr("href")), domain))
			} yield link
			def outboundLinks = for {
				link <- a
				if (hasDomain(link.attr("href")) && !compareDomains(getDomain(link.attr("href")), domain))
			} yield link

			println("Parsing inbound links..")
			htmlWriter.write("<h3>Inbound (" + inboundLinks.size + ")</h3><ul>")
			for (e <- inboundLinks) htmlWriter.write("<li><b>" + e.text() + "</b> ( <u><i>" + e.attr("href") + "</i></u> )</li>\n")
			htmlWriter.write("</ul>")

			println("Parsing outbound links..")
			htmlWriter.write("<h3>Outbound (" + outboundLinks.size + ")</h3><ul>")
			for (e <- outboundLinks) htmlWriter.write("<li><b>" + e.text() + "</b> ( <u><i>" + e.attr("href") + "</i></u> )</li>\n")
			htmlWriter.write("</ul>")



			// Images
			println("Parsing images..")
			val img = doc.select("img")
			htmlWriter.write("<h3>Images (" + img.size + ")</h3><ul>")
			for (e <- img) htmlWriter.write("<li>" + e.attr("src") + "</li>\n")
			htmlWriter.write("</ul>")


			htmlWriter.write("</body></html>")

			htmlWriter.close()

			println("All done..!")

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

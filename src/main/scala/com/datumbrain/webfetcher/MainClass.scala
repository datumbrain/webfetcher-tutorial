package com.datumbrain.webfetcher

object MainClass {

	def main(args : Array[String]): Unit = {

		if (args.size != 1)
			println("Invalid number of arguments..")

		import org.jsoup.Jsoup
		import org.jsoup.nodes.Document
		val doc: Document = Jsoup.connect(args(0)).get

		//println(doc.title)
		val titles = doc.getElementsByClass("LC20lb")

		import java.io.File
		import java.io.PrintWriter


		val out_dir = new File("./output")
		if (! out_dir.exists())
			out_dir.mkdir();
		val cur_dir = new File("./output/" + cleanURL(args(0)))
		if (! cur_dir.exists())
			cur_dir.mkdir();
		val htmlFile = new File("output/" + cleanURL(args(0)) + "/output.html")
		val htmlWriter = new PrintWriter(htmlFile)
		import scala.collection.JavaConversions._
		for (title <- titles) {
			htmlWriter.write(title.text() + "<br />\n")
		}
		htmlWriter.close()

		val urlFile = new File("output/" + cleanURL(args(0)) + "/output.url")
		val urlWriter = new PrintWriter(urlFile)
		urlWriter.write("[InternetShortcut]\n")
		urlWriter.write("URL="+ args(0) +"\n")
		urlWriter.close()

	}


	def cleanURL(url: String):String = {
		val s = url.replaceAll("[^a-zA-Z0-9 ]", "")
		s
	};
}

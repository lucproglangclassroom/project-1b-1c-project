package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods}
import scala.io.Source

object Main:

  @main
  def run(
           @arg(short = 'c') cloudSize: Int = 10,
           @arg(short = 'l') minLength: Int = 6,
           @arg(short = 'w') windowSize: Int = 1000
         ): Unit =

    System.err.println(s"cloudSize=$cloudSize minLength=$minLength windowSize=$windowSize")

    val consoleOutput = new OutputHandler:
      def handleOutput(topWords: Seq[(String, Int)]): Unit =
        val cloud = topWords
          .map { case (w, f) => s"$w: $f" }
          .mkString(" ")

        try
          println(cloud)
        catch
          case _: java.io.IOException =>
            System.exit(0)

    val processor = new WordProcessor(cloudSize, minLength, windowSize, consoleOutput)

    val wordPattern = """[\p{Alpha}0-9']+""".r
    val lines = Source.fromInputStream(System.in).getLines()

    for line <- lines do
      val words = wordPattern.findAllIn(line)
      for word <- words do
        processor.processWord(word)

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args.toSeq)

end Main
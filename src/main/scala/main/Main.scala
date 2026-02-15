package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods}
import scala.collection.mutable

object Main:

  @main
  def run(
    @arg(short = 'c') cloudSize: Int = 10,
    @arg(short = 'l') minLength: Int = 6,
    @arg(short = 'w') windowSize: Int = 1000
  ): Unit =

    // 1. Read input words using the regex from the project hints
    val words = scala.io.Source.stdin.getLines.flatMap { line =>
      import scala.language.unsafeNulls
      line.split("(?U)[^\\p{Alpha}0-9']+").filter(_.length >= minLength)
    }

    // 2. Setup the sliding window (The "Filter" for constant space)
    val window = mutable.Queue[String]()
    val counts = mutable.Map[String, Int]().withDefaultValue(0)

    var count = 0
    words.foreach { word =>
      window.enqueue(word)
      counts(word) += 1
      count += 1

      // 3. Keep memory usage constant by dropping old words
      if window.size > windowSize then
        val removed = window.dequeue()
        counts(removed) -= 1
        if counts(removed) <= 0 then counts.remove(removed)

      // 4. Print the cloud only after we have enough words
      if count >= windowSize then
        val cloud = counts.toSeq
          .sortBy { case (w, f) => (-f, w) } // Sort by frequency
          .take(cloudSize)
          .map((w, f) => s"$w: $f")
          .mkString(" ")

        // This try/catch handles the SIGPIPE requirement
        try println(cloud)
        catch case _: java.io.IOException => System.exit(0)
    }

  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args.toSeq)
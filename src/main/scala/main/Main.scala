package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods}
import scala.io.Source

object Main extends PureWordCounter:

  @main
  def run(
           @arg(short = 'c') cloudSize: Int = 10,
           @arg(short = 'l') minLength: Int = 6,
           @arg(short = 'w') windowSize: Int = 1000
         ): Unit =

    System.err.println(s"cloudSize=$cloudSize minLength=$minLength windowSize=$windowSize")

    // Read and filter words
    val wordPattern = """[\p{Alpha}0-9']+""".r
    val lines = Source.fromInputStream(System.in).getLines()
    val words = lines.flatMap(line => wordPattern.findAllIn(line))
      .filter(_.length >= minLength)

    // Use scanLeft 
    val initialState = State(Vector.empty, Map.empty)
    val states = words.scanLeft(initialState) { (state, word) =>
      updateState(state, word, windowSize)
    }

    
    states.drop(1).zipWithIndex.foreach { case (state, index) =>
      // Only output after window is full
      if index + 1 >= windowSize then
        val topWords = state.counts.toSeq
          .sortBy { case (w, f) => (-f, w) }
          .take(cloudSize)

        val cloud = topWords
          .map { case (w, f) => s"$w: $f" }
          .mkString(" ")

        try
          println(cloud)
        catch
          case _: java.io.IOException =>
            System.exit(0)
    }

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args.toSeq)

end Main
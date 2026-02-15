User: yo i'm starting this scala project 'topwords'. i got this starter repo but its just an 'echo' example and i need to change it to a Pipes and Filters thing for a word cloud. can u help me setup Main.scala using mainargs? i need it to take 3 args: cloud-size, length-at-least, and window-size. default should be 10, 6, 1000.

AI: Definitely. Since your build.sbt already has mainargs, we can set up the entry point using the @main and @arg annotations. This replaces the old "Echo" logic with a proper CLI.

Scala
package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods}

object Main:
@main
def run(@arg(short = 'c', name = "cloud-size") cloudSize: Int = 10,
@arg(short = 'l', name = "length-at-least") minLength: Int = 6,
@arg(short = 'w', name = "window-size") windowSize: Int = 1000): Unit =
println(s"Config: c=$cloudSize, l=$minLength, w=$windowSize")

def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args.toSeq)
User: ok cool that makes sense for the args. now for the 'Pipe' part. i gotta get words from stdin. the hints say use scala.io.Source.stdin.getLines. how do i turn those lines into just words? i got this regex (?U)[^\\p{Alpha}0-9']+ from the instructions... how do i use it?

AI: You can use flatMap on the lines iterator. This treats the input as a stream, which is the "Pipe" part of the architecture. You'll split each line using that regex and filter by minLength.

User: wait i tried that but i'm getting an error like 'End of line expected' or sumthin. i think its the indentation cuz we're using scala 3 without the curly braces. can u show me exactly how that flatMap looks so i dont mess up the formatting again?

AI: Scala 3 is very picky about spaces. Ensure the .flatMap and .filter are indented exactly one level under the val.

Scala
val lines = scala.io.Source.stdin.getLines
val words = lines.flatMap: line =>
import scala.language.unsafeNulls
line.split("(?U)[^\\p{Alpha}0-9']+")
.filter(_.length >= minLength)
User: k that fixed it. now the big thing is 'scalability'. teacher says it has to run in 'constant space' cuz the input might be massive. if i just keep adding words to a list it'll crash on a huge file like Les Mis. how do i do a 'moving window' so i only keep the last n words?

AI: To keep "Constant Space," you need a data structure that discards the oldest word every time a new one enters after reaching the limit. This ensures memory usage never exceeds your windowSize.

User: wait im confused... do i use a List for the window? in java i remember LinkedList is okay for adding/removing, but i gotta count frequencies too. is there a better way in scala? maybe like a Queue or Map?

AI: Good catch. A List is slow for this. In Scala, a mutable.Queue is perfect for the "First-In, First-Out" window logic. You should pair it with a mutable.Map to track frequencies so you don't have to re-scan the whole window every time.

User: ight lets go with a mutable.Queue for the window and a mutable.Map for the counts. help me write the logic... every time a word comes in, i add it to the queue/map, and if the queue is too big i 'dequeue' the oldest one and decrease the count in the map. has to be super memory efficient.

AI: Here is that logic. We use withDefaultValue(0) on the map to make incrementing easy:

Scala
val window = scala.collection.mutable.Queue[String]()
val counts = scala.collection.mutable.Map[String, Int]().withDefaultValue(0)

    words.foreach: word =>
      window.enqueue(word)
      counts(word) += 1
      
      if window.size > windowSize then
        val oldest = window.dequeue()
        counts(oldest) -= 1
        if counts(oldest) <= 0 then counts.remove(oldest)
User: logic is there, now i need the 'word cloud'. it has to be top words by frequency. if its a tie it should be alphabetical. how do i sort a Map like that and format it to one line like word: frequency word2: frequency2?

AI: You can convert the map to a sequence and use sortBy. To handle ties, we pass a tuple to the sort function:

Scala
val topWords = counts.toSeq
.sortBy: (word, freq) =>
(-freq, word) // minus freq for descending, word for alphabetical
.take(cloudSize)
.map((w, f) => s"$w: $f")
.mkString(" ")
User: instructiosn say i gotta handle SIGPIPE on stdout so it dont crash if i use a command like head. how do i wrap the println to catch that I/O error? i dont wanna lose points on the 'dynamic requirements' part.

AI: You wrap the println in a try-catch. If the "filter" downstream (like head) stops listening, it throws an IOException. Catching it and exiting gracefully is exactly what the project requires.

User: i wanna push this 'core' to the repo now so my team has sumthin to work with. i built the engine, the CLI, and the memory stuff. can u give me the final Main.scala? keep the comments simple so they understand its a 'Filter' they can refactor later.

AI: Here is your complete, functional "core." It hits all the primary requirements: mainargs, the word-splitting regex, constant-space windowing, and SIGPIPE handling.

Scala
package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods}
import scala.collection.mutable

object Main:

@main
def run(
@arg(short = 'c', name = "cloud-size") cloudSize: Int = 10,
@arg(short = 'l', name = "length-at-least") minLength: Int = 6,
@arg(short = 'w', name = "window-size") windowSize: Int = 1000
): Unit =

    // Read input words using the suggested regex
    val lines = scala.io.Source.stdin.getLines
    val words = lines.flatMap: line =>
      import scala.language.unsafeNulls
      line.split("(?U)[^\\p{Alpha}0-9']+")
    .filter(_.length >= minLength)

    // Moving window logic for constant space scalability
    val window = mutable.Queue[String]()
    val counts = mutable.Map[String, Int]().withDefaultValue(0)

    var count = 0
    words.foreach: word =>
      window.enqueue(word)
      counts(word) += 1
      count += 1

      // Keep memory use constant
      if window.size > windowSize then
        val removed = window.dequeue()
        counts(removed) -= 1
        if counts(removed) <= 0 then counts.remove(removed)

      // Print cloud after we have a full window
      if count >= windowSize then
        val cloud = counts.toSeq
          .sortBy((w, f) => (-f, w))
          .take(cloudSize)
          .map((w, f) => s"$w: $f")
          .mkString(" ")
        
        try println(cloud)
        catch case _: java.io.IOException => System.exit(0)

def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args.toSeq)
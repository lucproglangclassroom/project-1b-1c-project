package edu.luc.cs.cs371.topwords

import scala.collection.mutable

class WordProcessor(
                     cloudSize: Int,
                     minLength: Int,
                     windowSize: Int,
                     output: OutputHandler
                   ):

  var window = mutable.Queue[String]()
  var counts = mutable.Map[String, Int]().withDefaultValue(0)
  var wordCount = 0

  def processWord(word: String): Unit =
    if word.length >= minLength then
      window.enqueue(word)
      counts(word) += 1
      wordCount += 1

      if window.size > windowSize then
        val removed = window.dequeue()
        counts(removed) -= 1
        if counts(removed) <= 0 then
          counts.remove(removed)

      if wordCount >= windowSize then
        val topWords = getTopWords()
        output.handleOutput(topWords)

  def getTopWords(): Seq[(String, Int)] =
    counts.toSeq
      .sortBy { case (w, f) => (-f, w) }
      .take(cloudSize)

  def getWordCount: Int = wordCount

  def getCurrentWindowSize: Int = window.size

end WordProcessor

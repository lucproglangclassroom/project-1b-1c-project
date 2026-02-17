package edu.luc.cs.cs371.topwords
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.mutable.ListBuffer

class WordProcessorTest extends AnyWordSpec:

  class TestOutputHandler extends OutputHandler:
    val outputs = ListBuffer[Seq[(String, Int)]]()

    def handleOutput(topWords: Seq[(String, Int)]): Unit =
      outputs += topWords

  "WordProcessor" when:
    "given words shorter than minLength" should:
      "ignore them" in:
        val handler = new TestOutputHandler()
        val processor = new WordProcessor(3, 4, 5, handler)

        processor.processWord("cat")
        processor.processWord("dog")
        processor.processWord("hello")
        processor.processWord("world")

        assert(processor.getWordCount == 2)

    "tracking word frequencies" should:
      "count each word correctly" in:
        val handler = new TestOutputHandler()
        val processor = new WordProcessor(3, 1, 10, handler)

        for i <- 1 to 10 do
          processor.processWord("hello")

        val outputs = handler.outputs.toList
        assert(outputs.nonEmpty)

        val lastOutput = outputs.last
        assert(lastOutput.exists { case (w, f) => w == "hello" && f == 10 })

    "maintaining sliding window" should:
      "evict old words when full" in:
        val handler = new TestOutputHandler()
        val processor = new WordProcessor(3, 1, 3, handler)

        processor.processWord("aaa")
        processor.processWord("bbb")
        processor.processWord("ccc")
        processor.processWord("ddd")

        val outputs = handler.outputs.toList
        assert(outputs.nonEmpty)

        val lastOutput = outputs.last
        assert(!lastOutput.exists { case (w, _) => w == "aaa" })

end WordProcessorTest
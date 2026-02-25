package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec

class PureWordCounterTest extends AnyWordSpec with PureWordCounter:

  "PureWordCounter" when:
    "adding words" should:
      "add first word" in:
        val state = State(Vector.empty, Map.empty)
        val result = updateState(state, "hello", 10)

        assert(result.window == Vector("hello"))
        assert(result.counts("hello") == 1)

      "add second word" in:
        val state1 = State(Vector.empty, Map.empty)
        val state2 = updateState(state1, "hello", 10)
        val state3 = updateState(state2, "world", 10)

        assert(state3.window == Vector("hello", "world"))
        assert(state3.counts("hello") == 1)
        assert(state3.counts("world") == 1)

      "count same word twice" in:
        val state1 = State(Vector.empty, Map.empty)
        val state2 = updateState(state1, "hello", 10)
        val state3 = updateState(state2, "hello", 10)

        assert(state3.counts("hello") == 2)

    "window is full" should:
      "remove oldest word" in:
        val state1 = State(Vector.empty, Map.empty)
        val state2 = updateState(state1, "aaa", 3)
        val state3 = updateState(state2, "bbb", 3)
        val state4 = updateState(state3, "ccc", 3)
        val state5 = updateState(state4, "ddd", 3)

        assert(state5.window.length == 3)
        assert(state5.window == Vector("bbb", "ccc", "ddd"))

end PureWordCounterTest
package edu.luc.cs.cs371.topwords

trait PureWordCounter:
  
  // immutable State at any given moment
  case class State(window: Vector[String], counts: Map[String, Int])

  // Takes the old state and a new word, returns a new state
  def updateState(state: State, newWord: String, windowSize: Int): State =
    // Create a new window and new counts with the added word
    val addedWindow = state.window :+ newWord
    val currentCount = state.counts.getOrElse(newWord, 0)
    val addedCounts = state.counts.updated(newWord, currentCount + 1)

    
    if addedWindow.length > windowSize then
      val removedWord = addedWindow.head
      val newWindow = addedWindow.tail
      val countAfterRemoval = addedCounts(removedWord) - 1
      
      
      val newCounts = if countAfterRemoval <= 0 then
        addedCounts - removedWord
      else
        addedCounts.updated(removedWord, countAfterRemoval)
        
      State(newWindow, newCounts)
    else
      State(addedWindow, addedCounts)
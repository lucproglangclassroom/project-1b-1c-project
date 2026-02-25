package edu.luc.cs.cs371.topwords

trait OutputHandler:
  def handleOutput(topWords: Seq[(String, Int)]): Unit




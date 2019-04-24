package viw

import viw.internals.State
import viw.internals.State.Position

object Utils {

  def concat(separator: String, ss: String*): String = ss filter (_.nonEmpty) mkString separator

  def countLines(text: String, state: State): Position = {

    val l = text.count(_ == '\n')
    if (l == 0) Position(state.position.line, state.position.character+text.length)
    else Position(state.position.line+l, text.length-1-text.lastIndexOf('\n'))

  }

  def removeSubstring(state: State, from:Int, to: Int, separator: String): String = {

    val left = state.content.take(from)
    val right = state.content.drop(to)

    if (right == "") concat("", left, separator)
    else if (left == "") concat("", separator, right)
    else concat(separator, left, right)
  }

}


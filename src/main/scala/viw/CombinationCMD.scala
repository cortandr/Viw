package viw
import viw.internals.State
import viw.internals.State.Position

import scala.util.matching.Regex
import viw.StateWrapper.Buffer

object CombinationCMD extends CMD {

  val change: Regex = raw"(d|c)([0-9]?+)([a-z])".r
  val indent: Regex = raw"(>|<)([0-9]?+)([a-z|>|<])".r
  val find: Regex = raw"(f)([a-z])".r
  val findPunct: Regex = raw"(f)([^a-zA-Z])".r
  val count: Regex = raw"([0-9]+)([a-z])".r
  val yank: Regex = raw"(y)([0-9]?+)([a-z])".r

  override def applyCmd(sw: StateWrapper, cmd: String): Option[StateWrapper] = {

    cmd match {
      case change("d", _, "d") => deleteOrChangeLine(sw, newMode = true)
      case change("d", c, movement) => deleteOrChange(movement, c, sw, newMode = true)
      case change("c", _, "c") => deleteOrChangeLine(sw, newMode = false)
      case change("c", c, movement) => deleteOrChange(movement, c, sw, newMode = false)
      case indent(ind, c, movement) => indent(ind, c, movement, sw)
      case find(_, key) => find(key, sw)
      case findPunct(_, key) => findPunct(key, sw)
      case count(c, movement) => Option(repeatMove(movement, c.toInt, sw))
      case yank(_, _, "y") => copyLine(sw)
      case yank(_, c, movement) => copy(movement, c, sw)
      case _ => Option(sw)
    }
  }

  def copyLine(sw: StateWrapper): Option[StateWrapper] = {

    val newBuffer = Buffer(sw.buffer.cmdStack, sw.state.contentLines(sw.state.position.line), sw.buffer.commandBuffer)
    Option(StateWrapper(sw.prevState, sw.state, newBuffer, sw.visual))

  }

  def copy(move: String, c: String, sw: StateWrapper): Option[StateWrapper] = {

    // Apply movement to get deletion boundary
    val newSw = repeatMove(move, if (c == "") 1 else c.toInt, sw)

    // Current line index in content
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val newLineIdx = newSw.state.content.indexOf(sw.state.contentLines(newSw.state.position.line))

    // Build new content by removing line
    val from = currLineIdx + sw.state.position.character
    val to = newLineIdx + newSw.state.position.character
    val newBuffer = {
      if (from < to)
        Buffer(sw.buffer.cmdStack, sw.state.content.substring(Math.min(from, to), Math.max(from, to)), sw.buffer.commandBuffer)
       else
        Buffer(sw.buffer.cmdStack, sw.state.content.substring(to+1, from+1), sw.buffer.commandBuffer)
    }

    Option(StateWrapper(sw.prevState, sw.state, newBuffer, sw.visual))
  }

  def repeatMove(cmd: String, c: Int, sw: StateWrapper): StateWrapper = {

    if (c == 0) return sw

    val nextState = MovementCMD.applyCmd(sw, cmd).get

    repeatMove(cmd, c-1, nextState)
  }

  def indent(ind: String, c: String, mov: String, sw: StateWrapper): Option[StateWrapper] = {

    val indentation = if (c == "") "  " else (for (_ <- 0 to c.toInt) yield "  ").mkString("")

    // Apply move and get new line
    val newSw = {
      mov match {
        case ">" => MovementCMD.applyCmd(sw, "l")
        case "<" => MovementCMD.applyCmd(sw, "h")
        case _ => MovementCMD.applyCmd(sw, mov)
      }
    }

    // Assess whether next position is less or greater than current
    val forward = sw.state.position.line <= newSw.get.state.position.line

    // Build new content
    val newContent = {
      if (newSw.get.state.position.line == sw.state.position.line) {
        val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
        Utils.removeSubstring(
          sw.state,
          currLineIdx,
          if (ind == ">") currLineIdx else currLineIdx+indentation.length,
          if (ind == ">") indentation else "")
      } else {
        applyIndent(sw.state, sw.state.position.line, newSw.get.state.position.line, indentation, ind, if (forward) 1 else -1)
      }
    }

    val newPos = Position(sw.state.position.line, sw.state.position.character + (indentation.length * (if(ind == ">") 1 else -1)))

    val newState = State(newContent, newPos, newSw.get.state.selection, newSw.get.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def applyIndent(state: State, curr: Int, end: Int, ind: String, key: String, step: Int): String = {

    val currLineIdx = state.content.indexOf(state.contentLines(curr))
    val newContent =
      Utils.removeSubstring(
        state,
        currLineIdx,
        if (key == ">") currLineIdx else currLineIdx+ind.length,
        if (key == ">") ind else "")
    val newState = State(newContent, state.position, state.selection, state.mode)

    if (curr == end) return newContent

    applyIndent(newState, curr+step, end, ind, key, step)

  }

  def find(key:String, sw: StateWrapper): Option[StateWrapper] = {

    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val fromCursor = sw.state.content.drop(currLineIdx + sw.state.position.character)

    // Tokenize text
    val words = fromCursor.split("\\W").filter(_ != "").drop(1)

    val word = for(w <- words if w(0).toString == key) yield w

    // Get first first match idx of key in state content
    val wordIdx = fromCursor.indexOf(word(0))
    if (wordIdx == -1) return Option(sw)

    // Get new position
    val newPos = Utils.countLines(fromCursor.take(wordIdx), sw.state)

    val newState = State(sw.state.content, newPos, sw.state.selection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def findPunct(key: String, sw: StateWrapper): Option[StateWrapper] = {

    // Get current idx in state content
    val currIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line)) + sw.state.position.character
    val fromCursor = sw.state.content.drop(currIdx)

    // Get first first match idx of key in state content
    val keyIdx = fromCursor.indexOf(key)
    if (keyIdx == -1) return Option(sw)

    // Get new position
    val newPos = Utils.countLines(fromCursor.take(keyIdx), sw.state)

    val newState = State(sw.state.content, newPos, sw.state.selection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def deleteOrChangeLine(sw: StateWrapper, newMode: Boolean): Option[StateWrapper] = {

    // Get current and next line indexes in state content
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val nextLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line+1))

    // Build new content by removing line
    val newContent = Utils.removeSubstring(sw.state, currLineIdx, nextLineIdx, "")

    // Update buffer
    val newBuffer = Buffer(sw.buffer.cmdStack, sw.state.content.substring(currLineIdx, nextLineIdx), sw.buffer.commandBuffer)

    val newState = State(newContent, Position(sw.state.position.line, 0), sw.state.selection, newMode)

    Option(StateWrapper(sw.prevState, newState, newBuffer, sw.visual))
  }

  def deleteOrChange(cmd: String, c: String, sw: StateWrapper, newMode: Boolean): Option[StateWrapper] = {

    // Apply movement to get deletion boundary
    val newSw = repeatMove(cmd, if (c == "") 1 else c.toInt, sw)

    // Current line index in content
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val newLineIdx = newSw.state.content.indexOf(sw.state.contentLines(newSw.state.position.line))

    // Build new content by removing line
    val from = currLineIdx + sw.state.position.character
    val to = newLineIdx + newSw.state.position.character
    val (newContent, newBuffer) = {
      if (from < to) {
        (
          Utils.removeSubstring(sw.state, from, to, ""),
          Buffer(sw.buffer.cmdStack, sw.state.content.substring(Math.min(from, to), Math.max(from, to)), sw.buffer.commandBuffer)
        )
      } else {
        (
          Utils.removeSubstring(sw.state, to+1, from+1, ""),
          Buffer(sw.buffer.cmdStack, sw.state.content.substring(to+1, from+1), sw.buffer.commandBuffer)
        )
      }
    }

    val newPos =
      Position(
        Math.min(sw.state.position.line, newSw.state.position.line),
        Math.min(newSw.state.position.character, sw.state.position.character))

    val newState = State(newContent, newPos, newSw.state.selection, newMode)

    Option(StateWrapper(sw.prevState, newState, newBuffer, sw.visual))
  }
}

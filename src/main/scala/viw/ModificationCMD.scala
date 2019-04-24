package viw
import internals.State
import viw.internals.State.Position
import viw.StateWrapper.Buffer

object ModificationCMD extends CMD {

  val commands: List[String] = List("x", "X", "D", "J", "s", "C", "o", "p", "P")

  override def applyCmd(sw: StateWrapper, cmd: String): Option[StateWrapper] = {

    cmd match {
      case "x" => xDelete(0, sw, mode = true)
      case "s" => xDelete(0, sw, mode = false)
      case "X" => xDelete(-1, sw, mode = true)
      case "D" => deleteLine(sw, -1, mode = true)
      case "C" => deleteLine(sw, 0, mode = false)
      case "J" => joinLine(sw)
      case "o" => openLine(sw)
      case "p" => paste(sw, 1)
      case "P" => paste(sw, -1)
      case _ => Option(sw)
    }

  }

  def paste(sw: StateWrapper, direction: Int): Option[StateWrapper] = {

    // Get current position in content
    val currIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line)) + sw.state.position.character
    val nextIdx = currIdx + direction

    // Build new content
    val newContent = {
      if (direction == -1 && currIdx == 0)
        Utils.concat("", sw.buffer.contentBuffer, sw.state.content)
      else if (direction == 1 && currIdx == sw.state.content.length-1)
        Utils.concat("", sw.state.content, sw.buffer.contentBuffer)
      else
        Utils.removeSubstring(sw.state, Math.min(currIdx, nextIdx)+1, Math.max(currIdx, nextIdx), sw.buffer.contentBuffer)
    }

    // Build new position
    val newPos = {
      val tmp = Utils.countLines(sw.buffer.contentBuffer, sw.state)
      if (tmp.line != sw.state.position.line && tmp.character > 0 && direction == -1) tmp
      else if (tmp.line != sw.state.position.line && tmp.character == 0 && direction == -1) {
        val prevLine = sw.buffer.contentBuffer.split('\n')
        Position(tmp.line-1, prevLine(prevLine.length-1).length-1)
      }
      else if (direction == -1)
        Position(tmp.line, sw.state.position.character+sw.buffer.contentBuffer.length-1)
      else if (tmp.line != sw.state.position.line && direction == 1) tmp
      else
        Position(tmp.line, sw.state.position.character+sw.buffer.contentBuffer.length)
    }

    val newState = State(newContent, newPos, sw.state.selection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def openLine(sw: StateWrapper): Option[StateWrapper] = {

    // Get start and end of line index in state content
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val endLineIdx = currLineIdx + sw.state.contentLines(sw.state.position.line).length

    // Build new content
    val newContent = Utils.removeSubstring(sw.state, from = endLineIdx, to = endLineIdx, "\n")

    val newState = State(newContent, Position(sw.state.position.line+1, 0), sw.state.selection, mode = false)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def joinLine(sw: StateWrapper): Option[StateWrapper] = {

    // Get new line char index in state content
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val from = currLineIdx + sw.state.contentLines(sw.state.position.line).length

    // Build new content and get new position
    val newContent = Utils.removeSubstring(sw.state, from, from + 1, " ")
    val newPos = Position(sw.state.position.line, sw.state.contentLines(sw.state.position.line).length)

    // Update buffer
    val bf: Buffer = Buffer(sw.buffer.cmdStack, "\n", sw.buffer.commandBuffer)

    val newState = State(newContent, newPos, sw.state.selection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, bf, sw.visual))
  }

  def deleteLine(sw: StateWrapper, pos: Int, mode: Boolean): Option[StateWrapper] = {

    // Get end of line index
    val line = sw.state.contentLines(sw.state.position.line)
    val eol = sw.state.content.indexOf(line) + line.length

    // Build new content
    val newContent = Utils.removeSubstring(sw.state, sw.state.position.character, eol, "")

    // Update buffer
    val bf: Buffer = Buffer(sw.buffer.cmdStack, sw.state.content.slice(sw.state.position.character, eol), sw.buffer.commandBuffer)

    val newState = State(newContent, Position(sw.state.position.line, sw.state.position.character + pos), sw.state.selection, mode)

    Option(StateWrapper(sw.prevState, newState, bf, sw.visual))
  }

  def xDelete(dir: Int, sw: StateWrapper, mode: Boolean) : Option[StateWrapper] = {

    // Get current idx in content
    val currIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line)) + sw.state.position.character

    // Build new content
    val newContent = Utils.removeSubstring(sw.state, currIdx, currIdx+1, "")

    // Update buffer
    val bf: Buffer = Buffer(sw.buffer.cmdStack, sw.state.content(currIdx).toString, sw.buffer.commandBuffer)

    val newState = {
      if (currIdx == 0)
        State(newContent, Position(sw.state.position.line, 0), sw.state.selection, mode)
      else if (currIdx == sw.state.content.length-1)
        State(newContent, Position(sw.state.position.line, currIdx-1), sw.state.selection, mode)
      else State(newContent, Position(sw.state.position.line, sw.state.position.character + dir), sw.state.selection, mode)
    }

    Option(StateWrapper(sw.prevState, newState, bf, sw.visual))
  }
}

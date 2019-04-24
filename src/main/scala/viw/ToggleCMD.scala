package viw

import viw.internals.State
import viw.internals.State.Position
import viw.StateWrapper.Buffer

object ToggleCMD extends CMD {

  override def applyCmd(sw: StateWrapper, cmd: String): Option[StateWrapper] = {

    val newBuffer = Buffer(sw.buffer.cmdStack, sw.buffer.contentBuffer, cmd)

    cmd match {
      case " " =>
        if (!sw.state.mode) {
          val newState = State(sw.state.content, sw.state.position, sw.state.selection, mode = true)
          Option(StateWrapper(sw.prevState, newState, newBuffer, sw.visual))
        } else Option(sw)
      case "i" =>
        if (sw.state.mode) {
          val newState = State(sw.state.content, sw.state.position, sw.state.selection, mode = false)
          Option(StateWrapper(sw.prevState, newState, newBuffer, sw.visual))
        } else Option(sw)
      case "v" => Option(StateWrapper(sw.prevState, sw.state, newBuffer, visual = !sw.visual))
      case "V" =>
        val start = Position(sw.state.position.line, 0)
        val end = Position(sw.state.position.line, sw.state.contentLines(sw.state.position.line).length)
        val newSelection = Option((start, end))
        val newState = State(sw.state.content, sw.state.position, newSelection, sw.state.mode)
        Option(StateWrapper(sw.prevState, newState, newBuffer, sw.visual))
    }
  }
}

package viw

import viw.internals.State
import viw.StateWrapper.Buffer


object Viw {

  val finalCommands: List[String] =  List("h", "j", "k", "l", "w", "b", "e", "d", "c", "y", ">", "<")
  var stateWrapper: StateWrapper = StateWrapper(null, null, new Buffer, visual = false)

  def processKey(key: String, state: State): Option[State] = {

    if (stateWrapper.state == null)
      stateWrapper = StateWrapper(state, state, stateWrapper.buffer, stateWrapper.visual)
    else stateWrapper = StateWrapper(stateWrapper.prevState, state, stateWrapper.buffer, stateWrapper.visual)


    key match {
      case "i"|"v"|"V"|" " if stateWrapper.buffer.cmdStack.isEmpty =>
        stateWrapper = ToggleCMD.applyCmd(stateWrapper, key).get
        Option(stateWrapper.state)
      case "." if stateWrapper.buffer.cmdStack.isEmpty =>
        if (stateWrapper.buffer.commandBuffer.length > 1)
          stateWrapper = CombinationCMD.applyCmd(stateWrapper, stateWrapper.buffer.commandBuffer).get
        else stateWrapper = processSingleCommand(stateWrapper.buffer.commandBuffer, stateWrapper).get
        Option(stateWrapper.state)
      case _ => process(stateWrapper, key)
    }
  }


  def process(sw:StateWrapper, key: String): Option[State] = {

    if (sw.state.mode) {

      if ((finalCommands.contains(key) || stateWrapper.buffer.cmdStack.contains("f")) && stateWrapper.buffer.cmdStack.nonEmpty) {

        val cmdStack = stateWrapper.buffer.cmdStack ++ List(key)
        val commandBuffer = cmdStack.mkString("")

        // Update buffer and wrapper
        val updatedBuffer = Buffer(List(), stateWrapper.buffer.contentBuffer, commandBuffer)
        stateWrapper = StateWrapper(stateWrapper.prevState, stateWrapper.state, updatedBuffer, stateWrapper.visual)

        // process command
        stateWrapper = CombinationCMD.applyCmd(stateWrapper, commandBuffer).get
        Option(stateWrapper.state)
      } else {
        if (stateWrapper.buffer.cmdStack.nonEmpty) {
          val cmdStack = stateWrapper.buffer.cmdStack ++ List(key)
          // Update buffer and wrapper
          val updatedBuffer = Buffer(cmdStack, stateWrapper.buffer.contentBuffer, stateWrapper.buffer.commandBuffer)
          stateWrapper = StateWrapper(stateWrapper.prevState, stateWrapper.state, updatedBuffer, stateWrapper.visual)
          Option(sw.state)
        }
        else {
          // Update buffer and wrapper
          val updatedBuffer = Buffer(stateWrapper.buffer.cmdStack, stateWrapper.buffer.contentBuffer, key)
          stateWrapper = StateWrapper(stateWrapper.prevState, stateWrapper.state, updatedBuffer, stateWrapper.visual)

          // process command
          stateWrapper = processSingleCommand(key, stateWrapper).get
          Option(stateWrapper.state)
        }
      }
    }else Option(sw.state)
  }

  def processSingleCommand(key: String, state: StateWrapper): Option[StateWrapper] = {
    /*
    Method to dispatch movement or modification commands based on them triggering exit from Viw mode
     */

    if (MovementCMD.commands.contains(key))
      MovementCMD.applyCmd(state, key)
    else if (ModificationCMD.commands.contains(key))
      ModificationCMD.applyCmd(state, key)
    else {
      val cmdStack = stateWrapper.buffer.cmdStack ++ List(key)
      // Update buffer and wrapper
      val updatedBuffer = Buffer(cmdStack, stateWrapper.buffer.contentBuffer, stateWrapper.buffer.commandBuffer)
      Option(StateWrapper(stateWrapper.prevState, stateWrapper.state, updatedBuffer, stateWrapper.visual))
    }
  }


}

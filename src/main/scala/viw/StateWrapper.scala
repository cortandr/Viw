package viw
import viw.internals.State


trait StateFields {

  def prevState: State

  def state: State

  def buffer: StateWrapper.Buffer

  def visual: Boolean

}

case class StateWrapper(prevState: State,
                         state: State,
                         buffer: StateWrapper.Buffer,
                         visual: Boolean)

object StateWrapper {

  case class Buffer(cmdStack: List[String], contentBuffer: String, commandBuffer: String) {
    def this() = this(List(), "", "")

  }
}

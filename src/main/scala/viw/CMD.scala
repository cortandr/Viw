package viw

trait CMD {

  def applyCmd(sw: StateWrapper, cmd: String): Option[StateWrapper]

}

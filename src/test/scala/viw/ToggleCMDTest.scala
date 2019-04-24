package viw

import org.scalatest.{BeforeAndAfter, FunSuite}
import viw.internals.State.Position

class ToggleCMDTest extends FunSuite with ViwTest with BeforeAndAfter{
//
  test("Enter viw mode") {
    viwTrue(
      " ",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Exit viw mode") {
    viwFalse(
      "i",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

//  before {
//    // setup your test
//  }
//
//  after {
//    val selection = Option((Position(0, 0), Position(0, Viw.stateWrapper.state.contentLines(0).length)))
//    assert(Viw.stateWrapper.state.selection == selection)
//  }

  test("Select line") {
    viwTrue(
      "V",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }
}

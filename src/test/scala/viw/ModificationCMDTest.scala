package viw

import org.scalatest.{BeforeAndAfter, FunSuite}
import viw.StateWrapper.Buffer

class ModificationCMDTest extends FunSuite with ViwTest with BeforeAndAfter{

  before {
    // Normal tests
    val sw = Viw.stateWrapper
    val buffer = Buffer(sw.buffer.cmdStack, "ciao", sw.buffer.commandBuffer)
    Viw.stateWrapper = StateWrapper(sw.prevState, sw.state, buffer, sw.visual)
    // Paste newLine test
//    val sw = Viw.stateWrapper
//    val buffer = Buffer(sw.buffer.cmdStack, "Hey!\nHow are you?\n", sw.buffer.commandBuffer)
//    Viw.stateWrapper = StateWrapper(sw.prevState, sw.state, buffer, sw.visual)
  }

  after {
    // cleanup your history
  }

  test("Delete boundary") {
    viwTrue(
      "x",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit#.#""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing eli#t#""".stripMargin
    )
  }

  test("Delete backwards boundary") {
    viwTrue(
      "X",
      """#L#orem ipsum dolor sit amet, consectetur adipiscing elit.""".stripMargin,
      """#o#rem ipsum dolor sit amet, consectetur adipiscing elit.""".stripMargin
    )
  }

  test("Paste right") {
    viwTrue(
      "p",
      """Lorem ipsum dolor sit ame#t#, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit ametcia#o#, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Paste left") {
    viwTrue(
      "P",
      """Lorem ipsum dolor sit ame#t#, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amecia#o#t, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Paste beginning") {
    viwTrue(
      "P",
      """#L#orem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """cia#o#Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Paste end") {
    viwTrue(
      "p",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt#.#""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.cia#o#""".stripMargin
    )
  }

  test("Paste newLine") {
    viwTrue(
      "P",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |#C#ras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Hey!
        |How are you#?#
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }



}

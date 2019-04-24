package viw

import org.scalatest.{BeforeAndAfter, FunSuite}

class MovementCMDTest extends FunSuite with ViwTest with BeforeAndAfter {

  before {
    // setup your test
  }

  after {
    // cleanup your history
  }

  val sourceText =
    """Lorem ipsum dolor sit ame#t#, consectetur adipiscing elit.
      |Cras quis massa eu ex commodo imperdiet.
      |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin

  test("Repeat left move") {
    viwTrue(
      "h.",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """L#o#rem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Moving left at boundary") {
    viwTrue(
      "h",
      """#L#orem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """#L#orem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Moving right at boundary") {
    viwTrue(
      "l",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt#.#""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt#.#""".stripMargin
    )
  }

  test("Moving up from first line") {
    viwTrue(
      "k",
      """Lorem ipsum dolor sit #a#met, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit #a#met, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Moving down from last line") {
    viwTrue(
      "j",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at #j#usto malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at #j#usto malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Next word into next line") {
    viwTrue(
      "w",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo impe#r#diet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |#C#urabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Next word end of file") {
    viwTrue(
      "w",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tin#c#idunt""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidun#t#""".stripMargin
    )
  }


  test("Back word from mid word") {
    viwTrue(
      "b",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis ma#s#sa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis #m#assa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Back word into previous line") {
    viwTrue(
      "b",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |#C#ras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing #e#lit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Back word beginning of file") {
    viwTrue(
      "b",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """#L#orem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Nested bracket same line") {
    viwTrue(
      "%",
      """Lorem ipsum #(#dolor sit (amet, consectetur) adipiscing) elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum (dolor sit (amet, consectetur) adipiscing#)# elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Nested bracket different line") {
    viwTrue(
      "%",
      """Lorem ipsum #(#dolor sit (amet, consectetur) adipiscing elit.
        |Cras quis massa eu ex commodo) imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum (dolor sit (amet, consectetur) adipiscing elit.
        |Cras quis massa eu ex commodo#)# imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Not on bracket") {
    viwTrue(
      "%",
      """Lorem #i#psum (dolor sit (amet, consectetur) adipiscing elit.
        |Cras quis massa eu ex commodo) imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum #(#dolor sit (amet, consectetur) adipiscing elit.
        |Cras quis massa eu ex commodo) imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Not on bracket different line") {
    viwTrue(
      "%",
      """Lorem #i#psum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex (commodo) imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex #(#commodo) imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }
}
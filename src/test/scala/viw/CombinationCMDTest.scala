package viw

import org.scalatest.{BeforeAndAfter, FunSuite}

class CombinationCMDTest extends FunSuite with ViwTest with BeforeAndAfter {

  before {
    // setup your test
  }

  after {
    // cleanup your history
  }

  test("Count + right move") {
    viwTrue(
      "3l",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem #i#psum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Count Extra + right move") {
    viwTrue(
      "10l",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum d#o#lor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Delete + left move") {
    viwTrue(
      "dh",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lo#r#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Delete + right move") {
    viwTrue(
      "dl",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#m# ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Delete + up move") {
    viwTrue(
      "dk",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras #q#uis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem# #uis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Delete + next word move") {
    viwTrue(
      "dw",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.""".stripMargin,
      """Lor#i#psum dolor sit amet, consectetur adipiscing elit.""".stripMargin
    )
  }

  test("Delete + prev word move") {
    viwTrue(
      "db",
      """Lorem ipsum dolo#r# sit amet, consectetur adipiscing elit.""".stripMargin,
      """Lorem ipsum #d# sit amet, consectetur adipiscing elit.""".stripMargin
    )
  }

  test("Delete line") {
    viwTrue(
      "dd",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """#C#ras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Find key") {
    viwTrue(
      "fc",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, #c#onsectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Find punctuation") {
    viwTrue(
      "f,",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet#,# consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Not Found punctuation") {
    viwTrue(
      "f:",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Change + left move") {
    viwFalse(
      "ch",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lo#r#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Change + right move") {
    viwFalse(
      "cl",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#m# ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Change + up move") {
    viwFalse(
      "ck",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras #q#uis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem# #uis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Change + down move") {
    viwFalse(
      "cj",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#s# quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Indent + right move") {
    viwTrue(
      ">l",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """  Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Indent + right indent") {
    viwTrue(
      ">>",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """  Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Indent + down move") {
    viwTrue(
      ">j",
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """  Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |  Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Indent + up move") {
    viwTrue(
      ">k",
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cra#s# quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """  Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |  Cra#s# quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("De indent + left move") {
    viwTrue(
      "<h",
      """  Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("De Indent + left de-indent") {
    viwTrue(
      "<<",
      """  Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("De Indent + down move") {
    viwTrue(
      "<j",
      """  Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |  Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lor#e#m ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("De Indent + up move") {
    viwTrue(
      "<k",
      """  Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |  Cra#s# quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cra#s# quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Yank section and paste it") {
    viwTrue(
      "yw0P",
      """Lorem #i#psum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """ipsum# #Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }

  test("Yank line and paste it") {
    viwTrue(
      "yy0P",
      """Lorem #i#psum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin,
      """Lorem ipsum dolor sit amet, consectetur adipiscing elit#.#Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        |Cras quis massa eu ex commodo imperdiet.
        |Curabitur auctor tellus at justo malesuada, at ornare mi tincidunt.""".stripMargin
    )
  }


}
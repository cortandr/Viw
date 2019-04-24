package viw
import internals.State
import viw.internals.State.Position


object MovementCMD extends CMD {

  val commands : List[String] = List("h", "j", "k", "l", "w", "b", "e", "$", "0", "%", "a", "I", "A", "G", "v")
  val brackets: Map[String, String] = Map("(" -> ")", "[" -> "]", "{" -> "}")

  override def applyCmd(sw: StateWrapper, cmd: String): Option[StateWrapper] = {
    cmd match {
      case "h" => horizontalMove(sw, sw.state.position.character > 0, -1, newMode = true)
      case "j" => verticalMove(sw, sw.state.position.line < sw.state.contentLines.length - 1, 1)
      case "k" => verticalMove(sw, sw.state.position.line > 0, -1)
      case "l"|"a" =>
        val boundary = sw.state.position.character < sw.state.contentLines(sw.state.position.line).length-1
        horizontalMove(sw, boundary, 1, newMode = if(cmd == "l") true else false)
      case "w" => nextWord(sw)
      case "b" => prevWord(sw)
      case "e" =>
        val endWord = sw.state.content.drop(sw.state.position.character).split("\\W")(0).length
        val newPos = Position(sw.state.position.line, sw.state.position.character + endWord - 1)
        contentBoundary(sw, newPos, mode = true)
      case "$"|"A" =>
        val newPos =
          Position(
            sw.state.position.line,
            sw.state.contentLines(sw.state.position.line).length() + (if (cmd == "$") -1 else 0))
        contentBoundary(sw, newPos, if (cmd == "$") true else false)
      case "0"|"I" => contentBoundary(sw, Position(sw.state.position.line, 0), if(cmd == "0") true else false)
      case "G" =>
        val newPos = Position(sw.state.contentLines.length - 1, 0)
        contentBoundary(sw, newPos, mode = false)
      case "%" => moveBracket(sw)
      case _ => Option(StateWrapper(sw.prevState, sw.state, sw.buffer, visual = sw.visual))

    }
  }

  def contentBoundary(sw: StateWrapper, newPos: State.Position, mode: Boolean): Option[StateWrapper] = {

    // Get new selection
    val newSelection = if (!sw.visual) sw.state.selection else Option((sw.state.position, newPos))

    // Build new state
    val newState = State(sw.state.content, newPos, newSelection, mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))

  }

  def nextWord(sw: StateWrapper): Option[StateWrapper] = {

    // Get content from current position
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val fromCursor = sw.state.content.drop(currLineIdx + sw.state.position.character)

    // Tokenize text
    val words = fromCursor.split("\\W").filter(_ != "")

    // Find next word idx in state content
    val nextWordIdx =
      if (words.length > 1) fromCursor.indexOf(words(1))
      else fromCursor.indexOf(words(0)) + words(0).length - 1

    // Check for change of line and get new position
    val between = fromCursor.take(nextWordIdx)
    val newPos = {
      if (between.count(_ == '\n') == 0)
        Position(sw.state.position.line, sw.state.position.character + nextWordIdx)
      else
        Position(
          sw.state.position.line + between.count(_ == '\n'),
          sw.state.contentLines(sw.state.position.line + between.count(_ == '\n')).indexOf(words(1)))
    }

    val newSelection = {
      if (!sw.visual)
        sw.state.selection
      else Option((sw.state.position, newPos))
    }
    val newState = State(sw.state.content, newPos, newSelection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def moveBracket(sw: StateWrapper): Option[StateWrapper] = {

    // Get index of current position in state content
    val curr_pos = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line)) + sw.state.position.character
    val fromCursor = sw.state.content.drop(curr_pos)

    val newState = {
      if (brackets.keys.toArray.contains(sw.state.content(curr_pos).toString)) {

        val matchIdx = fromCursor.indexOf(brackets(sw.state.content(curr_pos).toString))
        val between = fromCursor.substring(1, matchIdx)
        val newPos = {
          if (between.count(_ == sw.state.content(curr_pos)) == 0)
            Utils.countLines(fromCursor.substring(1, matchIdx+1), sw.state)
          else {
            val text = matching_brackets(sw.state, curr_pos, between, fromCursor.drop(matchIdx+1), matchIdx)
            Utils.countLines(text, sw.state)
          }
        }

        val newSelection = {
          if (!sw.visual)
            sw.state.selection
          else Option((sw.state.position, newPos))
        }

        State(sw.state.content, newPos, newSelection, sw.state.mode)
      } else {
        // Brackets indexes in content
        val bracketsIdx = for (key <- brackets.keys) yield fromCursor.indexOf(key)

        // Get closest bracket to current cursor
        val firstBracket = bracketsIdx.filter(_ >= 0).min

        // Check for change of line and get new position
        val text = sw.state.content.slice(sw.state.position.character, sw.state.position.character+firstBracket)
        val newPos = Utils.countLines(text, sw.state)

        val newSelection = {
          if (!sw.visual)
            sw.state.selection
          else Option((sw.state.position, newPos))
        }

        State(sw.state.content, newPos, newSelection, sw.state.mode)
      }
    }

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def matching_brackets(state: State, currPos: Int, between: String, fromMatch: String, matchingBracket: Int) : String = {

    if (between.count(_ == state.content(currPos)) == 0)
      return state.content.substring(currPos, currPos+matchingBracket)

    val nextBracketIdx = between.indexOf(state.content(currPos).toString)
    val nextBetween = between.drop(nextBracketIdx+1)
    val nextMatch = fromMatch.indexOf(brackets(state.content(currPos).toString))
    val nextFromMatch = fromMatch.drop(nextMatch+1)

    matching_brackets(state, currPos, nextBetween, nextFromMatch, matchingBracket+nextMatch+1)

  }

  def prevWord(sw: StateWrapper): Option[StateWrapper] = {

    // Get content till current position
    val currLineIdx = sw.state.content.indexOf(sw.state.contentLines(sw.state.position.line))
    val toCursor = sw.state.content.take(currLineIdx + sw.state.position.character)

    // Tokenize words in content
    val words = toCursor.split("\\W").filter(_ != "")

    // Find prev word index in content
    val prevWordIdx = toCursor.lastIndexOf(words(words.length-1))

    // Check for change of line and get new position
    val between = toCursor.drop(prevWordIdx)
    val newPos = {
      if (between.count(_=='\n') == 0)
        Position(sw.state.position.line, prevWordIdx - currLineIdx)
      else
        Position(
          sw.state.position.line - between.count(_=='\n'),
          sw.state.contentLines(sw.state.position.line - between.count(_=='\n')).indexOf(words(words.length-1)))
    }

    val newSelection = {
      if (!sw.visual)
        sw.state.selection
      else Option((sw.state.position, newPos))
    }

    val newState = State(sw.state.content, newPos, newSelection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def horizontalMove(sw: StateWrapper, boundary: Boolean, direction: Int, newMode: Boolean): Option[StateWrapper] = {

    // Build new position
    val newPos = {
      Position(
        sw.state.position.line,
        if (boundary) sw.state.position.character + direction
        else sw.state.position.character)
    }

    // Build selection
    val newSelection = if (!sw.visual) sw.state.selection else Option((sw.state.position, newPos))

    // Build new state
    val newState = State(sw.state.content, newPos, newSelection, newMode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))
  }

  def verticalMove(sw: StateWrapper, boundary: Boolean, direction: Int): Option[StateWrapper] = {

    // Build new position
    val newPos = {
      if (boundary)
        Position(
          sw.state.position.line + direction,
          math.min(sw.state.contentLines(sw.state.position.line + direction).length(), sw.state.position.character))
      else
        Position(sw.state.position.line, sw.state.position.character)
    }

    // Build selection
    val newSelection = if (!sw.visual) sw.state.selection else Option((sw.state.position, newPos))

    // Build new state
    val newState = State(sw.state.content, newPos, newSelection, sw.state.mode)

    Option(StateWrapper(sw.prevState, newState, sw.buffer, sw.visual))

  }
}

package scala.tools.eclipse.scalatest.ui

sealed abstract class Formatter

final case object MotionToSuppress extends Formatter

final case class IndentedText(formattedText: String, rawText: String, indentationLevel: Int) extends Formatter {
  require(indentationLevel >= 0, "indentationLevel was less than zero: " + indentationLevel)
}


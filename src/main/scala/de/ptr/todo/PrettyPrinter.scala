package de.ptr.todo
import javax.swing.text.Document
import javax.swing.text.StyledDocument
import javax.swing.text.StyleContext
import javax.swing.text.StyleConstants
import java.awt.Color

class PrettyPrinter(doc: StyledDocument) {

  def prettyPrint() {
    val docText = doc.getText(0, doc.getLength())
    val historyMarker = docText.lastIndexOf("---")
    // all regular
    doc.setCharacterAttributes(0, historyMarker, Styles.Regular, true)
//    // bullets blue
//    val count = doc.getDefaultRootElement.getElementCount
//    println("count:"+count)
//    for (e <- 0 until count) {
//      val element = doc.getDefaultRootElement.getElement(e)
//      if (element.getStartOffset > 0 && element.getEndOffset <= docText.length) {
//        val sub = docText.substring(element.getStartOffset, element.getEndOffset - 1)
//        
//     		println(element.getStartOffset + " : " + element.getEndOffset+" = "+ sub)
//        if (sub.startsWith("- ")) {
//          doc.setCharacterAttributes(element.getStartOffset, element.getStartOffset + 1, Styles.BoldBlue, false)
//        }
//      }
//    }

    // History gray:
    doc.setCharacterAttributes(historyMarker, doc.getLength(), Styles.Gray, true)
  }

  object Styles {
    val Default = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE)
    StyleConstants.setFontFamily(Default, "SansSerif")

    val Regular = doc.addStyle("regular", Default)

    val Bold = doc.addStyle("bold", Regular)
    StyleConstants.setBold(Bold, true)

    val BoldBlue = doc.addStyle("boldblue", Regular)
    StyleConstants.setBold(Bold, true)
    StyleConstants.setForeground(Bold, Color.blue)

    val Gray = doc.addStyle("gray", Regular)
    StyleConstants.setForeground(Gray, Color.GRAY)

  }
}

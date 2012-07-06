package de.ptr.todo
import javax.swing.text.Document
import de.ptr.todo.DocumentOrganizer._
import javax.swing.text.StyledDocument
import javax.swing.text.StyleContext
import javax.swing.text.StyleConstants
import java.awt.Color

class PrettyPrinter(doc:StyledDocument) {
  
	def prettyPrint(){
	  val docText = doc.getText(0,doc.getLength())
	  val historyMarker = docText.lastIndexOf(HISTORY_POSITION_MARKER)
	  // all regular
	  doc.setCharacterAttributes(0, historyMarker, Styles.Regular, true)
	  // History gray:
	  doc.setCharacterAttributes(historyMarker, doc.getLength(), Styles.Gray, true)
	}
	
	
	object Styles{
		val Default = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE)
		StyleConstants.setFontFamily(Default, "SansSerif")
		
		val Regular = doc.addStyle("regular", Default)
		
		val Bold = doc.addStyle("bold", Regular)
		StyleConstants.setBold(Bold, true)

		val Gray = doc.addStyle("gray", Regular)
		StyleConstants.setForeground(Gray, Color.GRAY)
		
	}
}

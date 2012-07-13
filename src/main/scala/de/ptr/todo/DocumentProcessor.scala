package de.ptr.todo

import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent
import scala.actors.Actor
import scala.math.min
import scala.math.max
import javax.swing.text.StyledDocument
import javax.swing.text.Element
import javax.swing.text.BadLocationException
import java.util.Date
import java.text.SimpleDateFormat

class DocListener(textPublisher: TextPublisher) extends DocumentListener {
  val processor = new DocumentProcessor(textPublisher)

  // TODO we need a supervisor when Exception stops the actor
  processor.start

  override def changedUpdate(evt: DocumentEvent) {
    println("changedUpdate")
    textPublisher.documentChanged
//    processor ! 'changed
  }

  override def insertUpdate(evt: DocumentEvent) {
    println("insertUpdate")
    val doc = evt.getDocument.asInstanceOf[StyledDocument]
    val ele = doc.getParagraphElement(evt.getOffset())
    if (ele.getStartOffset >= 0 && ele.getEndOffset >= 0) {
      processor ! InsertEvent(ele, doc)
    }
  }

  override def removeUpdate(evt: DocumentEvent) {
    println("removeUpdate")
    processor ! 'remove
  }
}

case class InsertEvent(element: Element, doc: StyledDocument)

class DocumentProcessor(textPublisher: TextPublisher) extends Actor {

  val HISTORY_POSITION_MARKER = "---"

  def act() {
    while (true) {
      receive {
        case InsertEvent(ele, doc) => {
          println("changed: " + text(ele, doc))
          val start = ele.getStartOffset
          val docText = doc.getText(0, doc.getLength())
          var currentPosition = start
          val historyMarker = setupDocumentStructure(docText)
          //					val caretPosition = editorpane.peer().getCaretPosition()
          if (currentPosition < historyMarker) {

            val end = findEndFrom(ele.getEndOffset, docText, historyMarker)

            val text = docText.substring(start, end)
            if (text.startsWith("+-")) {

              val insertionText = text.replaceFirst("\\+", "").trim()

              insertForDate(new Date(), insertionText, historyMarker, docText)
              textPublisher.deleteText(start, end - start + 1)
              textPublisher.setCarret(max(start , 0))
            } else if (text.startsWith("-\n")) {
              textPublisher.insertText(" ", start + 1)
              textPublisher.setCarret(min(start + 2,docText.length))
            }
          }
        }
        case x => println("acted on: " + x)
      }
    }

    // endberechnung mit newlines bis dateiende oder - oder ---
    def findEndFrom(end: Int, text: String, historyMarker: Int): Int = {
      val nextBullet = text.indexOf("\n-", end - 1) // end-1 : do not swollow \n
      if (nextBullet > 0) nextBullet else historyMarker - 1
    }

    def setupDocumentStructure(docText: String): Int = {
      var pos = docText.indexOf(HISTORY_POSITION_MARKER)
      if (pos < 0) {
        System.out.println("setupDocumentStructure...")
        pos = docText.length
        textPublisher.insertText("\n\n" + HISTORY_POSITION_MARKER, pos)
      }
      return pos
    }

    def insertForDate(date: Date, text: String, historyMarker: Int, docText: String) {
      val sdf = new SimpleDateFormat("(dd.MM.yyyy)")
      val dateString = sdf.format(date)
      var datePos = docText.indexOf(dateString, historyMarker)
      val pos = historyMarker + HISTORY_POSITION_MARKER.length()
      if (datePos < 0) {
        textPublisher.insertText("\n" + dateString + "\n", pos)
        datePos = pos + 1
      }
      datePos = datePos + dateString.length() + 1 // zweiter Zeilenumbruch
      textPublisher.insertText(text.trim() + "\n", datePos)
    }
  }

  def text(ele: Element, doc: StyledDocument): String = {
    println(ele.getStartOffset + " : " + ele.getEndOffset + "doclen:" + ele.getDocument.getLength)
    val docText = doc.getText(0, doc.getLength())
    docText.substring(ele.getStartOffset, min(ele.getEndOffset, doc.getLength))
  }
}
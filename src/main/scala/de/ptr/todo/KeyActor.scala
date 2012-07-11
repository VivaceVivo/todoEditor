package de.ptr.todo

import scala.actors.Actor
import scala.swing.Publisher

class KeyActor(editor: Editor) extends Actor with Publisher {

  val HISTORY_POSITION_MARKER = "---";

  def act() {
    while (true) {
      receive {

        case "-" =>
          val pos = editor.peer.getCaretPosition
          println("act on: -: <"+editor.doc.getText(pos - 2, 3)+">")
          //          editor. ! new TextInsertEvent()
          if (pos == 0 || editor.doc.getText(pos - 2, 3) == "\n-") {
            publish(new TextInsertEvent(" ", pos, pos + 1))
          }
        // TODO KEYEVENT ERZEUGEN UND AN Editor senden 
//        case "+" =>
//          val docText = editor.doc.getText(0, editor.doc.getLength());
//          val pos = editor.peer.getCaretPosition
//          val historyMarker = setupDocumentStructure(docText);
//          val caretPosition = editor.peer.getCaretPosition();
//          if (pos < historyMarker) {
//
//          }
      }
    }
  }

  def setupDocumentStructure(docText: String): Int = {
    var pos = docText.indexOf(HISTORY_POSITION_MARKER);
    if (pos < 0) {
      println("setupDocumentStructure...");
      pos = editor.doc.getLength();
      publish(new TextInsertEvent("\n\n" + HISTORY_POSITION_MARKER, pos, editor.peer.getCaretPosition()))
    }
    return pos;
  }

}
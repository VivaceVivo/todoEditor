
package de.ptr.todo

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

import scala.io.Source
import scala.swing.Dialog
import scala.swing.Dialog.Result.Cancel
import scala.swing.Dialog.Result.No
import scala.swing.EditorPane
import scala.swing.FileChooser
import scala.swing.FileChooser.Result.Approve
import scala.swing.FileChooser.Result.Cancel
import scala.swing.FileChooser.Result.Error
import scala.swing.Reactions.StronglyReferenced
import scala.swing.event.Key
import scala.swing.event.Key.Location.Standard
import scala.swing.event.Key.Modifier.Control
import scala.swing.event.KeyPressed

import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument
import javax.swing.text.StyledEditorKit

class Editor(fileStateListener: FileStateListener) extends EditorPane with StronglyReferenced {
  val defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
  this.editorKit = new StyledEditorKit()
  var file: Option[File] = None
  val doc = peer.getDocument
  val prettyPrinter = new PrettyPrinter(doc.asInstanceOf[StyledDocument])
  prettyPrinter.prettyPrint()
  val textPublisher = new TextPublisher()
  this.listenTo(textPublisher)
  doc.addDocumentListener(new MyDocumentListener(doc, this, textPublisher))
  listenTo(keys)

  reactions += {
    case KeyPressed(_, Key.S, Control, Standard) => save
    case KeyPressed(_, Key.Q, Control, Standard) => quit
    case KeyPressed(_, _, _, _) => fileStateListener.fileDirty

    case TextInsertEvent(text, pos, caret) => {
      println("Received TextInsertEvent: " + pos + ":" + text);
      doc.insertString(pos, text, defaultStyle);
      peer.setCaretPosition(caret)
      prettyPrinter.prettyPrint()
    }

    case TextDeleteEvent(start, end, caret) => {
      println("Received TextDeleteEvent: " + start + "-" + end);
      doc.remove(start, end)
      if (doc.getText(start, doc.getLength() - start).startsWith(DocumentOrganizer.HISTORY_POSITION_MARKER)) {
        // eine Leerzeile einfügen
        doc.insertString(start, "\n", defaultStyle);
      }
      peer.setCaretPosition(caret)
    }
  }

  def save(): Boolean = {
    file match {
      case None => saveAs()
      case Some(f) => saveBuffer(f)
    }
  }

  def saveAs(): Boolean = {
    val dialog = new FileChooser
    val result = dialog.showSaveDialog(this)
    result match {
      case FileChooser.Result.Cancel => false // nix
      case Error => false // nix
      case Approve => {
        file = Some(dialog.selectedFile)
        saveBuffer(dialog.selectedFile)
      }
    }
  }

  private def saveBuffer(f: File): Boolean = {
    val writer = new BufferedWriter(new FileWriter(f))
    writer.write(text)
    writer.flush()
    writer.close()
    fileStateListener.fileClean
    true
  }

  def openFile(fileOpt: Option[File]) {
    fileOpt match {
      case Some(f) => {
        readFileIntoBuffer(f)
      }
      case _ => {
        val dialog = new FileChooser
        val result = dialog.showOpenDialog(this)
        result match {
          case FileChooser.Result.Cancel =>  // nix
          case Error =>  // nix
          case Approve => {
            file = Some(dialog.selectedFile)
            readFileIntoBuffer(dialog.selectedFile)
          }
        }
      }
    }

    def readFileIntoBuffer(f:File){
      println("openFile: " + f.getName())
        val source = Source.fromFile(f)
        text = source.getLines().mkString("\n")
        file = Some(f)
        fileStateListener.fileClean
    }
  }

  def quit() {
    import Dialog.Result._
    val dialogResult = Dialog.showOptions(this, message = "Save before Quit?", title = "Quit Editor", messageType = Dialog.Message.Question, optionType = Dialog.Options.YesNo, entries = Seq("Yes", "No"), initial = 0)
    val success = dialogResult match {
      case No => true
      case Dialog.Result.Cancel => true
      case x => println("do save???: " + x); save
    }
    if (success) {
      System.exit(0)
    }
  }

}
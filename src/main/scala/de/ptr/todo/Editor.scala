
package de.ptr.todo

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import scala.swing.FileChooser.Result._
import scala.swing.Reactions.StronglyReferenced
import scala.swing.event.Key.Location.Standard
import scala.swing.event.Key.Location.Standard
import scala.swing.event.Key.Modifier.Control
import scala.swing.event.Key
import scala.swing.event.KeyPressed
import scala.swing.Button
import scala.swing.Dialog
import scala.swing.EditorPane
import scala.swing.FileChooser
import javax.swing.text.StyledEditorKit
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument
import scala.io.Source

class Editor(filename:Option[String], fileStateListener: FileStateListener) extends EditorPane with StronglyReferenced {
  val defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
  this.editorKit = new StyledEditorKit()
  var file: Option[File] = if(filename.isDefined)Some(new File(filename.get)) else None
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
      println("Received TextInsertEvent: " + pos + ":" + text );
      doc.insertString(pos, text, defaultStyle);
      peer.setCaretPosition(caret)
      prettyPrinter.prettyPrint()
    }
    
    case TextDeleteEvent(start, end) => {
    	println("Received TextDeleteEvent: " + start + "-" + end);
    	doc.remove(start, end)
    	if(doc.getText(start, doc.getLength()-start).startsWith(DocumentOrganizer.HISTORY_POSITION_MARKER)){
    	  // eine Leerzeile einfügen
    	  doc.insertString(start, "\n", defaultStyle);
    	  peer.setCaretPosition(start)
    	}
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
      case Cancel => false // nix
      case Error => false // nix
      case Approve => {
        file = Some(dialog.selectedFile)
        saveBuffer(file.get)
      }
    }
  }

  private def saveBuffer(f: File): Boolean = {
    val writer = new BufferedWriter(new FileWriter(f))
    writer.write(text)
    writer.flush()
    writer.close()
    fileStateListener.fileChanged
    true
  }

  def openFile(fileOpt:Option[File]) {
    fileOpt match{
      case Some(file) => {
    	  println("openFile: " + file.getName())
        val source = Source.fromFile(file)
        text = source.getLines().mkString("\n")
      }
      case _ =>println("openFile: NONE")
    }
	  
  }

  def quit() {
    import Dialog.Result._
    val dialogResult = Dialog.showOptions(this, message = "Save before Quit?", title = "Quit Editor", messageType = Dialog.Message.Question, optionType = Dialog.Options.YesNo, entries = Seq("Yes", "No"), initial = 0)
    val success = dialogResult match {
      case No => true
      case Cancel => true
      case x => println("do save???: " + x); save
    }
    if (success) {
      System.exit(0)
    }
  }

}
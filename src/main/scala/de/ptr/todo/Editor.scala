
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

class Editor(fileStateListener: FileStateListener) extends EditorPane with StronglyReferenced {
  this.editorKit = new StyledEditorKit()
  var file: Option[File] = None
  val doc = peer.getDocument
  doc.addDocumentListener(new MyDocumentListener(doc))
  listenTo(keys)
  reactions += {
    case KeyPressed(_, Key.S, Control, Standard) => save
    case KeyPressed(_, Key.Q, Control, Standard) => quit
    case KeyPressed(_, _, _, _) => fileStateListener.fileDirty
  }

  def save():Boolean ={
    file match {
      case None => saveAs()
      case Some(f) => saveBuffer(f)
    }
  }

  def saveAs():Boolean ={
    val dialog = new FileChooser
    val result = dialog.showSaveDialog(this)
    result match {
      case Cancel => false// nix
      case Error => false// nix
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

  def openFile() {

  }

  def quit() {
    import Dialog.Result._
    val dialogResult = Dialog.showOptions(this, message = "Save before Quit?", title = "Quit Editor", messageType = Dialog.Message.Question, optionType = Dialog.Options.YesNo, entries = Seq("Yes", "No"), initial = 0)
    val success = dialogResult match {
      case No => true
      case Cancel => true
      case x => println("do save???: " + x);save
    }
    if(success){
      System.exit(0)
    }
  }

}
package de.ptr.todo

import java.io.File
import scala.swing.EditorPane
import scala.swing.FileChooser
import scala.swing.FileChooser.Result._
import java.io.BufferedWriter
import java.io.FileWriter
import scala.swing.Reactions.StronglyReferenced

class Editor extends EditorPane with StronglyReferenced {
  var file:Option[File] = None

  def saveAs(){
    val dialog = new FileChooser
    val result = dialog.showSaveDialog(this)
    result match {
      case Cancel => // nix
      case Error => // nix
      case Approve => {
        file = Some(dialog.selectedFile)
        saveBuffer(file.get)
      }
    }
  }
  def save(){
    file match{
      case None => saveAs()
      case Some(f) => saveBuffer(f)
    }
  }
  
  def openFile(){
    
  }
  
  def quit(){
    
  }
  
//  def reaction 
  
  private def saveBuffer(f:File): Unit = {
    val writer = new BufferedWriter(new FileWriter(f))
    writer.write(text)
    writer.close()
  }
  
}
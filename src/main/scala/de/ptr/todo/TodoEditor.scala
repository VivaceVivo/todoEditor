package de.ptr.todo
import scala.swing.BorderPanel
import scala.swing.Button
import scala.swing.LayoutContainer
import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication
import scala.swing.MenuBar
import scala.swing.Menu
import scala.swing.MenuItem
import scala.swing.Action
import scala.swing.EditorPane
import java.awt.Dimension
import java.io.File

trait FileStateListener{
  def fileDirty{}
  def fileChanged{}
}

object TodoEditor extends SimpleSwingApplication with FileStateListener{

  def top = new MainFrame {
    title = "Todo Editor"
    preferredSize = new Dimension(600, 400)
    val editor = new Editor(new FileStateListener{this.fileDirty; this.fileChanged})
    
    override def closeOperation = editor.quit()

    def fileDirty{
    	setTitle(editor.file, "(", ")") 
    }
    
    def fileChanged{
    	setTitle(editor.file, "", "") 
    }
    
    
    def setTitle(f:Option[File], prefix:String, suffix:String){
      f match{
        case Some(file) => title = "Todo Editor: " + prefix + file.getName() + suffix
        case None => title = "Todo Editor" 
      }
    }
    
    contents = new BorderPanel {
      import scala.swing.BorderPanel.Position._
//      layout += new Button {
//        text = "Click Me!"
//      } -> North
      layout +=editor->Center
    }


    val openAction = Action("Open File...") { editor.openFile }
    val saveAction = Action("Save (ctrl-s)") { editor.save }
    val saveAsAction = Action("Save As...") { editor.saveAs }
    val quitAction = Action("Quit (ctrl-q)") { editor.quit }


    menuBar = new MenuBar {
      contents += new Menu("File") {
   	    contents += new MenuItem(openAction)
    	contents += new MenuItem(saveAction)
   	    contents += new MenuItem(saveAsAction)
        contents += new MenuItem(quitAction)
      }
      contents += new Menu("Edit") {
        contents += new MenuItem(quitAction)
      }
    }

  }
  
  top.pack()
}

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
import scala.swing.event.KeyPressed
import scala.swing.event.Key.Modifier.Control
import scala.swing.event.Key
import scala.swing.event.Key.Location.Standard
import scala.swing.event.Key.Location.Unknown

object TodoEditor extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "Hello, World!"
    preferredSize = new Dimension(600, 400)
    val editor = new Editor()
    
    reactions += {
	  case KeyPressed(this.peer, Key.S, Control, Unknown) => editor.save//reaction here
	}

    
    contents = new BorderPanel {
      import scala.swing.BorderPanel.Position._
      layout += new Button {
        text = "Click Me!"
      } -> North
      layout +=editor->Center
    }


    val openAction = Action("Open File...") { editor.openFile }
    val saveAction = Action("Save") { editor.save }
    val saveAsAction = Action("Save As...") { editor.saveAs }
    val quitAction = Action("Quit") { editor.quit }


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

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

object TodoEditor extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "Hello, World!"

    contents = new BorderPanel {
      import scala.swing.BorderPanel.Position._
      layout += new Button {
        text = "Click Me!"
      } -> North
      layout +=editor->Center
    }

    val quitAction = Action("Quit") { System.exit(0) }

    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(quitAction)
      }
      contents += new Menu("Edit") {
        contents += new MenuItem(quitAction)
      }
    }

  }
  val editor = new EditorPane{
    size = new Dimension(600, 400)
  }
  
  top.pack()
}

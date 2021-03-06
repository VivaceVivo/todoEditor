package de.ptr.todo
import scala.swing.BorderPanel
import scala.swing.Button
import scala.swing.LayoutContainer
import scala.swing.MainFrame
import scala.swing.SimpleSwingApplication
import scala.swing.MenuBar
import scala.swing.Menu
import scala.swing.MenuItem
import scala.swing.ScrollPane
import scala.swing.Action
import scala.swing.EditorPane
import java.awt.Dimension
import java.io.File
import scala.swing.ScrollPane
import scala.swing.ScrollBar

trait FileStateListener {
  def fileDirty {}
  def fileClean {}
}

object TodoEditor extends SimpleSwingApplication with FileStateListener {

  var filename: Option[String] = None
  var editor: Editor = null

  def top = new MainFrame {
    title = "Todo Editor"
    preferredSize = new Dimension(600, 400)
    editor = new Editor(new FileStateListener { override def fileDirty = dirty; override def fileClean = changed })

    override def closeOperation = editor.quit()

    def dirty {
      println("fileDirty")
      setTitle(editor.file, "(", ")")
    }

    def changed {
      setTitle(editor.file, "", "")
    }

    def setTitle(f: Option[File], prefix: String, suffix: String) {
      f match {
        case Some(file) => title = "Todo Editor: " + prefix + file.getName() + suffix
        case None => title = "Todo Editor"
      }
    }
    contents = new BorderPanel {
      import scala.swing.BorderPanel.Position._
      import scala.swing.ScrollPane.BarPolicy._
      val scrollPane = new ScrollPane {
        contents = editor
        horizontalScrollBarPolicy = AsNeeded
        verticalScrollBarPolicy = AsNeeded
      }
      layout += scrollPane -> Center
    }

    val openAction = Action("Open File...") { editor.openFile(None) }
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

  /**
   * Wird von der Application mit den Startparametern aufgerufen.
   */
  override def startup(args: Array[String]) {
    super.startup(args)
    if (args.length > 0) {
      filename = Some(args(0))
      var file: Option[File] = if (filename.isDefined) Some(new File(filename.get)) else None

      editor.openFile(file)
      editor.peer.setCaretPosition(0)
    }
  }
}

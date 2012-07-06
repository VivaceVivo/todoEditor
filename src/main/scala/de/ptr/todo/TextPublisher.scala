package de.ptr.todo

import scala.swing.Publisher
import scala.swing.event.Event

class TextPublisher extends Publisher{
	def insertText(text:String, position:Int, caretPos:Int){
	  publish(new TextInsertEvent(text, position, caretPos))
	}
	def deleteText(start:Int, end:Int, caretPos:Int){
		publish(new TextDeleteEvent(start, end, caretPos))
	}
}

case class TextInsertEvent(text:String, position:Int, caretPos:Int) extends Event
case class TextDeleteEvent(start:Int, end:Int, caretPos:Int) extends Event
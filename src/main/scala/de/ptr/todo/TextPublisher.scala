package de.ptr.todo

import scala.swing.Publisher
import scala.swing.event.Event

class TextPublisher extends Publisher{
	def insertText(text:String, position:Int){
	  publish(new TextInsertEvent(text, position))
	}
	def deleteText(start:Int, end:Int){
		publish(new TextDeleteEvent(start, end))
	}
	def setCarret(start:Int){
		publish(new TextCarretEvent(start))
	}
	def documentChanged(){
		publish(new DocModifiedEvent())
	}
}

case class TextInsertEvent(text:String, position:Int) extends Event
case class TextDeleteEvent(start:Int, end:Int) extends Event
case class TextCarretEvent(start:Int) extends Event
case class DocModifiedEvent() extends Event
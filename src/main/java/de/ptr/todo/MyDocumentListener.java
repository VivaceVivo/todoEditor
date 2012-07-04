package de.ptr.todo;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import scala.swing.EditorPane;

public class MyDocumentListener implements DocumentListener {
	private Document doc;
	DocumentOrganizer documentOrganizer;
	public MyDocumentListener(Document doc, EditorPane editorPane) {
		this.doc = doc;
		documentOrganizer = new DocumentOrganizer(doc, editorPane);
	}

	public void changedUpdate(DocumentEvent e) {
		process(e);
	}

	public void insertUpdate(DocumentEvent e) {
		process(e);
	}

	public void removeUpdate(DocumentEvent e) {
		process(e);
	}

	public void process(DocumentEvent e) {
		StyledDocument styled = (StyledDocument)doc;
		Element ele = styled.getParagraphElement(e.getOffset());
//		prettyPrinter.changed(ele.getStartOffset(), ele.getEndOffset());
		documentOrganizer.changed(ele.getStartOffset(), ele.getEndOffset());
	}
}
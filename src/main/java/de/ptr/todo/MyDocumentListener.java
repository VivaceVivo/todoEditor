package de.ptr.todo;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class MyDocumentListener implements DocumentListener {
	private Document doc;
	DocumentOrganizer documentOrganizer;
	public MyDocumentListener(Document doc) {
		this.doc = doc;
		documentOrganizer = new DocumentOrganizer(doc);
	}

	public void changedUpdate(DocumentEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
		process(e);
	}

	public void removeUpdate(DocumentEvent e) {
		process(e);
	}

	public void process(DocumentEvent e) {
//		Element ele = doc.getParagraphElement(e.getOffset());
//		prettyPrinter.changed(ele.getStartOffset(), ele.getEndOffset());
		documentOrganizer.changed(e.getOffset(), e.getOffset() + e.getLength());
	}
}
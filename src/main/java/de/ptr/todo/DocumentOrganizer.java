package de.ptr.todo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import scala.actors.threadpool.LinkedBlockingQueue;
import scala.swing.EditorPane;

public class DocumentOrganizer extends ThreadListenerSupport implements
		Runnable {

	public static final String HISTORY_POSITION_MARKER = "\n\n---";
	public static final DateFormat sdf = new SimpleDateFormat("(dd.MM.yyyy)");

	private Queue<WorkItem> workItems = new LinkedBlockingQueue<WorkItem>();

	private Document doc;
	private EditorPane editorpane;
	private Thread organizerPrinterThread;
	private TextPublisher textPublisher;

	public DocumentOrganizer(Document doc, EditorPane editorpane,
			TextPublisher textPublisher) {
		this.doc = doc;
		this.editorpane = editorpane;
		organizerPrinterThread = new Thread(this);
		organizerPrinterThread.start();
		this.textPublisher = textPublisher;
	}

	public void changed(int start, int end) {
		if (start >= 0 && end > start) {
			workItems.add(new WorkItem(start, end));
		}
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
			while (!workItems.isEmpty()) {
				WorkItem workItem = workItems.remove();
				// editorpane.peer().setEditable(false);

				// System.out.println("start: "+start+" end:"+end);
				try {
					int start = workItem.start;
					String docText = doc.getText(0, doc.getLength());
					int currentPosition = start;
					int historyMarker = docText.lastIndexOf("\n---");
					if (currentPosition < historyMarker) {

						int end = findEndFrom(workItem.end, docText, historyMarker);
						// int end = workItem.end;

						String text = docText.substring(start, end);

						if (text.startsWith("+")) {
							System.out.println("organizing document.");
							notifyListeners(true);

							text = text.replaceFirst("\\+", "").trim();

							insertForDate(new Date(), text, historyMarker);
							textPublisher.deleteText(start, end - start + 1);

							notifyListeners(false);
						}
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				} finally {
					// editorpane.peer().setEditable(true);
				}
			}
		}
	}

	// endberechnung mit newlines bis dateiende oder - oder ---
	private int findEndFrom(int end, String text, int historyMarker) {

		int nextBullet = text.indexOf("\n-", end - 1)  ; // end-1 : do not swollow \n
		System.out.println("end: " + end);
		System.out.println("nextBullet: " + nextBullet);
		int ende = nextBullet > 0 ? nextBullet  : historyMarker - 1;
		System.out.println("ende: " + ende);
		return ende;
	}

	private void insertForDate(Date date, String text, int historyMarker) {
		try {
			String docText = doc.getText(doc.getStartPosition().getOffset(),
					doc.getLength());

			setupDocumentStructure(docText);

			String dateString = sdf.format(date);
			int datePos = docText.indexOf(dateString, historyMarker);
			int pos = historyMarker + HISTORY_POSITION_MARKER.length();
			if (datePos < 0) {
				// doc.insertString(pos, "\n" + dateString + "\n", def);
				textPublisher.insertText("\n" + dateString + "\n", pos, editorpane
						.peer().getCaretPosition());
				datePos = pos + 1;// Zeilenumbruch
			}
			datePos = datePos + dateString.length() + 1; // zweiter Zeilenumbruch
			// doc.insertString(datePos, text.trim() + "\n", def);
			textPublisher.insertText(text.trim() + "\n", datePos, editorpane.peer()
					.getCaretPosition());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	private void setupDocumentStructure(String docText) {
		int pos = docText.indexOf(HISTORY_POSITION_MARKER);
		if (pos < 0) {
			System.out.println("setupDocumentStructure...");
			// doc.insertString(doc.getLength(), "\n\n" + HISTORY_POSITION_MARKER,
			// def);
			textPublisher.insertText(HISTORY_POSITION_MARKER, doc.getLength(),
					editorpane.peer().getCaretPosition());
		}
	}

	private class WorkItem {
		int start;
		int end;

		public WorkItem(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}

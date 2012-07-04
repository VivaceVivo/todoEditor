package de.ptr.todo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import scala.swing.EditorPane;

public class DocumentOrganizer extends ThreadListenerSupport implements Runnable{
	public static final String HISTORY_POSITION_MARKER = "---";
	public static final DateFormat sdf = new SimpleDateFormat("(dd.MM.yyyy)");
	
	private Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	private Document doc;
	EditorPane editorpane;
	private boolean workTodo=false;
	private int modifiedStart = -1;
	private int modifiedEnd   = -1;
	private Thread organizerPrinterThread;
	
	public DocumentOrganizer(Document doc, EditorPane editorpane){
		this.doc = doc;
		this.editorpane = editorpane;
		organizerPrinterThread = new Thread(this);
		organizerPrinterThread.start();
	}
	
	private void setWorkTodo(boolean workTodo) {
		this.workTodo = workTodo;
	}
	
	public void changed(int start, int end){
		this.modifiedStart = start;
		this.modifiedEnd = end;
		setWorkTodo(true);
	}
	
	public void run() {
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//ignore
			}
			if(workTodo){
				editorpane.peer().setEditable(false);
				int start, end;
				if(modifiedStart>=0 && modifiedEnd>=0){
					start = modifiedStart;
					end = modifiedEnd;
					// reset
					modifiedStart = -1;
					modifiedEnd   = -1;
//				System.out.println("start: "+start+" end:"+end);
				try {
					String text = doc.getText(start, end-start);
					System.out.println(">"+text+"<");
					if(text.startsWith("+")){
						System.out.println("organizing document.");
						notifyListeners(true);
						
						text = text.replaceFirst("\\+", "").trim();
						doc.remove(start, end-start);
						setupDocumentStructure();
						insertForDate(new Date(), text);
						
						notifyListeners(false);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}finally{
					workTodo=false;
					editorpane.peer().setEditable(true);
				}
				}
			}
		}
	}

	private void insertForDate(Date date, String text) {
		try {
			String docText = doc.getText(doc.getStartPosition().getOffset(), doc.getLength());
			int pos = docText.lastIndexOf(HISTORY_POSITION_MARKER)+HISTORY_POSITION_MARKER.length();
			
			if(pos>=0){
				String dateString = sdf.format(date);
				int datePos = docText.indexOf(dateString, pos);
				if(datePos<0){
					doc.insertString(pos, "\n"+dateString+"\n", def);
					datePos = pos+1;// Zeilenumbruch
				}
				datePos = datePos+dateString.length()+1; // zweiter Zeilenumbruch
				doc.insertString(datePos, text.trim()+"\n", def);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}

	private void setupDocumentStructure() {
		try {
			String docText = doc.getText(doc.getStartPosition().getOffset(), doc.getLength());
			int pos = docText.indexOf(HISTORY_POSITION_MARKER);
			if(pos<0){
				doc.insertString(doc.getLength(), "\n\n"+HISTORY_POSITION_MARKER, def);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
}

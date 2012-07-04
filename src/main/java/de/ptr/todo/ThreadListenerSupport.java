package de.ptr.todo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ThreadListenerSupport {
	private Set threadListener;
	
	public ThreadListenerSupport (){
		threadListener = new HashSet();
	}
	public void addThreadListener(ThreadListener listener){
		threadListener.add(listener);
	}
	public void removeThreadListener(ThreadListener listener){
		threadListener.remove(listener);
	}
	
	protected void notifyListeners(boolean active){
		Iterator listeners = threadListener.iterator();
		while (listeners.hasNext()) {
			ThreadListener lst = (ThreadListener) listeners.next();
			if(active){
				lst.notifyThreadActive();
			}else{
				lst.notifyThreadInactive();
			}
		}
	}
}

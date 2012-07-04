package de.ptr.todo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ThreadListenerSupport {
	private Set<ThreadListener> threadListener;
	
	public ThreadListenerSupport (){
		threadListener = new HashSet<ThreadListener>();
	}
	public void addThreadListener(ThreadListener listener){
		threadListener.add(listener);
	}
	public void removeThreadListener(ThreadListener listener){
		threadListener.remove(listener);
	}
	
	protected void notifyListeners(boolean active){
		Iterator<ThreadListener> listeners = threadListener.iterator();
		while (listeners.hasNext()) {
			ThreadListener lst = listeners.next();
			if(active){
				lst.notifyThreadActive();
			}else{
				lst.notifyThreadInactive();
			}
		}
	}
}

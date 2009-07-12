package gui.playlist;

import java.awt.Dimension;
import java.awt.EventQueue;

public class EventSetSize implements Runnable {
	private Playlist playlist;
	private Dimension newSize;
	private Runnable nextEvent;
	
	
	public EventSetSize(Playlist playlist, Dimension size, Runnable event) {
		this.playlist = playlist;
		newSize = size;
		nextEvent = event;
	}

	public void run() {
		playlist.setPreferredSize(newSize);
		EventQueue.invokeLater(nextEvent);
	}
	
}

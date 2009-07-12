package gui.playlist;

public class EventAddIndividualPanel implements Runnable {
	private Playlist playlist;
	private IndividualPanel individualPanel;
	
	public EventAddIndividualPanel(Playlist playlist, IndividualPanel panel) {
		this.playlist = playlist;
		individualPanel = panel;
	}

	public void run() {
		playlist.add(individualPanel);
	}

}

package gui.playlist;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class PlaylistScrollPane extends JScrollPane {
	Playlist playlist = null;
	
	public PlaylistScrollPane (Playlist playlist) {
		super(playlist);
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	}
	public PlaylistScrollPane() {
		super();
		this.setViewportView(getPlaylist());
		
	}
	public Playlist getPlaylist() {
		if (playlist==null) {
			playlist = new Playlist();
		}
		return playlist;
	}
	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}
	
}

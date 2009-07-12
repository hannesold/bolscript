package gui.playlist;

import javax.swing.JLayeredPane;

public abstract class PlayablePanel extends JLayeredPane{
	
	/**
	 * Inform the panel that it is not currently played.
	 * @param playing
	 */
	public abstract void setPlaying(boolean playing);
	
	
	/**
	 * 
	 * @return 
	 */
	public abstract boolean isPlaying();
	
	/**
	 * Unhighlight all cells
	 */
	public abstract void unHighlightCells();
	
	/**
	 * Highlight a cell (used when this cell is played)
	 * @param cell
	 */
	public abstract void highlightCell(int cell);



}

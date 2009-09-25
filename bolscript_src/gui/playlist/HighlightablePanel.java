package gui.playlist;

import javax.swing.JLayeredPane;

public abstract class HighlightablePanel extends JLayeredPane{
	
	/**
	 * Inform the panel that it is not currently highlighted.
	 * @param highlighted
	 */
	public abstract void setHighlighted(boolean highlighted);
	
	
	
	/**
	 * 
	 * @return 
	 */
	public abstract boolean isHighlighted();
	
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

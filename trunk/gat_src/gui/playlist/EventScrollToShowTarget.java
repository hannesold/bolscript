package gui.playlist;

import javax.swing.JComponent;

public class EventScrollToShowTarget implements Runnable {
	private JComponent scrollable;
	private JComponent scrollTarget;
	
	/**
	 * Scrolls to the target.
	 * @param scrollable The Container, which shall be scrolled.
	 * @param scrollTarget The contained compononent which shall be visible after scrolling
	 */
	public EventScrollToShowTarget(JComponent scrollable, JComponent scrollTarget) {
		super();
		this.scrollable = scrollable;
		this.scrollTarget = scrollTarget;
	}
	
	public void run() {
		scrollable.scrollRectToVisible(scrollTarget.getBounds());
	}
	
	
}

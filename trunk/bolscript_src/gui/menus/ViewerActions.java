package gui.menus;

import javax.swing.AbstractAction;

public class ViewerActions {
	public AbstractAction decreaseBundling, increaseBundling, decreaseFontsize, increaseFontsize, resetFontsize;
	public AbstractAction[] setLanguage;
	
	public ViewerActions(AbstractAction decreaseBundling,
			AbstractAction increaseBundling,
			AbstractAction decreaseFontsize,
			AbstractAction increaseFontsize, AbstractAction resetFontsize,
			AbstractAction[] setLanguage) {
		super();
		this.decreaseBundling = decreaseBundling;
		this.increaseBundling = increaseBundling;
		this.decreaseFontsize = decreaseFontsize;
		this.increaseFontsize = increaseFontsize;
		this.resetFontsize = resetFontsize;
		this.setLanguage = setLanguage;
	}
}

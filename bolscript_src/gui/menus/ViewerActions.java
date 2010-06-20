package gui.menus;

import javax.swing.AbstractAction;

public class ViewerActions {
	public AbstractAction showMore, showLess, decreaseBundling, increaseBundling, decreaseFontsize, increaseFontsize, resetFontsize, printPreview;
	public AbstractAction[] setLanguage;
	
	public ViewerActions(
			AbstractAction showMore,
			AbstractAction showLess,
			AbstractAction decreaseBundling,
			AbstractAction increaseBundling,
			AbstractAction decreaseFontsize,
			AbstractAction increaseFontsize, 
			AbstractAction resetFontsize,
			AbstractAction[] setLanguage, 
			AbstractAction printPreview) {
		super();
		this.showMore = showMore;
		this.showLess = showLess;
		this.decreaseBundling = decreaseBundling;
		this.increaseBundling = increaseBundling;
		this.decreaseFontsize = decreaseFontsize;
		this.increaseFontsize = increaseFontsize;
		this.resetFontsize = resetFontsize;
		this.setLanguage = setLanguage;
		this.printPreview = printPreview;
	}
}

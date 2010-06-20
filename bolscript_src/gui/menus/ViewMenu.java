package gui.menus;

import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.actions.DecreaseBundling;
import gui.bolscript.actions.DecreaseFontSize;
import gui.bolscript.actions.IncreaseBundling;
import gui.bolscript.actions.IncreaseFontSize;
import gui.bolscript.actions.PrintPreview;
import gui.bolscript.actions.ResetFontSize;
import gui.bolscript.actions.SetLanguage;
import gui.bolscript.actions.SmartResizeEnlarge;
import gui.bolscript.actions.SmartResizeShrink;
import gui.bolscript.actions.ToggleConsole;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bols.BolName;
import bolscript.config.GuiConfig;

public class ViewMenu extends JMenu {
	
	private EditorFrame editor = null;
	private CompositionFrame viewer = null;
	private BrowserFrame browser = null;
	
	public ViewMenu() {
		super("View");
	}
	public ViewMenu(EditorFrame editor) {
		this();
		this.editor = editor;
		this.viewer = editor.getCompositionFrame();
		init();
	}
	public ViewMenu(CompositionFrame viewer) {
		this();
		this.viewer = viewer;
		this.editor = viewer.getEditor();
		init();
	}
	public ViewMenu(BrowserFrame browser) {
		this();
		this.browser = browser;
		init();
	}
	
	public void init () {
		ViewerActions actions;
		if (viewer !=null) {
			 actions = viewer.getCompositionPanel().getViewerActions();
		} else {
			actions = new ViewerActions(
					new SmartResizeEnlarge(null),
					new SmartResizeShrink(null),
					new DecreaseBundling(null),
					new IncreaseBundling(null),
					new DecreaseFontSize(null),
					new IncreaseFontSize(null),
					new ResetFontSize(null), 
					new AbstractAction[0],
					new PrintPreview(null));
		}
		JMenuItem showMore = new JMenuItem(actions.showMore);
		this.add(showMore);
		showMore.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_I, GuiConfig.MENU_SHORTKEY_MASK));
		JMenuItem showLess = new JMenuItem(actions.showLess);
		
		this.add(showLess);
		showLess.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_U, GuiConfig.MENU_SHORTKEY_MASK));
		
		this.addSeparator();
		
		JMenuItem incrFonts = new JMenuItem(actions.increaseFontsize);
		this.add(incrFonts);
		incrFonts.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_I, KeyEvent.ALT_DOWN_MASK | GuiConfig.MENU_SHORTKEY_MASK));

		JMenuItem decrFonts = new JMenuItem (actions.decreaseFontsize);
		decrFonts.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_U, KeyEvent.ALT_DOWN_MASK | GuiConfig.MENU_SHORTKEY_MASK));
		this.add(decrFonts);
		
		this.add(actions.resetFontsize);
		
		this.addSeparator();
		JMenuItem incrBundling = new JMenuItem(actions.increaseBundling);
		incrBundling.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_I, KeyEvent.SHIFT_DOWN_MASK | GuiConfig.MENU_SHORTKEY_MASK));			
		this.add(incrBundling);
		
		JMenuItem decrBundling = new JMenuItem(actions.decreaseBundling);
		decrBundling.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_U, KeyEvent.SHIFT_DOWN_MASK | GuiConfig.MENU_SHORTKEY_MASK));
		this.add(decrBundling);
		

		
		this.addSeparator();
		addLanguages();
		
		this.addSeparator();
		JCheckBoxMenuItem printPreview = new JCheckBoxMenuItem(actions.printPreview);
		printPreview.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK | GuiConfig.MENU_SHORTKEY_MASK));
		this.add(printPreview);

	}
	
	public void addLanguages () {
		
		int [] numberKeys = new int [] {
				KeyEvent.VK_1,
				KeyEvent.VK_2, 
				KeyEvent.VK_3, 
				KeyEvent.VK_4, 
				KeyEvent.VK_5, 
				KeyEvent.VK_6, 
				KeyEvent.VK_7,
				KeyEvent.VK_8,
				KeyEvent.VK_9,
				KeyEvent.VK_0};
		
		for (int i=0; i < BolName.languagesCount; i++) {
			JMenuItem l;
			if (viewer != null) {
				l= new JMenuItem(viewer.getCompositionPanel().getViewerActions().setLanguage[i]);
			} else {
				l = new JMenuItem(new SetLanguage(null, i));
			}
			l.setAccelerator(KeyStroke.getKeyStroke(
			        numberKeys[i], GuiConfig.MENU_SHORTKEY_MASK));

			this.add(l);
			
		}
	
}
}

package gui.menus;

import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.actions.DecreaseBundling;
import gui.bolscript.actions.DecreaseFontSize;
import gui.bolscript.actions.IncreaseBundling;
import gui.bolscript.actions.IncreaseFontSize;
import gui.bolscript.actions.ResetFontSize;
import gui.bolscript.actions.ToggleConsole;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

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
					new DecreaseBundling(null),
					new IncreaseBundling(null),
					new DecreaseFontSize(null),
					new IncreaseFontSize(null),
					new ResetFontSize(null), 
					new AbstractAction[0]);
		}
		
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
		        KeyEvent.VK_I, GuiConfig.MENU_SHORTKEY_MASK));			
		this.add(incrBundling);
		
		JMenuItem decrBundling = new JMenuItem(actions.decreaseBundling);
		decrBundling.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_U, GuiConfig.MENU_SHORTKEY_MASK));
		this.add(decrBundling);
		

	}
}

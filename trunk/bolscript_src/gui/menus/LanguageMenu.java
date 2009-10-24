package gui.menus;

import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.ExitProgram;
import gui.bolscript.actions.OpenNew;
import gui.bolscript.actions.OpenPreferences;
import gui.bolscript.actions.Redo;
import gui.bolscript.actions.RefreshFromTablafolder;
import gui.bolscript.actions.SaveAs;
import gui.bolscript.actions.SaveChanges;
import gui.bolscript.actions.SetLanguage;
import gui.bolscript.actions.Undo;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bols.BolName;
import bolscript.Master;
import bolscript.config.GuiConfig;

public class LanguageMenu extends JMenu {
	
	private EditorFrame editor = null;
	private CompositionFrame viewer = null;
	private BrowserFrame browser = null;
	
	public LanguageMenu() {
		super("Language");
	}
	public LanguageMenu(EditorFrame editor) {
		this();
		this.editor = editor;
		this.viewer = editor.getCompositionFrame();
		init();
	}
	public LanguageMenu(CompositionFrame viewer) {
		this();
		this.viewer = viewer;
		this.editor = viewer.getEditor();
		init();
	}
	public LanguageMenu(BrowserFrame browser) {
		this();
		this.browser = browser;
		init();
	}
	
	public void init () {
		
			int [] numberKeys = new int [] {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7,KeyEvent.VK_8,KeyEvent.VK_9,KeyEvent.VK_0};
			
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

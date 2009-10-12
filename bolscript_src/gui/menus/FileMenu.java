package gui.menus;

import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.ExitProgram;
import gui.bolscript.actions.ExportPdf;
import gui.bolscript.actions.OpenNew;
import gui.bolscript.actions.OpenPreferences;
import gui.bolscript.actions.RefreshFromTablafolder;
import gui.bolscript.actions.RemoveSelected;
import gui.bolscript.actions.SaveAs;
import gui.bolscript.actions.SaveChanges;
import gui.bolscript.actions.ToggleConsole;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bolscript.Master;
import bolscript.config.Config;

public class FileMenu extends JMenu {
	
	private EditorFrame editor = null;
	private CompositionFrame viewer = null;
	private BrowserFrame browser = null;
	
	public FileMenu() {
		super("File");
	}
	public FileMenu(EditorFrame editor) {
		this();
		this.editor = editor;
		this.viewer = editor.getCompositionFrame();
		init();
	}
	public FileMenu(CompositionFrame viewer) {
		this();
		this.viewer = viewer;
		this.editor = viewer.getEditor();
		init();
	}
	public FileMenu(BrowserFrame browser) {
		this();
		this.browser = browser;
		init();
	}
	
	public void init () {

		this.add(new OpenNew());
		
		this.addSeparator();
		
		JMenuItem saver = new JMenuItem(new SaveChanges(editor,false));
		saver.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, Config.MENU_SHORTKEY_MASK));
		this.add(saver);
		
		JMenuItem saveAs = new JMenuItem(new SaveAs(editor));
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
				(java.awt.event.InputEvent.SHIFT_MASK | 
						Config.MENU_SHORTKEY_MASK)));
		this.add(saveAs);
		
		JMenuItem export = new JMenuItem(new ExportPdf(viewer));
		export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 
				Config.MENU_SHORTKEY_MASK));
		this.add(export);
		
		this.addSeparator();
		
		JMenuItem closer = new JMenuItem(new CloseEditor(editor));
		closer.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_W, Config.MENU_SHORTKEY_MASK));
		this.add(closer);
		
		
		//if (browser != null) {
			this.addSeparator();
			
			JMenuItem remover = new JMenuItem(new RemoveSelected(browser));
			
			int remKey = (Config.OS == Config.MAC) ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;
			remover.setAccelerator(KeyStroke.getKeyStroke(
			        remKey, Config.MENU_SHORTKEY_MASK));
			
			this.add(remover);
		//}
		
		this.addSeparator();
		this.add(new RefreshFromTablafolder());
		
		JMenuItem toggleErrorConsole = new JMenuItem(ToggleConsole.getStandard());
		toggleErrorConsole.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_T, Config.MENU_SHORTKEY_MASK));
		
		this.addSeparator();
		this.add(toggleErrorConsole);
		
		
		
		
		if (!Master.master.isRunningAsMacApplication()) {
			this.add(new OpenPreferences());		
			this.addSeparator();
			this.add(new ExitProgram());
		}
	}

}

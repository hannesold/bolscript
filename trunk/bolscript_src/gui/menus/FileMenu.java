package gui.menus;

import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.actions.CheckForUpdates;
import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.ExitProgram;
import gui.bolscript.actions.ExportPdf;
import gui.bolscript.actions.OpenExistingFile;
import gui.bolscript.actions.OpenNew;
import gui.bolscript.actions.OpenPreferences;
import gui.bolscript.actions.RefreshFromLibraryfolder;
import gui.bolscript.actions.RemoveSelected;
import gui.bolscript.actions.RevealCompositionInOSFileManager;
import gui.bolscript.actions.SaveAs;
import gui.bolscript.actions.SaveChanges;
import gui.bolscript.actions.ToggleConsole;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bolscript.Master;
import bolscript.config.Config;
import bolscript.config.GuiConfig;

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
		
		JMenuItem openExistingFile = new JMenuItem(new OpenExistingFile(browser));
		openExistingFile.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_O, GuiConfig.MENU_SHORTKEY_MASK));
		this.add(openExistingFile);
		
		this.addSeparator();
		
		JMenuItem saver = new JMenuItem(new SaveChanges(editor,false));
		saver.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, GuiConfig.MENU_SHORTKEY_MASK));
		this.add(saver);
		
		JMenuItem saveAs = new JMenuItem(new SaveAs(editor));
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
				(java.awt.event.InputEvent.SHIFT_MASK | 
						GuiConfig.MENU_SHORTKEY_MASK)));
		this.add(saveAs);
		
		JMenuItem export = new JMenuItem(new ExportPdf(viewer));
		export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 
				GuiConfig.MENU_SHORTKEY_MASK));
		this.add(export);
		
		this.addSeparator();
		
		JMenuItem closer = new JMenuItem(new CloseEditor(editor));
		closer.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_W, GuiConfig.MENU_SHORTKEY_MASK));
		this.add(closer);
		
		
		//if (browser != null) {
			this.addSeparator();
			
			JMenuItem remover = new JMenuItem(new RemoveSelected(browser));
			
			int remKey = (Config.operatingSystem == Config.OperatingSystems.Mac) ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;
			remover.setAccelerator(KeyStroke.getKeyStroke(
			        remKey, GuiConfig.MENU_SHORTKEY_MASK));
			
			this.add(remover);
		//}
		
		this.addSeparator();
		
		JMenuItem revealer = new JMenuItem(new RevealCompositionInOSFileManager(browser, editor, null));
		revealer.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, GuiConfig.MENU_SHORTKEY_MASK | KeyEvent.ALT_DOWN_MASK));
		this.add(revealer);
		
		this.addSeparator();
		this.add(new RefreshFromLibraryfolder());
		
		JMenuItem toggleErrorConsole = new JMenuItem(ToggleConsole.getStandard());
		toggleErrorConsole.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_T, GuiConfig.MENU_SHORTKEY_MASK));
		
		this.addSeparator();
		this.add(toggleErrorConsole);
		
		this.addSeparator();
		this.add(new CheckForUpdates());		
		
		if (!Master.master.isRunningAsMacApplication()) {
			/**
			 * When running as mac application the preferences and quit menu items are in the application menu,
			 * not in the file menu.
			 */
			this.add(new OpenPreferences());

			this.addSeparator();
			this.add(new ExitProgram());
		} 
		
		
		
	}
	
	

}

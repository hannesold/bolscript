package gui.menus;

import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.ExitProgram;
import gui.bolscript.actions.OpenNew;
import gui.bolscript.actions.OpenPreferences;
import gui.bolscript.actions.Redo;
import gui.bolscript.actions.RefreshFromLibraryfolder;
import gui.bolscript.actions.SaveAs;
import gui.bolscript.actions.SaveChanges;
import gui.bolscript.actions.Undo;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bolscript.Master;

public class EditMenu extends JMenu {
	
	private EditorFrame editor = null;
	private CompositionFrame viewer = null;
	private BrowserFrame browser = null;
	
	public EditMenu() {
		super("Edit");
	}
	public EditMenu(EditorFrame editor) {
		this();
		this.editor = editor;
		this.viewer = editor.getCompositionFrame();
		init();
	}
	public EditMenu(CompositionFrame viewer) {
		this();
		this.viewer = viewer;
		this.editor = viewer.getEditor();
		init();
	}
	public EditMenu(BrowserFrame browser) {
		this();
		this.browser = browser;
		init();
	}
	
	public void init () {
		JMenuItem undo;
		JMenuItem redo;
		if (editor != null) {
			undo = new JMenuItem(new Undo(editor.getUndoManager()));
			redo = new JMenuItem(new Redo(editor.getUndoManager()));
		} else {
			undo = new JMenuItem(new Undo(null));
			redo = new JMenuItem(new Redo(null));
		}
		undo.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));	
		redo.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.add(undo);
		this.add(redo);
	}
}

package gui.bolscript;


import gui.bolscript.actions.CloseEditor;
import gui.menus.EditMenu;
import gui.menus.FileMenu;
import gui.menus.LanguageMenu;
import gui.menus.ViewMenu;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import basics.GUI;
import bols.BolBase;
import bols.BolBaseGeneral;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import bolscript.config.Config;

/**
 * This is the Composition Viewer Frame.
 * @author hannes
 *
 */
public class CompositionFrame extends JFrame implements WindowListener, CompositionChangedListener {


		BolBaseGeneral bolBase;
		CompositionPanel compositionPanel;
		JScrollPane scrollPane;
		EditorFrame editor = null;
		
	
		public CompositionFrame(Composition comp, Dimension size) {
			this(size);
			this.setTitle(comp.getName());
			compositionPanel.renderComposition(comp);
			comp.addChangeListener(this);
		}
		
		public CompositionFrame(Dimension size) {
			super();
			GUI.setAllSizes(this, size);
			
			this.setTitle("");
			bolBase = BolBase.standard();
			compositionPanel = new CompositionPanel(size, Config.standardLanguage);
			
			JPanel p = new JPanel();
			scrollPane = new JScrollPane(p);
			this.setContentPane(scrollPane);
			p.add(compositionPanel);
			addWindowListener(this);
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			initMenuBar();
		}
		
		public void initMenuBar() {
			JMenuBar menuBar = new JMenuBar();
			menuBar.add(new FileMenu(this));
			menuBar.add(new EditMenu(this));
			menuBar.add(new ViewMenu(this));
			menuBar.add(new LanguageMenu(this));
			/*JMenu fileMenu = new JMenu("File");
			fileMenu.add(new ExportPdf(this));
			
			menuBar.add(fileMenu);
			menuBar.add(compositionPanel.getLanguageMenu());
			menuBar.add(compositionPanel.getViewMenu());*/
			
			this.setJMenuBar(menuBar);
		}
		
		public void showComposition(Composition comp){
			this.setTitle(comp.getName());
			compositionPanel.renderComposition(comp);
		}


		public CompositionPanel getCompositionPanel() {
			return compositionPanel;
		}

		public void setCompositionPanel(CompositionPanel compositionPanel) {
			this.compositionPanel = compositionPanel;
		}

		public EditorFrame getEditor() {
			return editor;
		}

		public void setEditor(EditorFrame editor) {
			this.editor = editor;
		}

		public void windowClosing(WindowEvent e) {
			if (editor != null) {
				(new CloseEditor(editor)).closeEditor();
			}
			//System.exit( 0 );	
		}
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}

		public void setTitle(String s) {
			super.setTitle("Composition Viewer - " + s);
		}
		public void compositionChanged(
				CompositionChangeEvent compositionChangeEvent) {
			this.setTitle(compositionChangeEvent.getComposition().getName());
		}
		
		/**
		 * Creates the actual PDF document
		 * @param filename TODO
		 */
		public void createPdf(String filename, boolean shapes) {
		 compositionPanel.createPdf(filename, shapes);
		}


	}



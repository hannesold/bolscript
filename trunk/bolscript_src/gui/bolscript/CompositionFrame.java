package gui.bolscript;


import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.DecreaseBundling;
import gui.bolscript.actions.DecreaseFontSize;
import gui.bolscript.actions.ExportPdf;
import gui.bolscript.actions.IncreaseBundling;
import gui.bolscript.actions.IncreaseFontSize;
import gui.bolscript.actions.ResetFontSize;
import gui.bolscript.actions.SetLanguage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import basics.GUI;
import bols.BolBase;
import bols.BolBaseGeneral;
import bols.BolName;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import config.Config;
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
		
		private void initMenuBar() {
			JMenuBar menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			fileMenu.add(new ExportPdf(this));
			

			
			menuBar.add(fileMenu);
			menuBar.add(compositionPanel.getLanguageMenu());
			menuBar.add(compositionPanel.getViewMenu());
			
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



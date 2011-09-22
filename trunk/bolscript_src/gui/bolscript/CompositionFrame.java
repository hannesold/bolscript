package gui.bolscript;


import gui.bolscript.actions.CloseEditor;
import gui.bolscript.composition.CompositionPanel;
import gui.menus.EditMenu;
import gui.menus.FileMenu;
import gui.menus.ViewMenu;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import basics.GUI;
import bols.BolBase;
import bols.BolBaseGeneral;
import bols.tals.TalBase;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import bolscript.config.UserConfig;

/**
 * This is the Composition Viewer Frame.
 * @author hannes
 *
 */
public class CompositionFrame extends JFrame implements WindowListener, CompositionChangedListener {

		private static final long serialVersionUID = -977721216043612327L;
		
		BolBaseGeneral bolBase;
		CompositionPanel compositionPanel;
		JScrollPane scrollPane;
		EditorFrame editor = null;
		TalBase talBase;
	
		/**
		 * Is hidden by default. Call showLater().
		 * @param comp
		 * @param size
		 * @param talBase
		 */
		public CompositionFrame(Composition comp, Dimension size, TalBase talBase) {
			this(size, talBase);
			this.setTitle(comp.getName());
			compositionPanel.renderComposition(comp, false);
			comp.addChangeListener(this);
			
		}
		
		public CompositionFrame(Dimension size, TalBase talBase) {
			super();
			this.setVisible(false);
			
			GUI.setAllSizes(this, size);
			
			this.talBase = talBase;
			
			this.setTitle("");
			bolBase = BolBase.standard();
			compositionPanel = new CompositionPanel(size, UserConfig.standardLanguage, talBase, this);
			
			JPanel p = new JPanel();
			scrollPane = new JScrollPane(p);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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

			this.setJMenuBar(menuBar);
		}
		
		public void showComposition(Composition comp){
			this.setTitle(comp.getName());
			compositionPanel.renderComposition(comp, false);
		}


		public CompositionPanel getCompositionPanel() {
			return compositionPanel;
		}

		public void setCompositionPanel(CompositionPanel compositionPanel) {
			this.compositionPanel = compositionPanel;
			compositionPanel.setCompositionFrame(this);
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
			compositionPanel.createPdf(filename, shapes, true);
		}

		public void showLater() {
			EventQueue.invokeLater(new Runnable() {
				public void run(){
					setVisible(true);
				}
			});
			
		}


	}



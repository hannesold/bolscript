package bolscript;
	
import gui.bols.SequencePanel;
import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.CompositionPanel;
import gui.bolscript.EditorFrame;
import gui.bolscript.FilterPanel;
import gui.bolscript.SearchPanel;
import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.OmmitChangesAndClose;
import gui.bolscript.actions.SaveChanges;
import gui.bolscript.dialogs.CouldNotBeRemovedDialog;
import gui.bolscript.dialogs.PreferencesDialog;
import gui.bolscript.dialogs.SaveChangesDialog;
import gui.bolscript.tables.CompositionTableModel;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import midi.MidiStationSimple;
import basics.Debug;
import basics.FileWriteException;
import basics.GUI;
import basics.ZipTools;
import bols.BolBase;
import bols.tals.TalBase;
import bols.tals.TalDynamic;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.State;
import bolscript.config.Config;
import bolscript.config.ConfigChangeEvent;
import bolscript.config.ConfigChangeListener;
public class Master implements ConfigChangeListener{//implements ApplicationListener{//extends JFrame implements WindowListener {

		public static Master master;
		
		protected static boolean runningAsMacApplication;
		
		//public static Application application;
		
		CompositionBase compositionBase;
		CompositionTableModel compTableModel;
		
		ArrayList<EditorFrame> editors;
		BrowserFrame browserFrame;
		
		PreferencesDialog prefsDialog;

		CompositionPanel compositionPanel;
		ArrayList<CompositionFrame> compositionFrames;
		private FilterPanel filterPanel;
		private SearchPanel searchPanel;
		
		static Debug debug = new Debug(Master.class);
		
		
		public Master () {
			runningAsMacApplication = false;
		}	
		
		/**
		 * Sets the class visibilities in the Debug class.
		 * @param mute
		 */
		public void initDebug() {
			Debug.init();
			Debug.initClassMaps(new Class[]{
				
				Config.class,
				
				ZipTools.class
			}, new Class[]{
					Master.class,
				Reader.class,
				CompositionBase.class,
				FilterPanel.class,
				Composition.class,
				TalDynamic.class,
				CompositionPanel.class,
				SequencePanel.class,
				BolBase.class
			});
			Debug.setExclusivelyMapped(true);
		}
		
		public void init() {
			GUI.setNativeLookAndFeel();
			
			initDebug();
			//debug.showErrorConsole();
			
			GUI.init();
			
			Config.init();
			
			//Turn on midi or not
			/*try {
				MidiStationSimple.init();
			} catch (Exception e1) {
				Debug.critical(this, "MidiStationSimple could not be initialised");
				e1.printStackTrace();
			}*/
			
			
			prefsDialog = new PreferencesDialog();
			
			if (Config.firstRun) {
				Debug.temporary(this, "first run");
				prefsDialog.setModal(true);
				prefsDialog.setVisible(true);
			}
			
			BolBase.init(this.getClass());

			TalBase.init();
			
			
			compositionBase = new CompositionBase();
			compositionBase.addFolderRecursively(Config.pathToTalsNoSlash);
			compositionBase.addFolderRecursively(bolscript.config.Config.pathToCompositionsNoSlash);
			compTableModel = new CompositionTableModel(compositionBase);
			
			searchPanel = new SearchPanel();
			filterPanel = new FilterPanel(compositionBase, searchPanel);
			compositionBase.setFilterGUI(filterPanel);
			
			try {
				browserFrame = new BrowserFrame(new Dimension(800,800),compTableModel, filterPanel, searchPanel);
				browserFrame.setVisible(true);
				editors = new ArrayList<EditorFrame>();//new EditorFrame(new Dimension(400,800));
				compositionFrames = new ArrayList<CompositionFrame>();//new CompositionFrame(new Dimension(800,600),false);
				
			} catch (Exception e) {
				debug.critical("something went wrong");
				
			}
			
			Config.addChangeListener(this);
			debug.temporary("browserFrame established");
			
			
		}
		
		/**
		 * @param args
		 */
		public static void main(String[] args) {

			int i=0;
			while (i<args.length) {
				if (args[i].equalsIgnoreCase("showLayout")) {
					GUI.showLayoutStructure = true;
					debug.debug("showing layout");
				}
				if (args[i].equalsIgnoreCase("noDebug")) {
					Debug.setMute(true);
				}
				i++;
			}	
			master = new Master();
			master.init();
			
		}

		/**
		 * Is called whenever Config has changed (at the moment when tablaFolder was changed)
		 */
		public void configChanged(ConfigChangeEvent e) {
			refreshFromTablafolder();
		}

		/**
		 * Re-Inits BolBase and refreshes compositionbase from the tablaFolder
		 */
		public void refreshFromTablafolder() {
			BolBase.init(this.getClass());
			compositionBase.addFolderRecursivelyAndGetOutdated(Config.pathToTals);
			ArrayList<Composition> outdated = 
				compositionBase.addFolderRecursivelyAndGetOutdated(Config.pathToCompositions);
			for (Composition c: outdated) {
				if (c.getDataState() != State.NEW) {
					compositionBase.removeComposition(c);
				}
			}
					
			
		}

		public CompositionPanel getCompositionPanel() {
			return compositionPanel;
		}

		public void setCompositionPanel(CompositionPanel compositionPanel) {
			this.compositionPanel = compositionPanel;
		}
		
		public void saveEditor(EditorFrame editor) {
			saveEditorAs(editor, editor.getComposition().getLinkLocal());				
		}
		
		public void saveEditorAs(EditorFrame editor, String filename) {
			Composition comp = editor.getComposition();
			comp.setRawData(editor.getText());
			comp.extractInfoFromRawData();
			
			try {
				compositionBase.saveCompositionToFile(comp, filename);
				editor.getComposition().backUpRawData();
				debug.debug("Saved composition " + comp);
			} catch (FileWriteException ex) {
				debug.critical("Could not save composition: " + ex);
			}

		}
		
		public void closeEditor(EditorFrame editor) {
			int i = editors.indexOf(editor);
			if (i>=0) {
				debug.debug("close Editor  " +i + ": " + editor);
				
				Composition c = editors.get(i).getComposition();
				
				if (c.getDataState() == State.NEW) { //if it has not been saved!
					compositionBase.removeComposition(c);
				}
				
				c.getDataState().close(c);
				
				editors.get(i).dispose();
				compositionFrames.get(i).dispose();
				
				c.removeChangeListener(editors.get(i));
				c.removeChangeListener(compositionFrames.get(i));
				
				editors.remove(i);
				compositionFrames.remove(i);
			}
			
		}
		
		public void clickOnCompositionList(MouseEvent e) {
			if (e.getClickCount() == 2){
		         debug.debug(" double click OnCompositionList" );
		         debug.debug(" src " + e.getSource());
		         
		         int[] selectedRows = ((JTable) e.getSource()).getSelectedRows();
		         for (int i=0; i < selectedRows.length; i++) {
		        	 JTable table = (JTable) e.getSource();
		        	 CompositionTableModel model = (CompositionTableModel) table.getModel();
		        	 int index = table.getRowSorter().convertRowIndexToModel(selectedRows[i]);
		        	 
		        	 Composition comp =  model.getComposition(index);
		        	 openEditor(comp);
		         }
		    } else {
		    	debug.debug( "single click");
		    }
		}

		public ArrayList<EditorFrame> getEditors(Composition comp) {
			ArrayList<EditorFrame> compEditors = new ArrayList<EditorFrame>();
			for (int i=0; i<editors.size();i++) {
				if (editors.get(i).getComposition() == comp) compEditors.add(editors.get(i));
			}
			return compEditors;
		}

		/**
		 * Opens an editor for the coomposition.
		 * @param comp The composition to be edited.
		 */
		public void openEditor(Composition comp) {
			debug.debug("Opening comp: " + comp);
       	 if (comp.getDataState() != State.EDITING) {
       		 comp.establishRawData();
       		 if (comp.establishRawData()) {
       			 comp.backUpRawData();
       		 
       			 comp.getDataState().open(comp);
       			 
       			 CompositionFrame compositionFrame = new CompositionFrame(comp, new Dimension(600,700));
       			 
       			 EditorFrame editor = new EditorFrame(comp, new Dimension(390,700));
       			
       			 editor.setCompositionFrame(compositionFrame);	 
       			 editor.setLocation(GUI.topRight(browserFrame));
       			 editor.arrangeCompositionFrame();
       			 
       			 
       			 compositionFrame.setEditor(editor);
       			 
       			 // add to list of composition frames and editors
       			 compositionFrames.add(compositionFrame); 
       			 editors.add(editor);
       			 
       			 
       			 editor.setVisible(true);
       			 compositionFrame.setVisible(true);
    			}
  
		} else if (comp.getDataState() == State.EDITING){
			getEditors(comp).get(0).setVisible(true);
		}
		}
       	 
		public void showSaveChangesDialog(EditorFrame editor) {
			
			debug.debug("show SaveChangesDialog for " + editor);
			editor.saveChangesDialog = new SaveChangesDialog(editor);
			editor.saveChangesDialog.setVisible(true);

			debug.debug("has decision for " + editor);
			
			switch (editor.saveChangesDialog.getChoice()) {
			case SaveChangesDialog.SAVE:
				new SaveChanges(editor, true).actionPerformed(null);
				break;
			case SaveChangesDialog.NO:
				new OmmitChangesAndClose(editor).actionPerformed(null);
				break;
			default:
				//cancelled
				//case JOptionPane.CLOSED_OPTION
				//case JOptionPane.CANCEL_OPTION: 
				break;
			}
		}


		/**
		 * Removes the compositions from the compositionBase and deletes them if requested.
		 */
		public void removeCompositions(ArrayList<Composition> comps, boolean andDelete) {
			ArrayList<Composition> failed = new ArrayList<Composition>();
			for (Composition comp: comps) {
				
				if (comp.getDataState() != State.EDITING) {
					compositionBase.removeComposition(comp, andDelete);
				} else {
					failed.add(comp);
				}
				
			}
			
			if (failed.size()>0) {
				CouldNotBeRemovedDialog dialog = new CouldNotBeRemovedDialog(browserFrame, failed);
				dialog.setVisible(true);
				dialog.dispose();
			}
		}

		/**
		 * Opens a new editor with an empty composition (only filled with a little template)
		 * The new compositions status is set to NEW, and it is instantly added to the compositionBase
		 */
		public void openNewComposition() {

			String template = "Editor: Unknown\nGharana: Punjab\nTal: Teental\nType: Unknown\n\n";
		
			Composition comp = new Composition(template);
			if (comp.isTal()) comp = new TalDynamic(comp);
			
			comp.setDataState(State.NEW);
			comp.setLinkLocal(compositionBase.generateFilename(comp, Config.bolscriptSuffix));
			compositionBase.addComposition(comp);
			
			openEditor(comp);
			
			
			
			
			
		}

		/**
		 * Tries to close the program safely:
		 * - attempts to close all open editors first
		 */
		public void exit() {
			debug.temporary("exiting");
			boolean closeInterrupted = false;
			int i=0;
			while (i < editors.size() && !closeInterrupted) {
				EditorFrame e = editors.get(i);
				Composition c = e.getComposition();
				CloseEditor closer = new CloseEditor(e);
				closer.actionPerformed(null);
				if (c.getDataState() == State.EDITING || c.getDataState() == State.NEW) {
					//the composition was not closed, exit has been cancelled!
					closeInterrupted = true;
				}
			}
			if (!closeInterrupted) {
				debug.temporary("storing config....");
				boolean configStored = false;
				try {
					Config.storePreferences();
					configStored = true;
				} catch (Exception e) {
					configStored = false;
				}
				if (configStored) {
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(browserFrame, "Sorry, the configuration could not be stored, you will have to reenter the tabla folder at the next program launch.", "Configuration not stored", JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}
			}
			
			if (MidiStationSimple.getStandard() != null) {
				MidiStationSimple.getStandard().shutDown();
			}
			
		}

		public boolean isRunningAsMacApplication() {
			return runningAsMacApplication;
		}
		
		public void showPreferences() {
			prefsDialog.setModal(true);
			prefsDialog.setVisible(true);
		}


		

	}


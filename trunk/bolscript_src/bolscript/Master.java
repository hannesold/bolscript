package bolscript;

import gui.bolscript.BolBaseFrame;
import gui.bolscript.BrowserFrame;
import gui.bolscript.CompositionFrame;
import gui.bolscript.EditorFrame;
import gui.bolscript.FilterPanel;
import gui.bolscript.SearchPanel;
import gui.bolscript.actions.CloseEditor;
import gui.bolscript.actions.OmmitChangesAndClose;
import gui.bolscript.actions.SaveChanges;
import gui.bolscript.composition.CompositionPanel;
import gui.bolscript.dialogs.CouldNotBeRemovedDialog;
import gui.bolscript.dialogs.LoadingTablafolder;
import gui.bolscript.dialogs.OpenOrImportExistingFileDialog;
import gui.bolscript.dialogs.PreferencesDialog;
import gui.bolscript.dialogs.SaveChangesDialog;
import gui.bolscript.sequences.SequencePanel;
import gui.bolscript.tables.CompositionTableModel;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;

import javax.swing.JOptionPane;

import midi.MidiStationSimple;
import basics.Debug;
import basics.FileManager;
import basics.FileReadException;
import basics.FileWriteException;
import basics.GUI;
import basics.ZipTools;
import bols.BolBase;
import bols.tals.TalDynamic;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.DataState;
import bolscript.config.Config;
import bolscript.config.ConfigChangeEvent;
import bolscript.config.ConfigChangeListener;
import bolscript.config.GuiConfig;
import bolscript.config.PreferenceKeys;
import bolscript.config.UserConfig;
import bolscript.packets.types.HistoryEntry;
import bolscript.packets.types.HistoryOperationType;
import bolscript.packets.types.PacketTypeFactory;
public class Master implements ConfigChangeListener{//implements ApplicationListener{//extends JFrame implements WindowListener {

	public static Master master;

	protected static boolean runningAsMacApplication;

	//public static Application application;

	CompositionBase compositionBase;
	CompositionTableModel compTableModel;

	ArrayList<EditorFrame> editors;
	BrowserFrame browserFrame;
	BolBaseFrame bolBaseFrame;

	PreferencesDialog prefsDialog;

	CompositionPanel compositionPanel;
	ArrayList<CompositionFrame> compositionFrames;
	private FilterPanel filterPanel;
	private SearchPanel searchPanel;

	private LoadingTablafolder loadingFrame ;

	static Debug debug = new Debug(Master.class);


	public Master () {
		runningAsMacApplication = false;
	}	

	/**
	 * Sets the class visibilities in the Debug class.
	 * @param mute
	 */
	@SuppressWarnings("unchecked")
	public void initDebug() {
		Debug.init();
		Debug.initClassMaps(new Class[]{
				Config.class,
				ZipTools.class
		}, new Class[]{
				Master.class,
				FileManager.class,
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

		SplashScreen splashScreen = SplashScreen.getSplashScreen();
		if (splashScreen==null) {
			debug.critical("SplashScreen not loaded!");
		}

		GUI.init();

		PacketTypeFactory.init();

		//force previous uninstall!
		/* 
		try {
			UserConfig.uninstall();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		Config.init();

		//Turn on midi or not
		/*try {
				MidiStationSimple.init();
			} catch (Exception e1) {
				Debug.critical(this, "MidiStationSimple could not be initialised");
				e1.printStackTrace();
			}*/


		prefsDialog = new PreferencesDialog();

		if (UserConfig.firstRun) {
			Debug.temporary(this, "first run");
			prefsDialog.setModal(true);
			prefsDialog.setVisible(true);
		}

		EventQueue.invokeLater(new Runnable() { public void run() {
			showLoadingframeThenLoad();
		}});
	}

	public void showLoadingframeThenLoad() {
		//loadingFrame = new LoadingTablafolder();
		//loadingFrame.setVisible(true);

		EventQueue.invokeLater(new Runnable() { public void run() {
			initGui();
		}});
	}

	public void initGui(){

		BolBase.init(this.getClass());

		compositionBase = new CompositionBase();			
		compositionBase.addFolderRecursively(Config.pathToTalsNoSlash);
		compositionBase.addFolderRecursively(bolscript.config.Config.pathToCompositionsNoSlash);
		compTableModel = new CompositionTableModel(compositionBase);

		filterPanel = new FilterPanel(compositionBase);
		compositionBase.setFilterGUI(filterPanel);

		try {

			browserFrame = new BrowserFrame(GuiConfig.getBrowserFrameDimension(),compTableModel, filterPanel);
			browserFrame.setVisible(true);
			Image icon = GuiConfig.getWindowsFrameIcon();
			debug.temporary("imageIcon: " +icon);
			if (icon != null) browserFrame.setIconImage(icon);
			editors = new ArrayList<EditorFrame>();//new EditorFrame(new Dimension(400,800));
			compositionFrames = new ArrayList<CompositionFrame>();//new CompositionFrame(new Dimension(800,600),false);

			//bolBaseFrame = new BolBaseFrame(new Dimension(800,800));
			//bolBaseFrame.setVisible(true);

		} catch (Exception e) {
			debug.critical("something went wrong");
			System.exit(0);
		}
		//loadingFrame.setVisible(false);
		Config.addChangeListener(this);
		debug.temporary("browserFrame established");

		//loadingFrame.setVisible(false);
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
		if (e.hasChanged(PreferenceKeys.TABLA_FOLDER)) {
			refreshFromTablafolder();
		}
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
			if (c.getDataState() != DataState.NEW) {
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


	public void revealFileInOSFileManager(String filename) {
		//windows implementation
		if (Config.OS == Config.WINDOWS) {
			try {

				/**
				 * fix a problem which is unclear to me at the moment, where
				 * \\ apears instead of \ in a path after saving a new composition.
				 */
				filename = filename.replaceAll("[\\\\]+", Matcher.quoteReplacement("\\"));

				/**
				 * Tell Windows Explorer to show the file
				 */
				Runtime.getRuntime().exec("explorer.exe /select, \""+filename+"\"");
			} catch (IOException e) {
				debug.critical("could not show file in explorer, " + e);
				e.printStackTrace();
			}
		}

		//mac implementation is done in MasterMac
	}

	public void saveEditorAs(EditorFrame editor, String filename) {
		Composition comp = editor.getComposition();
		comp.setEditableRawData(editor.getText());
		comp.extractInfoFromEditableRawData();

		if (comp.hasChangedSinceBackup() && comp.getDataState() == DataState.EDITING) {
			//String user = comp.getEnteredUser();
			String user = UserConfig.getUserId();

			comp.getHistory().add(new HistoryEntry(Calendar.getInstance().getTime(), HistoryOperationType.MODIFIED, user));			
		} else if (comp.getDataState() == DataState.NEW) {
			if (UserConfig.userIdIsOnDefaultSetting()) {
				String user = comp.getEnteredUser();
				if (user != null) {
					if (!user.equals(UserConfig.getUserId())) {
						//TODO ask if user shall be saved.
						UserConfig.setUserIdAfterValidation(user);
					}
				}
			}
		}

		try {
			compositionBase.saveCompositionToFile(comp, filename);
			editor.getComposition().backUpEditableRawData();
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

			if (c.getDataState() == DataState.NEW) { //if it has not been saved!
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

	}

	public ArrayList<EditorFrame> getEditors(Composition comp) {
		ArrayList<EditorFrame> compEditors = new ArrayList<EditorFrame>();
		for (int i=0; i<editors.size();i++) {
			if (editors.get(i).getComposition() == comp) compEditors.add(editors.get(i));
		}
		return compEditors;
	}


	/**
	 * 
	 */
	public void openSomeExistingFile(File file) {

		Composition comp = null;
		try {
			comp = compositionBase.addFile(file);
		} catch (FileReadException e) {			
			e.printStackTrace();
		}
		if (comp != null) {
			String path = comp.getLinkLocal();
			if (!path.startsWith(UserConfig.tablaFolder)) {
				//want to import?
				OpenOrImportExistingFileDialog question = new OpenOrImportExistingFileDialog(browserFrame);
				question.setVisible(true); // (modal)
				switch (question.getChoice()) {
				case (OpenOrImportExistingFileDialog.IMPORT):
					//add the file to a generated place
					String completeNewFilename = CompositionBase.generateFilename(comp, Config.bolscriptSuffix, true);
					comp.setLinkLocal(completeNewFilename);
					
					try {
						compositionBase.saveCompositionToFile(comp, comp.getLinkLocal());
					} catch (FileWriteException e) {
						debug.critical("File could not be saved under " + comp.getLinkLocal());
					}
					break;
				case(OpenOrImportExistingFileDialog.JUST_OPEN):
					//do nothing
					break;
				}
			} else {
				//is already in tabla folder
			}
			openEditor(comp);
		}


	}

	/**
	 * Opens an editor for the coomposition.
	 * @param comp The composition to be edited.
	 */
	public void openEditor(Composition comp) {
		debug.debug("Opening comp: " + comp);
		if (comp.getDataState() != DataState.EDITING) {
			comp.establishRawData();
			if (comp.establishRawData()) {
				comp.backUpEditableRawData();

				comp.getDataState().open(comp);

				CompositionFrame compositionFrame = new CompositionFrame(comp, GuiConfig.getCompositionViewerSize(), compositionBase);
				Image icon = GuiConfig.getWindowsFrameIcon();
				if (icon != null) compositionFrame.setIconImage(icon);

				EditorFrame editor = new EditorFrame(comp, GuiConfig.getEditorSize());
				if (icon != null) editor.setIconImage(icon);

				editor.setCompositionFrame(compositionFrame);	 
				compositionFrame.setEditor(editor);
				editor.initMenuBar();
				compositionFrame.initMenuBar();


				editor.setLocation(GUI.topRight(browserFrame));
				editor.arrangeCompositionFrame();

				// add to list of composition frames and editors
				compositionFrames.add(compositionFrame); 
				editors.add(editor);

				editor.showLater();
				compositionFrame.showLater();
			}

		} else if (comp.getDataState() == DataState.EDITING){
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


	public BrowserFrame getBrowser() {
		return browserFrame;
	}

	/**
	 * Removes the compositions from the compositionBase and deletes them if requested.
	 */
	public void removeCompositions(ArrayList<Composition> comps, boolean andDelete) {
		ArrayList<Composition> failed = new ArrayList<Composition>();
		for (Composition comp: comps) {

			if (comp.getDataState() != DataState.EDITING) {
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
		String template = "Editor: " + UserConfig.getUserId() + "\nGharana: Punjab\nType: Unknown\n\nTal: Teental\n";

		Composition comp = new Composition(template, compositionBase);
		comp.setDataState(DataState.NEW);
		comp.setLinkLocal(CompositionBase.generateFilename(comp, Config.bolscriptSuffix, true));
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
			if (c.getDataState() == DataState.EDITING || c.getDataState() == DataState.NEW) {
				//the composition was not closed, exit has been cancelled!
				closeInterrupted = true;
			}
		}
		if (!closeInterrupted) {
			debug.temporary("storing config....");
			boolean configStored = false;
			try {
				UserConfig.storePreferences();
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

	public CompositionBase getCompositionBase() {
		return compositionBase;
	}





}



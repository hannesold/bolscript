package bolscript.compositions;

import gui.bolscript.FilterPanel;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import sun.misc.Regexp;

import basics.Debug;
import basics.FileManager;
import basics.FileReadException;
import basics.FileWriteException;
import basics.FolderFilter;
import basics.SuffixFilter;
import bols.BolBase;
import bols.BolBaseGeneral;
import bols.tals.Tal;
import bols.tals.TalBase;
import bolscript.config.Config;
import bolscript.filters.MetaValueFilter;
import bolscript.filters.VisibleCompositionDonator;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.scanner.Parser;

public class CompositionBase implements VisibleCompositionDonator, TalBase{
	BolBaseGeneral bolBase = null;
	//TalBase talBase = null;
	private ArrayList<CompositionBaseListener> listeners;

	private ArrayList<Composition> compositions;

	private FilterPanel filterGUI = null;

	public CompositionBase() {
		compositions = new ArrayList<Composition>();
		listeners = new ArrayList<CompositionBaseListener>();
		bolBase = BolBase.standard();
	}

	/**
	 * Adds a file to the composition base, but only if there is not yet a file
	 * with the same link_lokal.
	 * @param file
	 * @throws FileReadException
	 * @return the existing or the added composition. 
	 */
	public Composition addFile(File file) throws FileReadException{
		Composition existing = this.getCompositionByLinkLocal(file.getAbsolutePath());
		if (existing == null ) {
			Composition comp = new Composition(file, this);
			addComposition(comp);
			return comp;
		} else {
			Debug.debug(this, "The file was already added: " + file);
			return existing;
		}
	}

	public void addComposition(Composition comp) {
		//use better test using equals!
		if (!compositions.contains(comp)) {
			compositions.add(comp);
			fireCompositionBaseChanged(this, CompositionBaseChangeEvent.DATA);
		}

	}

	/**
	 * Returns a Tal Object if a Tal with the given Name was found in the composition base.
	 * If none is found null is returned.
	 * <p>Details:<br/>
	 * When comparing names the case is ignored.
	 * Gathers all compositions that implement the Tal interface.</p>
	 */
	public Tal getTalFromName(String name) {
		for (Composition comp: compositions) {
			if (comp.isTal()) { 
				Tal tal = comp.getTalInfo();
				if (name.equalsIgnoreCase(tal.getName())) {
					return tal;
				}
			}
		}
		return null;
	}

	/**
	 * Removes the composition from compositionBase without deleting it.
	 * @param comp
	 */
	public void removeComposition(Composition comp) {
		removeComposition(comp,false);
	}

	/**
	 * Removes the composition from the CompositionBase and deletes it if requested.
	 * This also adapts the compositions State.
	 * @param comp
	 * @param andDelete
	 */
	public void removeComposition(Composition comp, boolean andDelete) {
		compositions.remove(comp); //remove from composition base
		//if (comp.isTal()) talBase.removeTal(comp); //and talbase

		comp.getDataState().remove(comp);

		//try to delete it
		if (andDelete) {
			try {
				File f = new File(comp.getLinkLocal());
				if (f.exists()) {
					if (f.delete()) {
						comp.getDataState().delete(comp);
					} else {
						//nothing
					}
				}  //nothing comp.getDataState().remove(comp);
			} catch (Exception e) {

			}
		}
		fireCompositionBaseChanged(this, CompositionBaseChangeEvent.DATA);
	}

	/**
	 * Saves the raw data to a file.
	 * If comp.linkLocal equals the filename parameter, this simply saves the composition
	 * under its filename overwriting previous content. 
	 * If a different loaded composition is found under the filename parameter it is overwritten and removed from the comp base.
	 * If comp.linklocal differs from the filename, a "save copy as" mode is run, both copies are kept in the comp base.
	 * Expects the rawdata to be set.
	 * Expects the linklocal of the filename to contain the old filename.
	 * @param comp
	 * @param filename
	 */
	public void saveCompositionToFile(Composition comp, String filename) throws FileWriteException {

		Composition compToOverwrite = getCompositionByLinkLocal(filename);

		if (compToOverwrite == comp) {
			Debug.debug(this, "Composition is saved:");
			//comp is saved under its own filename
			FileManager.writeFile(filename, comp.getCompleteDataForStoring(), Config.compositionEncoding);
			Debug.debug(this, "...is saved");
			comp.getDataState().save(comp);


		} else if (compToOverwrite != null){
			// An existing other composition is overwritten
			// the composition to be overwritten is removed from compbase
			
			removeComposition(compToOverwrite);
			compToOverwrite.getDataState().delete(comp);

			comp.setLinkLocal(filename);
			comp.rebuildFulltextSearch();

			Debug.debug(this, "Composition is overwritten:");
			//comp is saved
			FileManager.writeFile(filename, comp.getCompleteDataForStoring(), Config.compositionEncoding);
			Debug.debug(this, "...is saved");
			comp.getDataState().save(comp);

		} else {
			// There is no composition to be overwritten.
			// The old composition file is added seperately to the compBase
			// This can be seen as "save copy as"
			try {
				String oldFileName = comp.getLinkLocal();

				//comp is saved under new filename
				FileManager.writeFile(filename, comp.getCompleteDataForStoring(), Config.compositionEncoding);
				Debug.debug(this, "...is saved to new filename.");
				comp.setLinkLocal(filename);
				comp.rebuildFulltextSearch();

				comp.getDataState().save(comp);

				//comp under old filename is added again as it 
				//is not deleted or overwritten
				Debug.debug(this, "ADDING FILE : " + oldFileName);
				addFile(new File(oldFileName));
			} catch (FileReadException e) {
				Debug.debug(this, "The old file could not be added.");
			}
		}


		/*if (comp.isTal()) {
			talBase.addTal(comp);
		}*/

		fireCompositionBaseChanged(this, CompositionBaseChangeEvent.DATA);



	}

	public Composition getCompositionByLinkLocal(String link) {
		for (Composition c: compositions) {
			if (link.equals(c.getLinkLocal())) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Adds all not yet added compositions from this folder and its subfolders.
	 * Returns the compositions which already existed in the CompositionBase
	 * but were not 'confirmed', i.e. contained in the path or its subfolders. 
	 * @param path
	 */
	public ArrayList<Composition> addFolderRecursivelyAndGetOutdated(String path) {
		ArrayList<Composition> compsBefore = new ArrayList<Composition>(compositions);
		ArrayList<Composition> compsNew = addFolderRecursively(path);

		ArrayList<Composition> outdated = new ArrayList<Composition>();
		for (Composition c: compsBefore) {
			if (!compsNew.contains(c)) {
				outdated.add(c);
			}
		}
		return outdated;
	}

	/**
	 * Adds all not yet added compositions from this folder and its subfolders. 
	 * @param path
	 * @return A list of all compositions either added or already existing.
	 */
	public ArrayList<Composition> addFolderRecursively (String path) {
		ArrayList<Composition> compositionsAdded = new ArrayList<Composition>();
		Debug.debug(this, "ADDING FOLDER " + path);
		File file = new File(path);
		FilenameFilter tablaFileFilter = new SuffixFilter(bolscript.config.Config.bolscriptSuffix);
		FileFilter folderFilter = new FolderFilter();

		if (file.isDirectory()) {

			File[] files = file.listFiles(tablaFileFilter);
			for (int i=0; i < files.length; i++) {
				Debug.debug(this, "ADDING FILE " + files[i]);
				try {
					Composition added = addFile(files[i]);
					if (added != null) {
						compositionsAdded.add(added);
					}

				} catch (FileReadException ex) {
					Debug.debug(this, ex);
				}
			}

			File[] subFolders = file.listFiles(folderFilter);
			for (int i=0; i < subFolders.length; i++) {
				Debug.debug(this, "ADDING FOLDER " + file);
				compositionsAdded.addAll(addFolderRecursively(subFolders[i].getAbsolutePath()));
			}			

		}	

		return compositionsAdded;
	}




	/**
	 * Get all compositions.
	 * @return
	 */
	public ArrayList<Composition> getCompositions() {
		return compositions;
	}


	/**
	 * 
	 */
	public void updateFiltered() {
		this.fireCompositionBaseChanged(filterGUI, CompositionBaseChangeEvent.VIEW);
	}

	/**
	 * Returns the Compositions that have gone through the filters in the filterpane.
	 * Use these for showing in the browser composition table
	 * @return
	 */
	public ArrayList<Composition> getFiltered() {
		if (filterGUI == null) return compositions;
		return filterGUI.getVisibleCompositions();
	}

	/**
	 * Returns the Compositions which are available for viewing.
	 * This can be restricted if a playlist (not implemented) is selected.
	 * Currently all compositions are visible
	 * @return
	 */
	public ArrayList<Composition> getVisibleCompositions() {
		return compositions;
	}

	public void addChangeListener(CompositionBaseListener c) {
		if (!listeners.contains(c)) {
			listeners.add(c);
		}
	}

	public void removeChangeListener(CompositionBaseListener c) {
		listeners.remove(c);
	}

	private void fireCompositionBaseChanged(Object source, int type) {
		for (CompositionBaseListener listener: listeners) {
			if (source==null) source=this;
			EventQueue.invokeLater(new CompositionBaseChangeEvent(source, this, listener, type));
		}
	}


	public void setFilterGUI(FilterPanel filterGUI) {
		this.filterGUI = filterGUI;
	}

	public String toString() {
		String s = "";
		for (int i=0; i < compositions.size(); i++) {
			s += i + ": " + compositions.get(i).toString() + "\n" + compositions.get(i).getLinkLocal() + "\n\n";
		}
		return s;
	}

	public static String MakeSafeDirectoryName(String candidate) {

		String badFileCharactersRegex = "[\\/:*?\"<>|]+";	
		String processed = candidate.replaceAll(badFileCharactersRegex, "-");
		processed = processed.replaceAll(Parser.SNatBeginningOrEnd, "");
		return processed;

	}

	private static String generateFilepath(Composition comp) {
		String path = "";
		if (comp.isTal()) {
			path = Config.pathToTals;
		} else {
			path = Config.pathToCompositions;
			ArrayList<String> list = comp.getMetaValues().getList(PacketTypeDefinitions.EDITOR);
			if (list.size()>0){
				String editor =  list.get(0);
				if (editor != null && editor.length() > 0) {
					String editorPath = MakeSafeDirectoryName(editor);
					if (!editorPath.isEmpty()) {
						path += editorPath + Config.fileSeperator;
					}
				}
			}
		}
		return path;
	}


	/**
	 * Generates a filename including the complete path. 
	 * If the compopsition is a Tal the path will be the tal directory,
	 * else the compositions directory, concatenated with the editor, 
	 * if this can be made a valid dir-name.
	 * The filename will be numerated (1) (2) etc until a new filename is found,
	 * if ensureIsNew is true.
	 * @param comp The Composition
	 * @param suffix The File suffix
	 * @param ensureIsNew
	 * @return
	 */
	public static String generateFilename(Composition comp, String suffix, boolean ensureIsNew) {
		StringBuilder s = new StringBuilder();

		s.append(generateFilepath(comp));

		String seperator = " - ";

		MetaValues metaValues = comp.getMetaValues();

		if (metaValues.getList(PacketTypeDefinitions.TAL).size() != 0) {
			s.append(metaValues.makeString(PacketTypeDefinitions.TAL));
		} else {
			s.append("unknown tal");
		}
		ArrayList<String> types = metaValues.getList(PacketTypeDefinitions.TYPE);
		if ( types != null) {
			if (types.size() != 0) {
				s.append(seperator + metaValues.makeString(PacketTypeDefinitions.TYPE));
			}
		} else s.append(seperator + "unknown type");
		if (!comp.getName().equals("")) {
			s.append(seperator + comp.getName());
		} else s.append("unknown name");

		String filename = s.toString() + suffix;	
		File f = new File(filename);		

		if (ensureIsNew) {
			//make sure the filename is unique
			int i = 1;		
			while (f.exists()) {
				filename = s.toString() + " ("+i+")" + suffix;
				f = new File(filename);
				i++;
			}
		}
		return filename;
	}


}

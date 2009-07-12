package bolscript.compositions;

import gui.bolscript.FilterPanel;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

import basics.Debug;
import basics.FileReadException;
import basics.FileWriteException;
import basics.FolderFilter;
import basics.SuffixFilter;
import basics.Tools;
import bols.BolBase;
import bols.BolBaseGeneral;
import bols.tals.TalBase;
import bols.tals.TalDynamic;
import bolscript.Reader;
import bolscript.filters.StringArrayFilterGeneral;
import bolscript.filters.VisibleCompositionDonator;
import config.Config;

public class CompositionBase extends basics.Basic implements VisibleCompositionDonator{
	BolBaseGeneral bolBase = null;
	TalBase talBase = null;
	private ArrayList<CompositionBaseListener> listeners;
	
	private ArrayList<Composition> compositions, visibles, filtered;
	
	private ArrayList<String> visibleGharanas;
	private ArrayList<String> visibleSpeeds;
	private ArrayList<String> visibleTals;
	private ArrayList<String> visibleTypes;
	
	private ArrayList<StringArrayFilterGeneral> filters;
	private FilterPanel filterGUI = null;
	
	public CompositionBase() {
		compositions = new ArrayList<Composition>();
		visibles = new ArrayList<Composition>();
		filtered = new ArrayList<Composition>();
		
		listeners = new ArrayList<CompositionBaseListener>();
		
		bolBase = BolBase.standard();
		talBase = new TalBase(bolBase);
		TalBase.setStandard(talBase);
		
		
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
			
			Composition comp = new Composition(file);
			if (comp.isTal()) comp = new TalDynamic(comp);
			
			addComposition(comp);
			comp.getDataState().connect(comp);
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
			
			/*for (CompositionBaseListener listener: listeners) {
				if (listener.getClass().isInstance(CompositionChangedListener.class)) {
					comp.addChangeListener((CompositionChangedListener) listener);
				}
			}*/
			
			if (comp.isTal()) {
				talBase.addTal(comp);
			} else Debug.debug(this, "added Composition " + comp.getName());
			
			fireCompositionBaseChanged(this, CompositionBaseChangeEvent.DATA);
		}

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
		if (comp.isTal()) talBase.removeTal(comp); //and talbase
		
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
	 * Expects the rawdata to be set.
	 * Expects the linklocal of the filename to contain the old filename.
	 * @param comp
	 * @param filename
	 */
	public void saveCompositionToFile(Composition comp, String filename) throws FileWriteException {
		
		Composition compToOverwrite = getCompositionByLinkLocal(filename);
		
		if (compToOverwrite == comp) {
			Debug.debug(this, "Composition is saved:");
			//comp is saved
			Reader.writeFile(filename, comp.getRawData());
			Debug.debug(this, "...is saved");
			comp.getDataState().save(comp);
			
			
		} else if (compToOverwrite != null){
			//the composition to be overwritten is removed from compbase
			removeComposition(compToOverwrite);
			compToOverwrite.getDataState().delete(comp);
			
			comp.setLinkLocal(filename);
			comp.rebuildFulltextSearch();
			
			Debug.debug(this, "Composition is overwritten:");
			//comp is saved
			Reader.writeFile(filename, comp.getRawData());
			Debug.debug(this, "...is saved");
			comp.getDataState().save(comp);
			
		} else {
			//there is no composition to be overwritten, the old composition is added seperately
			try {
				String oldFileName = comp.getLinkLocal();
				
				//comp is saved under new filename
				Reader.writeFile(filename, comp.getRawData());
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
		
		
		if (comp.isTal()) {
			talBase.addTal(comp);
		}
		
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
		out("ADDING FOLDER " + path);
		File file = new File(path);
		FilenameFilter tablaFileFilter = new SuffixFilter(config.Config.bolscriptSuffix);
		FileFilter folderFilter = new FolderFilter();
		
		if (file.isDirectory()) {
			
			File[] files = file.listFiles(tablaFileFilter);
			for (int i=0; i < files.length; i++) {
				out("ADDING FILE " + files[i]);
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
				out("ADDING FOLDER " + file);
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
	 * This can be restricted if a playlist is selected.
	 * Currently all compositions are visible
	 * @return
	 */
	public ArrayList<Composition> getVisibleCompositions() {
		return compositions;
	}
	
	public void addChangeListener(CompositionBaseListener c) {
		if (!listeners.contains(c)) {
			listeners.add(c);
			/*if (c.getClass().isInstance(CompositionChangedListener.class)) {
				Debug.debug(this, " passing listener on");
				for (Composition comp: compositions) {
					comp.addChangeListener((CompositionChangedListener) c);
				}
			}*/
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
	
	
	public static String generateFilename(Composition comp, String suffix) {
		StringBuilder s = new StringBuilder();
		s.append(Config.pathToCompositions);
		String seperator = " - ";
		if (comp.getTals().size() != 0) {
			s.append(Tools.toString(comp.getTals()));
		} else {
			s.append("unknown tal");
		}
		if (comp.getTypes() != null) {
			if (comp.getTypes().size() != 0) {
				s.append(seperator + Tools.toString(comp.getTypes()));
			}
		} else s.append(seperator + "unknown type");
		if (!comp.getName().equals("")) {
			s.append(seperator + comp.getName());
		} else s.append("unknown name");
		
		
		
		String filename = s.toString() + suffix;	
		File f = new File(filename);		
		
		//make sure the filename is unique
		int i = 1;		
		while (f.exists()) {
			filename = s.toString() + " ("+i+")" + suffix;
			f = new File(filename);
			i++;
		}
		
		return filename;
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


	

}

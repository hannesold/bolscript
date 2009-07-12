package gui.bolscript;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import basics.Debug;
import bolscript.Reader;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.CompositionBaseChangeEvent;
import bolscript.compositions.CompositionBaseListener;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import bolscript.filters.FullTextSearchFilter;
import bolscript.filters.GharanaFilter;
import bolscript.filters.SpeedFilter;
import bolscript.filters.StringArrayFilterGeneral;
import bolscript.filters.TalFilter;
import bolscript.filters.TypeFilter;

public class FilterPanel extends JPanel implements ListSelectionListener, KeyListener, CompositionBaseListener, CompositionChangedListener {

	private static final long serialVersionUID = 9132774459629687854L;

	private long selectionCounter = 0;
	public CompositionBase compBase;
	private static String ALL = "All";
	
	
	//the gui elements
	private SearchPanel searchPanel;
	private int nrOfLists;
	private ArrayList<StringArrayFilterGeneral> listFilters;
	private FilterList gharanaList;
	private FilterList talList;
	private FilterList typeList;
	private FilterList speedList;
		
	//the filters
	private ArrayList<FilterList> lists;
	private FullTextSearchFilter searchFilter;
	
	
	public FilterPanel(CompositionBase compBase, SearchPanel searchPanel) {
		super();
		this.compBase = compBase;
		this.searchPanel = searchPanel;
		initPanels();
		compBase.addChangeListener(this);
	}
	
	public void initPanels() {
		
		
		lists = new ArrayList<FilterList>();
		listFilters = new ArrayList<StringArrayFilterGeneral>();
		
	    nrOfLists = 4;
		
	    searchFilter = new FullTextSearchFilter();	   
	    listFilters.add(new GharanaFilter());
		listFilters.add(new TalFilter());
		listFilters.add(new TypeFilter());	
		listFilters.add(new SpeedFilter());
		
		// set Sources
	    searchFilter.setCompositionSource(compBase);
		listFilters.get(0).setCompositionSource(searchFilter);
		for (int i=1; i< listFilters.size(); i++) {
			listFilters.get(i).setCompositionSource(listFilters.get(i-1));	
		}
		
		// set all to bypass and generate initial data sets
	    searchFilter.setAcceptAll(true);
	    searchFilter.bypass();
		for(int i=0; i< nrOfLists; i++) {
			listFilters.get(i).setAcceptAll(true);
			listFilters.get(i).bypass();
			ArrayList<String> collection = listFilters.get(i).collect(true);
			collection.add(0,ALL + " (" + collection.size()+")");
		}
				
		// generate the JLists
		gharanaList = new FilterList(listFilters.get(0).getCollectedStrings().toArray());
		talList = new FilterList(listFilters.get(1).getCollectedStrings().toArray());
		typeList = new FilterList(listFilters.get(2).getCollectedStrings().toArray());
		speedList = new FilterList(listFilters.get(3).getCollectedStrings().toArray());
		
		lists.add(gharanaList);		
		lists.add(talList);
		lists.add(typeList);
		lists.add(speedList);
		
		for(FilterList list: lists) {
			list.setSelectedIndex(0);
			list.setLayoutOrientation(FilterList.VERTICAL);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		
		//add the Wrapper Panels for the JLists (and set their title)
		FilterListPanel gharanaPanel = new FilterListPanel("Gharanas", gharanaList);
		FilterListPanel talPanel = new FilterListPanel("Tals", talList);
		FilterListPanel typePanel = new FilterListPanel("Types", typeList);
		FilterListPanel speedPanel = new FilterListPanel("Speeds", speedList);
		
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.add(gharanaPanel);
		this.add(talPanel);
		this.add(typePanel);
		this.add(speedPanel);
		
		
		refreshDataAndSelectionsInList(0);
		processSelections(0);
		startListening();
		
	}
	
	/**
	 * Is called when the selections in one of the FilterLists is changed
	 */
	public void valueChanged(ListSelectionEvent e) {
		
		if (!e.getValueIsAdjusting()) {
			stopListening();
			
			Debug.debug(this, "\n\n"+selectionCounter+": Selection Event registered! in " + e);
			int index = lists.indexOf(e.getSource());
			
			if (index >=0) processSelections(index);
			
			selectionCounter++;
			startListening();
		}
	}
	

	/**
	 * Is called when a search string is entered
	 */
	public void keyReleased(KeyEvent e) {
		stopListening();
		Debug.debug(this, "\n\n"+selectionCounter+": KeyTyped Event registered! in " + e);
		
		processSelections(-1);
		
		selectionCounter++;
		startListening();
	}
	
	public void processSelections(int startingAtFilter) {
		Debug.debug(this,"PROCESS SELECTIONS STARTING AT "+ startingAtFilter);
		
		if (startingAtFilter == -1) {
			//code for the searchPanel has changed
			String searchKey = searchPanel.getSearchField().getText();
			
			if (searchKey.replaceAll(Reader.SN, "").equals("")) {
				searchFilter.setAcceptAll(true);	
				searchFilter.bypass();
			} else {
				searchFilter.setAcceptAll(false);
				searchFilter.filter(new String[]{searchKey});
			}
			
			//refresh gharana list
			checkAllSelection(0);
			refreshDataAndSelectionsInList(0);
			
			//continue processing selections 
			startingAtFilter = 0;
		}
		
		//list filters
		for (int i = startingAtFilter; i < nrOfLists; i++) {
			
			checkAllSelection(i);
			
			//generate filteredComps(i)
			Debug.debug(this,"PROCESSING SELECTIONS AT "+ (i));
			applyFilterAccordingToSelection(i);
			if (i<=lists.size()-1) {
				Debug.debug(this,"Refreshing SELECTIONS AT "+ (i+1));
				//update next list based on filteredComps(i)
				refreshDataAndSelectionsInList(i+1);
			}
			
		}
		
		compBase.updateFiltered();

	}
	
	/**
	 * If "All" is part of a selection in a list
	 * the selection is reduced to "All", as it covers all other List entries.
	 * @param index
	 */
	public void checkAllSelection(int index) {
		if (lists.get(index).getSelectedIndices().length>0) {
			if (lists.get(index).getSelectedIndices()[0] == 0) {
				lists.get(index).setSelectedIndex(0);
			}
				
		}
	}
	
	/**
	 * Apply Filter according to a selection in the filterlist with the given index.
	 * @param index
	 */
	public void applyFilterAccordingToSelection(int index) {
		
		if (index>=0 && index < listFilters.size()) {
			FilterList list = lists.get(index);
			StringArrayFilterGeneral filter = listFilters.get(index);

			int[] indices = list.getSelectedIndices();
			if (indices.length > 0) {
				
				//set to bypass if "All" is selected
				filter.setAcceptAll(indices[0]==0);

				if (filter.acceptsAll()) {
					filter.bypass();
				} else {
					//obtain search patterns
					String[] searchPattern = new String[indices.length];
				
					for (int j=0; j<indices.length; j++) {
						searchPattern[j] = new String((String) list.getModel().getElementAt(indices[j]));
						Debug.debug(this, "adding searchPattern " + searchPattern[j]);
					}
					//filter
					filter.filter(searchPattern);
				}
				
			} else { //there is no selection so "ALL" is selected
				list.setSelectedIndex(0);
				filter.setAcceptAll(true);
				filter.bypass();
			}

		} //if index >=0
		
	}
	
	public void refreshDataAndSelectionsInList(int i) {
		
		if (i >= 0 && i < lists.size()) {
			FilterList list = lists.get(i);
			StringArrayFilterGeneral filter = listFilters.get(i);
			
			int [] indices = list.getSelectedIndices();
			if (indices.length >0) {
				boolean ALL_selected = (indices[0]==0);
				
				//collect the currently selected strings
				ArrayList<String> selectedStrings = new ArrayList<String>(indices.length);
				for (int j = 0; j < indices.length; j++) {
					selectedStrings.add((String) list.getModel().getElementAt(indices[j]));
					Debug.debug(this, "adding old selection: " + selectedStrings.get(j));
				}

				//set the new list entries
				ArrayList<String> collection = filter.collect(true);
				collection.add(0, ALL + " (" + collection.size()+")");
				
				//collect all strings for this list from the compositionbase 
				String[] newListData = collection.toArray(new String[collection.size()]);
				list.setListData(newListData);

				//find the items which were previously selected and select them again
				
				if (ALL_selected) {
					//only select all
					list.setSelectedIndex(0);
				} else {
					ArrayList<Integer> newSelectedIndices = new ArrayList<Integer> ();
					for (int j=1; j < newListData.length;j++) {
						int l;
						Debug.debug(this, "comparing entry in new List " + newListData[j]);
						if ((l = selectedStrings.indexOf(newListData[j])) >=0){
							selectedStrings.remove(l);
							newSelectedIndices.add(j);
							Debug.debug(this, "matched");
						}	else {
							Debug.debug(this, "not found in " + selectedStrings);
						}
					}
					//rebuild selections
					int[] newIndices = new int[newSelectedIndices.size()];
					for (int j=0; j < newIndices.length; j++) {
						newIndices[j] = newSelectedIndices.get(j);

						if (newIndices[j] == 0)	Debug.debug(this, "ALL IS SELECTED!");
					}
					//set the selection
					list.setSelectedIndices(newIndices);
				}
			
			} else { 
				list.setSelectedIndex(0);
			}
		} //if 
	}
	
	/**
	 * Returns the compositions which are visible after the filtering process.
	 * @return
	 */
	public ArrayList<Composition> getVisibleCompositions() {
		return listFilters.get(listFilters.size()-1).getVisibleCompositions();
	}
	
	public void compositionBaseChanged(CompositionBaseChangeEvent event) {
		if (event.getSource() != this) {
			stopListening();
			refreshDataAndSelectionsInList(0);
			processSelections(0);
			startListening();
		}
	}
	public void compositionChanged(CompositionChangeEvent compositionChangeEvent) {
	//	processSelections(0);
	}
	

	public void startListening() {
		for (FilterList list: lists) {
			list.addListSelectionListener(this);
		}		
		searchPanel.addKeyListener(this);
	}
	public void stopListening() {
		for (FilterList list: lists) {
			list.removeListSelectionListener(this);
		}
		searchPanel.removeKeyListener(this);
	}
	
	public void keyPressed(KeyEvent e) {
		//Debug.debug(this, "Key Pressed!");
	}
	public void keyTyped(KeyEvent e) {
		//Debug.debug(this, "Key Released!");
	}

}

package gui.bolscript.tables;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import basics.Debug;
import basics.Tools;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.CompositionBaseChangeEvent;
import bolscript.compositions.CompositionBaseListener;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import bolscript.compositions.State;

public class CompositionTableModel extends AbstractTableModel implements CompositionBaseListener, CompositionChangedListener {

	CompositionBase compBase = null;
	
	public final static int STATE = 0;
	public final static int NAME = 1;
	public final static int TALS = 2;
	public final static int TYPE = 3;
	public final static int SNIPPET = 4;
	public final static int GHARANAS = 5;
	public final static int SPEEDS = 6;
	public final static int COMPOSERS = 7;
	public final static int SOURCES = 8;
	public final static int EDITORS = 9;
	
	public final static int NR_OF_COLUMNS = 10;
	public static int[] COLUMNS_TO_SHOW;
	public static String[] COLUMN_NAMES;
	public static int[] COLUMN_ORDER;
	public static HashMap<Integer, Integer> COLUMN_INDEX;
	static {
		COLUMNS_TO_SHOW = new int[]{STATE,NAME,TALS,TYPE, SNIPPET, GHARANAS, SPEEDS, COMPOSERS, SOURCES, EDITORS};
		COLUMN_NAMES = new String[]{" ", "Name", "Tals", "Type", "Snippet", "Gharanas", "Speeds", "Composers", "Sources", "Editors"};
		COLUMN_ORDER = new int[]{STATE, NAME, TALS, TYPE, SPEEDS,GHARANAS, SNIPPET, COMPOSERS, SOURCES, EDITORS};
		COLUMN_INDEX = new HashMap<Integer, Integer>();
		COLUMN_INDEX.put(STATE, 0);
		COLUMN_INDEX.put(NAME, 1);
		COLUMN_INDEX.put(TALS, 2);
		COLUMN_INDEX.put(TYPE, 3);
		COLUMN_INDEX.put(SPEEDS, 4);
		COLUMN_INDEX.put(GHARANAS, 5);
		COLUMN_INDEX.put(SNIPPET, 6);
		COLUMN_INDEX.put(COMPOSERS, 7);
		COLUMN_INDEX.put(SOURCES, 8);
		COLUMN_INDEX.put(EDITORS, 9);
		
	}
	
	private int NR_OF_COLUMNS_TO_SHOW = NR_OF_COLUMNS;
	private ArrayList<Composition> compositions;
	
	
	public CompositionTableModel(CompositionBase compBase) {
		this.compBase = compBase;
		compBase.addChangeListener(this);
		this.compositions = compBase.getFiltered();
		addAsListenerToCompositions();
	}

	
	/*@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return super.getColumnClass(columnIndex);
	}*/

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return COLUMN_NAMES[COLUMN_ORDER[column]];
	}

	public int getColumnCount() {
		return NR_OF_COLUMNS_TO_SHOW;
	}

	public int getRowCount() {
		return compositions.size();
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (COLUMNS_TO_SHOW[COLUMN_ORDER[columnIndex]]) {
		case STATE:
			return Integer.class; 
		default:
			return String.class;
		}
		
		
	}


	public Object getValueAt(int rowIndex, int columnIndex) {
		
		Composition c = compositions.get(rowIndex);
		switch (COLUMNS_TO_SHOW[COLUMN_ORDER[columnIndex]]) {
		case STATE:
			return (State) c.getDataState(); 
		case TALS:
			return Tools.toString(c.getTals());
		case TYPE:
			return Tools.toString(c.getTypes());
		case COMPOSERS:
			return Tools.toString(c.getComposers());
		case SOURCES:
			return Tools.toString(c.getSources());
		case EDITORS:
			return Tools.toString(c.getEditors());
		case NAME:
			return c.getName();
		case SNIPPET:
			return c.getSnippet();
		case GHARANAS:
			return Tools.toString(c.getGharanas());
		case SPEEDS:
			return Tools.toString(c.getSpeeds());
		}
		return "undefined";
	}


	public Composition getComposition(int i) {
		if (i < compositions.size()) {
			return compositions.get(i);
		} else return null;
	}


	public void compositionBaseChanged(CompositionBaseChangeEvent event) {
		for (Composition comp: compositions) {
			comp.removeChangeListener(this);
		}
		compositions = new ArrayList<Composition>(compBase.getFiltered());//(ArrayList<Composition>) compBase.getFiltered().clone();

		addAsListenerToCompositions();
		
		fireTableDataChanged();
	}

	private void addAsListenerToCompositions() {
		for (Composition comp: compositions) {
			comp.addChangeListener(this);
		}
	}

	public void compositionChanged(CompositionChangeEvent compositionChangeEvent) {
		Debug.debug(this, "registered compositionChanged");
		//compositions = compBase.listVisible();
		int i = compositions.indexOf(compositionChangeEvent.getComposition());
		if (i >= 0) fireTableRowsUpdated(i, i);
		//fireTableCellUpdated(row, column);
	}
	
	

}

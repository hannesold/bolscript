package gui.bolscript.tables;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import basics.Debug;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionBase;
import bolscript.compositions.CompositionBaseChangeEvent;
import bolscript.compositions.CompositionBaseListener;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import bolscript.compositions.DataState;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;

public class CompositionTableModel extends AbstractTableModel implements CompositionBaseListener, CompositionChangedListener {

	CompositionBase compBase = null;
	
	private ArrayList<Composition> compositions;
	private ArrayList<PacketType> metaColumns;
	
	public CompositionTableModel(CompositionBase compBase) {
		this.compBase = compBase;
		compBase.addChangeListener(this);
		this.compositions = compBase.getFiltered();
		addAsListenerToCompositions();
		
		init();
	}

	public void init() {
		metaColumns = new ArrayList<PacketType>();
		PacketType[] types = PacketTypeFactory.getColumnTypes();
		
		for (int i=0; i < types.length;i++) {
			metaColumns.add(types[i]);
		}
		Collections.sort(metaColumns);		
	}
	

	@Override
	public String getColumnName(int column) {
		if (column==0) {
			return "";
		} else {
			return metaColumns.get(column-1).getDisplayNameTable();
		}
	}
	@Override
	public int getColumnCount() {
		return 1+metaColumns.size();
	}
	
	@Override
	public int getRowCount() {
		return compositions.size();
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0) {
			return Integer.class;
		} else return String.class;		
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		Composition c = compositions.get(rowIndex);
		if (columnIndex==0) {
			return (DataState) c.getDataState();
		} else {
			int typeIndex = metaColumns.get(columnIndex-1).getId();
			return c.getMetaValues().makeString(typeIndex);
		}
	}


	/**
	 * Returns the composition at the i-th row.
	 * @param i
	 */
	public Composition getComposition(int i) {
		if (i < compositions.size()) {
			return compositions.get(i);
		} else return null;
	}


	@Override
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

	@Override
	public void compositionChanged(CompositionChangeEvent compositionChangeEvent) {
		Debug.debug(this, "registered compositionChanged");
		//compositions = compBase.listVisible();
		int i = compositions.indexOf(compositionChangeEvent.getComposition());
		if (i >= 0) fireTableRowsUpdated(i, i);
		//fireTableCellUpdated(row, column);
	}
	
	

}

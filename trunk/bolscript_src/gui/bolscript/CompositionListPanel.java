package gui.bolscript;

import gui.bolscript.tables.CellRenderer;
import gui.bolscript.tables.CompositionTableModel;
import gui.bolscript.tables.StateRenderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import basics.GUI;
import bolscript.Master;
import bolscript.config.Config;

public class CompositionListPanel extends JScrollPane {

	JTable compositionTable = null;
	CompositionTableModel tableModel = null;
	
	public CompositionListPanel(CompositionTableModel model) {
		super();
		this.tableModel = model;
		compositionTable = new JTable(model);
		compositionTable.setDefaultRenderer(Integer.class, new StateRenderer(false));
		compositionTable.setDefaultRenderer(Object.class, new CellRenderer());
		compositionTable.setBackground(Color.white);//Config.tableBG);
		compositionTable.getColumnModel().getColumn(0).setMaxWidth(20);
		compositionTable.getColumnModel().getColumn(1).setMinWidth(140);
		compositionTable.getColumnModel().getColumn(2).setMinWidth(90);
		compositionTable.getColumnModel().getColumn(3).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(4).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(5).setWidth(90);
		compositionTable.addMouseListener(GUI.proxyClickListener(Master.master, "clickOnCompositionList"));
		//compositionTable.setShowGrid(false);
		compositionTable.setGridColor(Config.tableBG);
		compositionTable.setShowGrid(false);
		compositionTable.setShowHorizontalLines(false);
		compositionTable.setShowVerticalLines(true);
		
		RowSorter<CompositionTableModel> sorter = new TableRowSorter<CompositionTableModel>(model);
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(model.COLUMN_INDEX.get(model.TALS), SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(model.COLUMN_INDEX.get(model.TYPE), SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(model.COLUMN_INDEX.get(model.NAME), SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(model.COLUMN_INDEX.get(model.SPEEDS), SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(model.COLUMN_INDEX.get(model.GHARANAS), SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(model.COLUMN_INDEX.get(model.TALS), SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys); 

		//= new Def
		compositionTable.setRowSorter(sorter);
		
		
		this.setViewportView(compositionTable);
		
	}

	public JTable getCompositionTable() {
		return compositionTable;
	}

	public CompositionTableModel getTableModel() {
		return tableModel;
	}
	
	
}

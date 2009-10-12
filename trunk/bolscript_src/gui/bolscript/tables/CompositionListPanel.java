package gui.bolscript.tables;


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
import bolscript.config.GuiConfig;

public class CompositionListPanel extends JScrollPane {

	JTable compositionTable = null;
	CompositionTableModel tableModel = null;
	
	public CompositionListPanel(CompositionTableModel model) {
		super();
		this.tableModel = model;
		compositionTable = new JTable(model);
		compositionTable.setDefaultRenderer(Integer.class, new StateRenderer(false));
		compositionTable.setDefaultRenderer(Object.class, new CellRenderer());
		compositionTable.setBackground(Color.white);
		compositionTable.getColumnModel().getColumn(0).setMaxWidth(20);
		compositionTable.getColumnModel().getColumn(1).setMinWidth(140);
		compositionTable.getColumnModel().getColumn(2).setMinWidth(90);
		compositionTable.getColumnModel().getColumn(3).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(4).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(5).setWidth(90);
		compositionTable.addMouseListener(GUI.proxyClickListener(Master.master, "clickOnCompositionList"));
		//compositionTable.setShowGrid(false);
		compositionTable.setGridColor(GuiConfig.tableBG);
		compositionTable.setShowGrid(false);
		compositionTable.setShowHorizontalLines(false);
		compositionTable.setShowVerticalLines(true);
		//compositionTable.set
		
		//set Rowsorters
		RowSorter<CompositionTableModel> sorter = new TableRowSorter<CompositionTableModel>(model);
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		for (int i = 1; i < model.getColumnCount(); i++) {
			sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
		}
		sorter.setSortKeys(sortKeys); 

		//= new Def
		compositionTable.setRowSorter(sorter);
		
		
		this.setViewportView(compositionTable);
		this.getViewport().setBackground(Color.white);
		
		
		this.setOpaque(false);
		
	}

	public JTable getCompositionTable() {
		return compositionTable;
	}

	public CompositionTableModel getTableModel() {
		return tableModel;
	}
	
	
}

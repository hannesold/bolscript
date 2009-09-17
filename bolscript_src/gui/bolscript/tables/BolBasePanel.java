package gui.bolscript.tables;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import bols.BolBase;
import bols.BolName;

public class BolBasePanel extends JPanel {
	
	BolBaseTableModel model;
	JTable table;
	
	public BolBasePanel() {
		super();
		this.setLayout(new BorderLayout());

		BolBaseTableModel model = new BolBaseTableModel(BolBase.getStandard());
		table = new JTable(model);
		
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane,BorderLayout.CENTER);
		
		initColumnWidths();
	}
	
private void initColumnWidths () {
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(BolName.SIMPLE).setMinWidth(42);
		columnModel.getColumn(BolName.SIMPLE).setWidth(60);
		columnModel.getColumn(BolName.SIMPLE).setMaxWidth(60);
		columnModel.getColumn(BolName.EXACT).setMinWidth(42);
		columnModel.getColumn(BolName.EXACT).setWidth(60);
		columnModel.getColumn(BolName.EXACT).setMaxWidth(60);
		columnModel.getColumn(BolName.DEVANAGERI).setMinWidth(25);
		columnModel.getColumn(BolName.DEVANAGERI).setWidth(25);
		columnModel.getColumn(BolName.DEVANAGERI).setMaxWidth(60);
		columnModel.getColumn(BolName.TRANSLITERATION).setMinWidth(42);
		columnModel.getColumn(BolName.TRANSLITERATION).setWidth(60);
		columnModel.getColumn(BolName.TRANSLITERATION).setMaxWidth(60);
		columnModel.getColumn(BolName.INITIALS).setMinWidth(25);
		columnModel.getColumn(BolName.INITIALS).setWidth(25);
		columnModel.getColumn(BolName.INITIALS).setMaxWidth(50);
		columnModel.getColumn(BolBaseTableModel.descriptionColumn).setMinWidth(140);
		columnModel.getColumn(BolBaseTableModel.descriptionColumn).setWidth(170);
		columnModel.getColumn(BolBaseTableModel.descriptionColumn).setMaxWidth(270);
		columnModel.getColumn(BolBaseTableModel.detailsColumn).setMinWidth(70);
		columnModel.getColumn(BolBaseTableModel.detailsColumn).setWidth(70);
		columnModel.getColumn(BolBaseTableModel.detailsColumn).setMaxWidth(120);
		
	}
	
}

package gui.bolscript.tables;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import basics.Debug;
import bols.BolBase;
import bols.BolName;
import bolscript.config.GuiConfig;

public class BolBasePanel extends JPanel {
	
	private static final long serialVersionUID = 7584847766972066875L;
	
	BolBaseTableModel model;
	JTable table;
	JScrollPane scrollPane;
	
	public BolBasePanel() {
		super();
		this.setLayout(new BorderLayout());

		model = new BolBaseTableModel(BolBase.getStandard());
		table = new JTable(model);
		//table.setMinimumSize(new Dimension(500,300));
		table.setDefaultRenderer(Object.class, new CellRenderer(true));
		table.setGridColor(GuiConfig.tableBG);
		table.setShowGrid(false);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		scrollPane = new JScrollPane(table);
		//scrollPane.setPreferredSize(new Dimension(500,300));
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		//scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
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
		//columnModel.getColumn(BolBaseTableModel.descriptionColumn).setWidth(170);
		//columnModel.getColumn(BolBaseTableModel.descriptionColumn).setMaxWidth(270);
		columnModel.getColumn(BolBaseTableModel.detailsColumn).setMinWidth(70);
		//columnModel.getColumn(BolBaseTableModel.detailsColumn).setWidth(70);
		//columnModel.getColumn(BolBaseTableModel.detailsColumn).setMaxWidth(120);
		
	}

public void selectBol(BolName bolName) {
	Debug.temporary(this, "attempting to select " + bolName);
	String toFind = bolName.getNameForDisplay(BolName.EXACT);
	for (int i=0; i < model.getRowCount(); i++) {
		if (model.getValueAt(i, BolName.EXACT).equals(toFind)) {
			
			Debug.temporary(this, "setting selection to " +toFind + ", at row " + i);
			table.getSelectionModel().setSelectionInterval(i, i);
			
			
			Rectangle bolRowRectangle = table.getCellRect(i, BolName.EXACT, false);
			Rectangle currentView = table.getVisibleRect();
			
			int y = Math.max(0,bolRowRectangle.y - ((currentView.height - bolRowRectangle.height)/2));
			Rectangle viewCenteredAroundSelectedBolRow = new Rectangle(currentView.x, y,currentView.width,currentView.height);
			
			Debug.temporary(this, "attempting to scroll to " + viewCenteredAroundSelectedBolRow);
			table.scrollRectToVisible(viewCenteredAroundSelectedBolRow);
			//scrollPane.set
			break;
		}
	}
	
	
	
}
	
}

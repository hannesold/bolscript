package gui.bolscript.tables;

import javax.swing.table.AbstractTableModel;

import bols.BolBase;
import bols.BolName;
import bols.BolNameBundle;
import bolscript.packets.Packet;

public class BolBaseTableModel extends AbstractTableModel {

	private BolBase bolBase;
	public static int descriptionColumn = BolName.languagesCount;
	public static int detailsColumn = descriptionColumn+1;
	public static int nrOfColumns = detailsColumn+1;
	
	@Override
	public String getColumnName(int column) {
		if (column < BolName.languagesCount) {
			return BolName.languageNames[column];
		} else if (column == descriptionColumn) {
			return "Description";
		} else if (column == detailsColumn) {
			return "Details";
		} else return null;
	}

	public BolBaseTableModel(BolBase bolBase) {
		this.bolBase = bolBase;
	}
	
	public int getColumnCount() {
		return nrOfColumns;
	}

	public int getRowCount() {
		return bolBase.getWellDefinedBolNames().size() + bolBase.getReplacementPackets().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < bolBase.getWellDefinedBolNames().size()) {
			
			BolName bolName = bolBase.getWellDefinedBolNames().get(rowIndex);
			
			if (columnIndex < BolName.languagesCount) {
				return bolName.getName(columnIndex);
			} else if (columnIndex == descriptionColumn) {
				return bolName.getDescription();
			} else if (columnIndex == detailsColumn) {
				return (bolName.getHandType() == BolName.COMBINED)? bolName.getLeftHand() + " + " + bolName.getRightHand(): BolName.handTypes[bolName.getHandType()];
			} else return null;
			
		} else {
			int index = rowIndex-bolBase.getWellDefinedBolNames().size();

			Packet packet = bolBase.getReplacementPackets().get(index);
			BolNameBundle bolNameBundle = (BolNameBundle) packet.getObject();
			if (bolNameBundle != null) {
			if (columnIndex < BolName.languagesCount) {
				return bolNameBundle.getName(columnIndex);
			} else if (columnIndex == descriptionColumn) {
				return bolNameBundle.getDescription();
			} else if (columnIndex == detailsColumn) {
				return packet.getValue();
			} else return null;
			} else return "undefined, packet: " + packet;
		}
		
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
		//super.setValueAt(aValue, rowIndex, columnIndex);
	}


}

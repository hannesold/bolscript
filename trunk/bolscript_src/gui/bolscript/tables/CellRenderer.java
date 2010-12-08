package gui.bolscript.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import basics.GUI;
import bolscript.config.GuiConfig;

public class CellRenderer extends DefaultTableCellRenderer {

	boolean withToolTips = false;
	
	public CellRenderer(boolean withToolTips) {
		this.withToolTips = withToolTips;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);//new JLabel();
		String stringValue = (value != null ? value.toString() : "");
		c.setText(" " +  stringValue);
		
		c.setOpaque(true);
		Dimension size = GUI.getPrefferedSize(c, 50);
		size = new Dimension(size.width + 10, size.height+10);
		
		Color bgRows = (row % 2 == 0) ? GuiConfig.colorEvenRows : GuiConfig.colorUnvenRows;

		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
			c.setForeground(table.getSelectionForeground());
		} else {
			c.setBackground(bgRows);
			c.setForeground(table.getForeground());
		}
		
		if (withToolTips) {
			c.setToolTipText(stringValue);
		}
		return c;
	}

}

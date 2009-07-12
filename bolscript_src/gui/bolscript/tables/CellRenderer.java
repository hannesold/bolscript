package gui.bolscript.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import basics.GUI;
import config.Config;

public class CellRenderer extends DefaultTableCellRenderer {

	

	public CellRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		/*Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);*/
		
		JLabel c = new JLabel();
		c.setText(" "+ value.toString() );
		
		c.setOpaque(true);
		Dimension size = GUI.getPrefferedSize(c, 50);
		size = new Dimension(size.width + 10, size.height+10);
		
		Color bgRows = (row%2==0) ? Config.colorEvenRows : Config.colorUnvenRows;

		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
			c.setForeground(table.getSelectionForeground());
		} else {
			c.setBackground(bgRows);
			c.setForeground(table.getForeground());

		}
		
		
		return c;
	}

}

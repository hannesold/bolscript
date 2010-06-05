package gui.bolscript.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


import bolscript.compositions.DataState;
import bolscript.config.GuiConfig;

public class StateRenderer extends DefaultTableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public StateRenderer(boolean isBordered) {
		super();
		this.isBordered = isBordered;
	}

	public Component getTableCellRendererComponent(
			JTable table, Object dataState,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, dataState, isSelected, hasFocus, row, column);
					
		//Color newColor = (Color)color;
		if ((DataState) dataState == DataState.EDITING) {
				c.setText("*");
				c.setToolTipText("Editing");
		} else if ((DataState) dataState == DataState.MISSING) {
				c.setText("?");
				c.setToolTipText("Missing");
		} else if ((DataState) dataState == DataState.NEW){
			c.setText("*");
			c.setToolTipText("New");			
		} else {
				c.setText(" ");
		}
		//c.setOpaque(false); //MUST do this for background to show up.
		c.setOpaque(true);
		Color bgRows = (row%2==0) ? GuiConfig.colorEvenRows : GuiConfig.colorUnvenRows;

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

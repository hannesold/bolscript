package gui.bolscript.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;


import bolscript.compositions.DataState;
import bolscript.config.GuiConfig;

public class StateRenderer extends JLabel implements TableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public StateRenderer(boolean isBordered) {
		this.isBordered = isBordered;
		setOpaque(false); //MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(
			JTable table, Object dataState,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		//Color newColor = (Color)color;
		if ((DataState) dataState == DataState.EDITING) {
				this.setText("*");
				setToolTipText("Editing");
		} else if ((DataState) dataState == DataState.MISSING) {
				this.setText("?");
				setToolTipText("Missing");
		} else if ((DataState) dataState == DataState.NEW){
			this.setText("*");
			setToolTipText("New");			
		} else {
				this.setText(" ");
		}
		this.setOpaque(true);
		Color bgRows = (row%2==0) ? GuiConfig.colorEvenRows : GuiConfig.colorUnvenRows;

		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
			this.setForeground(table.getSelectionForeground());
		} else {
			this.setBackground(bgRows);
			this.setForeground(table.getForeground());

		}
		return this;
	}



}

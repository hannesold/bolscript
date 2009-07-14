package gui.bolscript.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;


import bolscript.compositions.State;
import bolscript.config.Config;

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
		if ((State) dataState == State.EDITING) {
				this.setText("*");
				setToolTipText("Editing");
		} else if ((State) dataState == State.MISSING) {
				this.setText("?");
				setToolTipText("Missing");
		} else if ((State) dataState == State.NEW){
			this.setText("*");
			setToolTipText("New");			
		} else {
				this.setText(" ");
		}
		this.setOpaque(true);
		Color bgRows = (row%2==0) ? Config.colorEvenRows : Config.colorUnvenRows;

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

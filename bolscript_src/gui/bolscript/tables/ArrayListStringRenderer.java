package gui.bolscript.tables;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class ArrayListStringRenderer extends JLabel implements TableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public ArrayListStringRenderer(boolean isBordered) {
		this.isBordered = isBordered;
		//setOpaque(true); //MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(
			JTable table, Object obj,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		StringBuilder s = new StringBuilder ();
		ArrayList<String> strings = (ArrayList<String>) obj;
		
		for (int i=0; i < strings.size(); i++) {
			
		}
		
		//setBackground(newColor);
		if (isBordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
							table.getSelectionBackground());
				}
				setBorder(selectedBorder);
			} else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
							table.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}
/*
		setToolTipText("RGB value: " + newColor.getRed() + ", "
				+ newColor.getGreen() + ", "
				+ newColor.getBlue());*/
		return this;
	}



}

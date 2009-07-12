package gui.playlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import algorithm.composers.kaida.Individual;
import basics.GUI;

public class FeaturesPanel extends JPanel {
	private JTable table;
	private Individual individual;
	private Insets insets;
	private JScrollPane scrollPane;
	
	public FeaturesPanel(Individual in, Dimension size) {
		super();
		BoxLayout layout = new BoxLayout(this,BoxLayout.PAGE_AXIS);
		this.setLayout(layout);
		
		if (in.isRated()) {
			individual = in;
			insets = new Insets(10,10,10,10);
	
			table = new JTable(new FeaturesGoalTableModel(in.getFeatures()));
			Dimension tableSize = new Dimension(size.width-insets.left-insets.right, size.height - insets.top - insets.bottom);
			//table.setSize(tableSize);
			scrollPane = new JScrollPane(table);
			//scrollPane.setSize(tableSize);
			table.setPreferredScrollableViewportSize(tableSize);
			this.add(scrollPane, BorderLayout.CENTER);
	        
			TableColumn column = null;
			for (int i = 0; i < 2; i++) {
			    column = table.getColumnModel().getColumn(i);
			    if (i == 0) {
			        column.setPreferredWidth(140); //sport column is bigger
			    } else {
			        column.setPreferredWidth(50);
			    }
			}
			
			
		}
		
		GUI.setAllSizes(this, size);
	}

}

package gui.bolscript.sequences;


import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import bolscript.config.RunParameters;

/**
 * A Cell is simply one beat (/matra)
 * The CellPanel has a CellBorder.
 * @see CellBorder
 * @author hannes
 *
 */
public class CellPanel extends JPanel {
	
	public static Color cBorder = new Color(20,20,20);
	public static Color cBackground = Color.WHITE;//new Color(150,250,150);
	public static Color cHighlight = new Color(200,255,200);
	private int thickness = 1;
	private boolean highlighted;

	public CellPanel(int x, int y, Dimension size) {
		super(null, false);
		
		this.setBounds(x,y,size.width,size.height);
		this.setBorder(CellBorder.getStandard(cBorder,1, SequencePanel.cellBorderPadding));
		this.setVisible(true);
		
		if (RunParameters.showLayoutStructure) {
			this.setOpaque(true);
			this.setBackground(new Color(220,220,220,200));
		} else {
			this.setBackground(cBackground);
			this.setOpaque(true);
		}

		highlighted = false;
	}

	public void setHighlight(boolean b) {
		// TODO Auto-generated method stub
		//System.out.println("cell.highlight: " + b);
		highlighted = b;
		if (b) {
			this.setBackground(cHighlight);
			
		} else {
			this.setBackground(cBackground);
		}
		
	}
	
}

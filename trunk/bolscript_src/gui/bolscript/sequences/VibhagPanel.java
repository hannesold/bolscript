package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import bols.tals.Vibhag;
import bolscript.config.GuiConfig;
import bolscript.config.RunParameters;

public class VibhagPanel extends JPanel {
	
	private Vibhag vibhag;
	private JLabel label;
	
	public VibhagPanel(int x, int y, Dimension size, Vibhag vibhag) {
		super(null, false);
		this.setBounds(x,y,size.width,size.height);
		this.setBorder(new VibhagBorder(Color.BLACK,GuiConfig.vibhagLineThicknesses[vibhag.getType()]));
		this.setVisible(true);
		this.setBackground(GuiConfig.vibhagPanelBackgroundColor);
		if (RunParameters.showLayoutStructure) {
			this.setOpaque(true);
			this.setBackground(new Color(180, 220, 180,180));
		} else this.setOpaque(false);
		this.vibhag = vibhag;
		
		this.add(getLabel(vibhag));
		this.setToolTipText(vibhag.getName());
	}
	
	public JLabel getLabel(Vibhag vibhag) {
		if (label!=null) {
			return label;
		} else {
			label = new JLabel(vibhag.getSymbol());
			label.setFont(GuiConfig.vibhagSymbolFont);
			label.setLocation(6,0);
			label.setSize(label.getPreferredSize().width, 10);
			return label;
		}
	}
	
}

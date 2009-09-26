package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.GUI;
import bols.tals.Vibhag;

public class VibhagPanel extends JPanel {
	
	public static Color cBorder = new Color(20,20,20);
	public static Color cBackground = Color.WHITE;
	private int thickness = 1;
	private Vibhag vibhag;
	private JLabel label;
	public static Font symbolFont;
	static {
		symbolFont = new Font("Arial",Font.PLAIN,11);
	}
	
	private static int[] vibhagLineThicknesses;
	static {
		vibhagLineThicknesses = new int[3];
		vibhagLineThicknesses[Vibhag.SAM] = 2;
		vibhagLineThicknesses[Vibhag.TALI] = 1;
		vibhagLineThicknesses[Vibhag.KALI] = 1;
	}
	public VibhagPanel(int x, int y, Dimension size, Vibhag vibhag) {
		super(null, false);
		
		this.setBounds(x,y,size.width,size.height);
		
		this.setBorder(new VibhagBorder(Color.BLACK,vibhagLineThicknesses[vibhag.getType()]));
		this.setVisible(true);
		this.setBackground(cBackground);
		if (GUI.showLayoutStructure) {
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
			label.setFont(symbolFont);
			label.setLocation(6,0);
			label.setSize(label.getPreferredSize().width, 10);
			return label;
		}
	}
	
}

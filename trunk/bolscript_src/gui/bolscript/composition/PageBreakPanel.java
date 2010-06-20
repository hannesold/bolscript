package gui.bolscript.composition;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import basics.GUI;

public class PageBreakPanel extends JPanel {
	
	public static final int LOW = 0;
	public static final int HIGH = 10;
	
	boolean active = false;
	int nrOfFollowingPage = 1;
	JLabel label;
	
	public boolean isActive() {
		return active;
	}

	
	public void setActive(boolean active, int pagenr) {
		this.active = active;
		nrOfFollowingPage = pagenr;
		if (active) {
			if (nrOfFollowingPage>0) {
				label.setText(" Page "+nrOfFollowingPage+ " ");
			} else {
				label.setText(" Page begin ");
			}
			Dimension size = GUI.getPrefferedSize(label, 200);
			label.setBounds(10,5,size.width, size.height);
		}
		setVisible(active);
		//
	}

	public Float position;
	public int quality;
	
	public PageBreakPanel(Float pageBreak, int width, int quality) {
		super(null);
		this.position = pageBreak;
		this.active = false;
		this.quality = quality;
		
				
		label = new JLabel(" Page begin ");
		label.setOpaque(true);	
		label.setForeground(new Color(0,0,0,0.6f));
		label.setLocation(10,5);
		label.setBackground(new Color(255,255,255));
		label.setBorder(new LineBorder(new Color(0,0,0,0.15f)));
		Dimension size = GUI.getPrefferedSize(label, 200);
		label.setBounds(10,5,size.width, size.height);
		this.add(label);
		
		this.setBorder(PageBreakBorder.getStandard(new Color(0,0,0,0.8f), 1, 0));		
		this.setBounds(0, pageBreak.intValue(), width, GUI.getBottom(label));
		this.setOpaque(false);
		
	}
}

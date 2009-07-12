package gui.bolscript.composition;

import java.awt.Color;

import javax.swing.JPanel;

public class PageBreakPanel extends JPanel {
	
	public static final int LOW = 0;
	public static final int HIGH = 10;
	
	boolean active = false;
	public Float position;
	public int quality;
	
	public PageBreakPanel(Float pageBreak, int width, int quality) {
		super(null);
		this.position = pageBreak;
		this.active = false;
		this.quality = quality;
		this.setBackground(Color.red);
		this.setBounds(0, pageBreak.intValue(), width, 1);
	}
}

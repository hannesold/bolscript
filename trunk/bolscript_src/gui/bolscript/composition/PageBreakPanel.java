package gui.bolscript.composition;

import java.awt.Color;

import javax.swing.JPanel;

import bolscript.config.GuiConfig;

public class PageBreakPanel extends JPanel {
	
	public static final int LOW = 0;
	public static final int HIGH = 10;
	
	boolean active = false;
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		this.setBackground((active)?new Color(0,0,0,0.5f):new Color(0,0,0,0.01f));
		this.setBounds(this.getBounds().x, this.getBounds().y, this.getBounds().width,(active?3:1));
		//
	}

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

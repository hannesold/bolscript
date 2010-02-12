package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class BolBorder implements Border {
	private Color color;
	private int thickness, stickHeight;
	
	public BolBorder(Color col, int stickHeight, int thickness) {
		super();
		this.color = col;
		this.thickness = thickness;
		this.stickHeight = stickHeight;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
	
		g.setColor(color);
		
		g.drawLine(0, y+height-stickHeight, 0, y+height-1);
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(thickness,0,0,0);
	}

	public boolean isBorderOpaque() {
		return false;
	}
}

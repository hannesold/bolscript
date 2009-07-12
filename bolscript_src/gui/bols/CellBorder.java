package gui.bols;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class CellBorder implements Border {
	private Color color;
	private int thickness;
	
	public CellBorder(Color col, int thickness) {
		super();
		this.color = col;
		this.thickness = thickness;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		g.setColor(color);
		g.drawLine(0,height-1,width,height-1);
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(0,0,0,thickness);
	}

	public boolean isBorderOpaque() {
		return false;
	}
}

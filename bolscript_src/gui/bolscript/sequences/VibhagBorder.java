package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class VibhagBorder implements Border {
	private Color color;
	private int thickness;
	
	public VibhagBorder(Color color, int thickness) {
		super();
		// TODO Auto-generated constructor stub
		this.color = color;
		this.thickness = thickness;
		
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		g.setColor(color);
		for (int i=0; i < thickness;i++) {
			g.drawLine(i+1,0,i+1,height);
		}
	}
	
	public Insets getBorderInsets(Component c) {
		// TODO Auto-generated method stub
		return new Insets(0,thickness+1,0,0);
	}
	
	public boolean isBorderOpaque() {
		// TODO Auto-generated method stub
		return false;
	}

}

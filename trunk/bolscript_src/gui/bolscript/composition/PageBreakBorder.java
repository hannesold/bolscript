package gui.bolscript.composition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import basics.Debug;

public class PageBreakBorder implements Border {
	private Color color;
	private int thickness;
	private int cellPadding;

	private Color[] steps;

	private int nrOfSteps;
	private int fadeWidth = 12;
	private int stepWidth = 1;

	private static PageBreakBorder standard;

	public PageBreakBorder(Color col, int thickness, int padding) {
		super();
		nrOfSteps = fadeWidth / stepWidth;
		this.color = col;
		int r, g, b;
		double a;
		r = color.getRed(); g = color.getGreen(); b = color.getBlue(); a = color.getAlpha();

		steps = new Color[nrOfSteps];

		for (int i=0; i < (nrOfSteps-1);i++) {
			int ai = (int) Math.max(0,Math.min(255,
					Math.round((a/((double)nrOfSteps))*(double)i)
			));
			steps[i] = new Color(r,g,b,ai);
		}

		steps[nrOfSteps-1] = new Color(r,g,b,(int)a);

		this.thickness = thickness;
		this.cellPadding = padding;
	}

	public static Border getStandard(Color col, int thickness, int padding) {
		if (standard == null) {
			standard = new PageBreakBorder(col,thickness,padding);
		} else {
			if (!(standard.color == col && standard.thickness == thickness && standard.cellPadding == padding)) {
				standard = new PageBreakBorder(col,thickness,padding);
			}
		}
		return standard;

	}
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {

		g.setColor(color);
		for (int z=0; z < thickness; z++) {
			int margin = cellPadding;
			if (2*margin < width) {
				g.drawLine(margin, z, width-margin, z);
			}
		}

	}

	public Insets getBorderInsets(Component c) {
		return new Insets(0,0,0,thickness);
	}

	public boolean isBorderOpaque() {
		return false;
	}
}

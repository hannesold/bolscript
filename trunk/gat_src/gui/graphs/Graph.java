package gui.graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import algorithm.tools.Timer;

public class Graph extends JPanel {
	private static final long serialVersionUID = -2379776404472147673L;
	protected Axis[] axes;
	protected Axis xAxis, yAxis;
	protected Color cBackground;
	private Color cGridLines;
	
	public Graph(int x, int y, int width, int height) {
		super(true);
		
		xAxis = new Axis("Generation", x, width, 0f, 50, 5f, false);
		xAxis.setValSubStep(1f);
		xAxis.setUnitFormat("%2.0f");
		yAxis = new Axis("Fitness", y, height, 0f, 1f, 0.1f, true);
		yAxis.setUnitFormat("%1.1f");
		axes = new Axis[2];
		axes[0] = xAxis;
		axes[1] = yAxis;
		cBackground = Color.WHITE;
		setBackground(cBackground);
		cGridLines = new Color(230,230,230);
		int margin = 150;
		this.setPreferredSize(new Dimension(x + width + margin, y + margin));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintGrid(g);
	}
	
	public Axis getXAxis() {
		return xAxis;
	}

	public Axis getYAxis() {
		return yAxis;
	}

	public void paintGrid(Graphics g) {
		Timer t = new Timer("draw Grid");
		t.mute();
		t.start();
		drawGridLines(g);
		drawRulers(g);
		t.stopAndPrint();
	}
	public void drawRulers(Graphics g) {
		xAxis.drawAxis(g, xAxis.getPos(), yAxis.getNullPos());
		yAxis.drawAxis(g, xAxis.getNullPos(), yAxis.getPos());
	}
	
	public void drawGridLines(Graphics g) {
		
		for (Axis ax: axes) {
			
			double v = ax.getValMin(); 
			double valStep = ax.getValStep();
			double valMax = ax.getValMax();
			int i = 0;
			if (!ax.isYAxis()) {
				//xAxis, do vertical lines
				int y0 = yAxis.getNullPos();
				int y1 = yAxis.projectPixel(yAxis.getValMax());
				
				while ( v <= valMax) {
					int p = ax.projectPixel(v);
					//draw a little line for each step 
					g.setColor(cGridLines);
					g.drawLine(p, y0, p, y1);
					i++;
					v = valStep * (double) i;
				}
			} else {
				//yAxis, do horizontals
//				xAxis, do vertical lines
				int x0 = xAxis.getNullPos();
				int x1 = xAxis.projectPixel(xAxis.getValMax());
				
				while ( v <= valMax) {
					int p = ax.projectPixel(v);
					//draw a little line for each step 
					g.setColor(cGridLines);
					g.drawLine(x0,p,x1,p);
					//draw unit
					i++;
					v = valStep * (double) i;
				}				
			}
			
		}		
	}
}

package gui.graphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Axis {
	
	private double valMax, valMin, valRange;
	private double valStep, valSubStep;
	private int pixSize, pixPosition;
	private int stepRad, subStepRad;
	private double pixScale;
	private String unitFormat;
	
	private boolean yAxis;
	private String label;
	
	private Color cLine, cStep, cLabel, cSubStep;
	
	public Axis(String label, int pixPosition, int pixSize, double valMin, double valMax, double valStep, boolean yAxis) {
		super();
		this.pixPosition = pixPosition;
		this.pixSize = pixSize;
		
		this.valMin = valMin;
		this.valMax = valMax;
		this.valRange = valMax-valMin;
		this.yAxis = yAxis;
		this.valStep = valStep;
		this.valSubStep = valStep/2f;
		this.stepRad = 3;
		this.subStepRad = 1;
		this.label = label;
		
		pixScale = ((double) pixSize) / valRange;
		
		cLine = Color.BLACK;
		cStep = Color.BLACK;
		cSubStep = Color.BLACK;
		cLabel = Color.BLACK;
		
		unitFormat = "%2.2f";
	}

	

	public int projectPixel(double val) {
		if (yAxis) {
			return pixPosition - (int) Math.round(pixScale * val);
		} else {
			return pixPosition + (int) Math.round(pixScale * val);	
		}
	}
	
	public int getNullPos() {
		return projectPixel(0f);
	}
	
	public int getPos() {
		return pixPosition;
	}

	public String valToString(double v) {
		String s = String.format(unitFormat,v);
		return s;
	}
	
	public void drawAxis(Graphics g, int xPos, int yPos) {
		
		if (yAxis) {
			//is an yAxis
			pixPosition = yPos;
			int x = xPos;
			double v = valMin;
			int i=0;
			
			//draw line
			g.setColor(cLine);
			g.drawLine(x, projectPixel(valMin), x, projectPixel(valMax));
			
			//draw units+their labels
			while ( v <= valMax) {
				int y = projectPixel(v);
				
				//draw a little line for each step 
				g.setColor(cStep);
				g.drawLine(x-stepRad, projectPixel(v), x+stepRad, projectPixel(v));
				
				//draw unit
				g.setFont(new Font("Arial",Font.PLAIN, 9));
				g.drawString(valToString(v), x-30, y+4);
				i++;
				v = valStep * (double) i;
			}
			//draw subunits
			if (valSubStep != 0f) {
				i = 0;
				v = valMin;
				//draw subunits
				while ( v <= valMax) {
					int y = projectPixel(v);
					
					//draw a little line for each step 
					g.setColor(cSubStep);
					g.drawLine(x-subStepRad, y, x+subStepRad, y);
					
					i++;
					v = valSubStep * (double) i;
				}
			}			
			
			//draw label
			g.setColor(cLabel);
			g.setFont(new Font("Arial",Font.PLAIN, 10));
			g.drawString(label, x-20, projectPixel(valMax)-10);	
			
		} else {
			//is an xAxis
			pixPosition = xPos;
			int y = yPos;
			//g.drawLine(projectPixel(minVal));	
			double v = valMin;
			int i=0;
			
			//draw line
			g.setColor(cLine);
			g.drawLine(projectPixel(valMin), y, projectPixel(valMax),y);
			
			//draw units+their labels
			while ( v <= valMax) {
				int x = projectPixel(v);
				
				//draw a little line for each step 
				g.setColor(cStep);
				g.drawLine(projectPixel(v), y-stepRad, projectPixel(v), y+stepRad);
				
				//draw unit
				g.setFont(new Font("Arial",Font.PLAIN, 9));
				g.drawString(valToString(v), x-2, y+20);
				i++;
				v = valStep * (double) i;
			}			
			if (valSubStep != 0f) {
				v = valMin;
				i =0;
				//draw subunits
				while ( v <= valMax) {
					int x = projectPixel(v);
					
					//draw a little line for each step 
					g.setColor(cSubStep);
					g.drawLine(x, y-subStepRad, x, y+subStepRad);
					
					i++;
					v = valSubStep * (double) i;
				}
			}
			
			//draw label
			g.setColor(cLabel);
			g.setFont(new Font("Arial",Font.PLAIN, 10));
			g.drawString(label, projectPixel(valMax)+10, y+5);	
		}
		
	}
	

	//getters and setters 
	public int getPixSize() {
		return pixSize;
	}
	public void setPixSize(int pixSize) {
		this.pixSize = pixSize;
	}
	public double getValMax() {
		return valMax;
	}
	public double getValMin() {
		return valMin;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getValStep() {
		return valStep;
	}

	public void setValStep(double valStep) {
		this.valStep = valStep;
	}

	public double getValSubStep() {
		return valSubStep;
	}

	public void setValSubStep(double valSubStep) {
		this.valSubStep = valSubStep;
	}



	public String getUnitFormat() {
		return unitFormat;
	}



	public void setUnitFormat(String unitFormat) {
		this.unitFormat = unitFormat;
	}



	public void setValMax(double valMax) {
		this.valMax = valMax;
		this.valRange = valMax - valMin;
		pixScale = ((double) pixSize) / valRange;
	}
	public void setValMin(double valMin) {
		this.valMin = valMin;
		this.valRange = valMax - valMin;
		pixScale = ((double) pixSize) / valRange;
	}



	public boolean isYAxis() {
		return yAxis;
	}



	
}

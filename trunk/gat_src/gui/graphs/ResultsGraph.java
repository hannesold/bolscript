package gui.graphs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import algorithm.statistics.FitnessStats;
import algorithm.statistics.TestRunGenerationStatistics;
import algorithm.tools.Timer;
import basics.GUI;

public class ResultsGraph extends Graph {
	
	static Color[] colors;
	static String[] views;
	static {
		colors = new Color[100];
		for (int i=0; i < colors.length; i++) {
			switch (i) {
			case 0:
				colors[i] = new Color(255,0,0,100);
				break;
			case 1:
				colors[i] = new Color(0,255,0,100);
				break;
			case 2:
				colors[i] = new Color(0,0,255,100);
				break;
			case 3:
				colors[i] = new Color(125,125,0,100);
				break;				
			default:
				colors[i] = new Color(200,200,200,100);
				break;
			}
		}
		views = new String[]{"Full", "Full with Top line", "Top and Average", "Top and Average (lines)"};
	}
	
	
	
	private static final long serialVersionUID = -2367926205076891846L;
	private ArrayList<TestRunGenerationStatistics> data;
	private Vector<JCheckBox> checkBoxes;
	private JPanel legend = null;
	private JComboBox viewSelector = null;
	private JPanel graph;
	
	public ResultsGraph(int x, int y, int width, int height, ArrayList<TestRunGenerationStatistics> data) {
		super(x, y, width, height);
		this.data = data;
		checkBoxes = new Vector<JCheckBox>(10);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getLegend());
		this.add(getViewSelector());
		
		this.setBorder(new EmptyBorder(y+25,x-5,0,0));
		//paintComponent(this.getGraphics());
		
	}
	
	private JPanel getLegend() {
		if (legend == null) {
			legend = new JPanel();
			legend.setBackground(Color.WHITE);

			legend.setBorder(new EmptyBorder(0,0,5,5));
			legend.setAlignmentX(0f);
			legend.setAlignmentY(0.15f);
			
			
			//legend.setPreferredSize(new Dimension(200,50));
//			JLabel lbl = new JLabel("Display");
//			lbl.setAlignmentX(0.1f);
//			lbl.setPreferredSize(new Dimension(100,20));
//			legend.add(lbl);
			
			BoxLayout layout = new BoxLayout(legend, BoxLayout.Y_AXIS);
			legend.setLayout(layout);
			
//			legend.setLayout(new FlowLayout());
			
			legend.add(Box.createRigidArea(new Dimension(1,15)));
			updateLegend();

		}
		return legend;
	}
	
	private JComboBox getViewSelector() {
		if (viewSelector == null) {
			viewSelector = new JComboBox();
			for (int i=0; i < views.length; i++) {
				viewSelector.addItem(views[i]);
			}
			viewSelector.setSelectedIndex(0);
			viewSelector.addActionListener(GUI.proxyActionListener(this,"onViewSelect"));
			viewSelector.setAlignmentX(0f);
			viewSelector.setBorder(new EmptyBorder(0,4,0,0));
			viewSelector.setOpaque(false);
			GUI.setAllSizes(viewSelector, new Dimension(150,20));
			
		}
		return viewSelector ;
	}

	public static Color makeTransparent(Color c, double alphaFactor) {
		Color c2 = new Color(c.getRed(),c.getGreen(),c.getBlue(), Math.round((double)c.getAlpha()*alphaFactor));
		return c2;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		updateLegend();
		drawPlots(g);
	}
	
	public void onCheckBox(ActionEvent e) {
		repaint();
	}
	
	public void onViewSelect(ActionEvent e) {
		repaint();
	}
	
	public void drawPlots(Graphics g) {
		drawPlots(g, data);
	}

	public void updateLegend() {
		boolean changeMade = false;
		for (int i = 0; i < data.size(); i++) {
			if (checkBoxes.size() <= i) {
				// entry panel
				JPanel entryPanel = new JPanel();
				entryPanel.setBackground(Color.WHITE);
				entryPanel.setAlignmentX(0f);
				BoxLayout lpLayout = new BoxLayout(entryPanel, BoxLayout.X_AXIS);
				entryPanel.setLayout(lpLayout);
				
				// checkbox
				JCheckBox checkBox = new JCheckBox();
				checkBox.setSelected(true);
				checkBoxes.add(checkBox);
				checkBox.addActionListener(GUI.proxyActionListener(this,"onCheckBox"));
				checkBox.setText(data.get(i).getLabel());
				checkBox.setForeground(colors[i]);
				checkBox.setBackground(Color.WHITE);
				entryPanel.add(checkBox);
				
				
				// title
//				JLabel title = new JLabel(data.get(i).getLabel());
//				title.setForeground(colors[i]);
//				entryPanel.add(title);
				
				// add to legend
				legend.add(entryPanel);
				System.out.println("legend entry added");
				changeMade= true;
			} else {
				
			}
			

		}		
		
		if (changeMade) {
			repaintLegend();
		}
	}
	
	public void repaintLegend() {
		legend.repaint();
		viewSelector.repaint();
	}

	public void drawPlots(Graphics g, ArrayList<TestRunGenerationStatistics> results) {
		
		synchronized (results) {
		if (results.size() > 0) {
			Timer t = new Timer("drawing results");
			t.start();
			t.mute();
						
			int j=0;
	
			for (TestRunGenerationStatistics result : results) {
				
				if (checkBoxes.get(j).isSelected()) {
					
//					through all result sheets
					switch (viewSelector.getSelectedIndex()) {
					case 1:
						drawPlotWithLines(g,result,colors[j]);
						break;
					case 2:
						drawPlotLines(g,result,colors[j]);
						break;
					case 3:
						drawPlotOnlyLines(g,result,colors[j]);
						break;
					default:
						drawPlot(g,result,colors[j]);
						break;
					}
				}
				
				j++;
			} //through all result sheets
			t.stopAndPrint();
			
		} //results exist
		} //synchronized
	
		
	}
	
	private void drawPlotWithLines(Graphics g, TestRunGenerationStatistics results, Color c) {
		//System.out.println("drawPlot()");
		for (int i=0; i < results.nrOfGenerations(); i++) {
			
			if (results.hasStat(i)) {
				if (results.hasStat(i+1)) {
						
					int[] xs = new int[4];
					int[] ys = new int[4];
					//clockwise
					//draw min-max polygon
					xs[0] = xAxis.projectPixel(i);
					ys[0] = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					xs[1] = xAxis.projectPixel(i+1);
					ys[1] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.MAXIMUMFITNESS));
					xs[2] = xAxis.projectPixel(i+1);
					ys[2] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.MINIMUMFITNESS));
					xs[3] = xAxis.projectPixel(i);
					ys[3] = yAxis.projectPixel(results.getStat(i,FitnessStats.MINIMUMFITNESS));
					g.setColor(makeTransparent(c, 0.3f));
					
					g.fillPolygon(xs, ys, 4);
					
					//draw stdDeviation polygon
					xs[0] = xAxis.projectPixel(i);
					ys[0] = yAxis.projectPixel(Math.min(1f,results.getStat(i,FitnessStats.AVERAGEFITNESS) + results.getStat(i,FitnessStats.STDDEVIATION)));
					xs[1] = xAxis.projectPixel(i+1);
					ys[1] = yAxis.projectPixel(Math.min(1f,results.getStat(i+1,FitnessStats.AVERAGEFITNESS) + results.getStat(i+1,FitnessStats.STDDEVIATION)));
					xs[2] = xAxis.projectPixel(i+1);
					ys[2] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.AVERAGEFITNESS) - results.getStat(i+1,FitnessStats.STDDEVIATION));
					xs[3] = xAxis.projectPixel(i);
					ys[3] = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS) - results.getStat(i,FitnessStats.STDDEVIATION));
					g.setColor(makeTransparent(c, 0.2f));
					g.fillPolygon(xs, ys, 4);
						
					//			draw maximum line
					int x = xAxis.projectPixel(i);
					int y = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					g.setColor(c);
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.MAXIMUMFITNESS)));
	
					
					//			draw average line
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					g.setColor(c);
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.AVERAGEFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					//draw average minibox
					g.drawRect(x-1,y-1,2,2);					
				} else {
					//ausfaden malen?
				}

			}
							
			
		}
		
	}


	public void drawPlotOnlyLines(Graphics g, TestRunGenerationStatistics results, Color c) {
		
		//System.out.println("drawPlot()");
		for (int i=0; i < results.nrOfGenerations(); i++) {
			
			if (results.hasStat(i)) {
				if (results.hasStat(i+1)) {
						

					//	draw maximum line
					int x = xAxis.projectPixel(i);
					int y = yAxis.projectPixel(results.getStat(i,FitnessStats.MINIMUMFITNESS));
					g.setColor(c);
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.MINIMUMFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.MINIMUMFITNESS));
					//draw maximum minibox
//					g.drawRect(x-1,y-1,2,2);
					
		//			draw maximum line
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					g.setColor(c);
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.MAXIMUMFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					//draw maximum minibox
//					g.drawRect(x-1,y-1,2,2);
					
					
		//			draw average line
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					//g.setColor(makeTransparent(c,0.5f));
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.AVERAGEFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					//draw average minibox
					g.drawRect(x-1,y-1,2,2);					
				} else {
					//ausfaden malen?
				}

			}
							
			
		}

	}
	public void drawPlotLines(Graphics g, TestRunGenerationStatistics results, Color c) {
		
		//System.out.println("drawPlot()");
		for (int i=0; i < results.nrOfGenerations(); i++) {
			
			if (results.hasStat(i)) {
				if (results.hasStat(i+1)) {
						
					int[] xs = new int[4];
					int[] ys = new int[4];
					//clockwise
					//draw min-max polygon
					xs[0] = xAxis.projectPixel(i);
					ys[0] = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					xs[1] = xAxis.projectPixel(i+1);
					ys[1] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.MAXIMUMFITNESS));
					xs[2] = xAxis.projectPixel(i+1);
					ys[2] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.MINIMUMFITNESS));
					xs[3] = xAxis.projectPixel(i);
					ys[3] = yAxis.projectPixel(results.getStat(i,FitnessStats.MINIMUMFITNESS));
					g.setColor(makeTransparent(c, 0.3f));
					
					g.fillPolygon(xs, ys, 4);
					
		//			draw maximum line
					int x = xAxis.projectPixel(i);
					int y = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					g.setColor(c);
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.MAXIMUMFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					//draw maximum minibox
//					g.drawRect(x-1,y-1,2,2);
					
					
		//			draw average line
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					//g.setColor(makeTransparent(c,0.5f));
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.AVERAGEFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					//draw average minibox
					g.drawRect(x-1,y-1,2,2);					
				} else {
					//ausfaden malen?
				}

			}
							
			
		}

	}
	
	public void drawPlot(Graphics g, TestRunGenerationStatistics results, Color c) {
	
		//System.out.println("drawPlot()");
		for (int i=0; i < results.nrOfGenerations(); i++) {
			
			if (results.hasStat(i)) {
				if (results.hasStat(i+1)) {
						
					int[] xs = new int[4];
					int[] ys = new int[4];
					//clockwise
					//draw min-max polygon
					xs[0] = xAxis.projectPixel(i);
					ys[0] = yAxis.projectPixel(results.getStat(i,FitnessStats.MAXIMUMFITNESS));
					xs[1] = xAxis.projectPixel(i+1);
					ys[1] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.MAXIMUMFITNESS));
					xs[2] = xAxis.projectPixel(i+1);
					ys[2] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.MINIMUMFITNESS));
					xs[3] = xAxis.projectPixel(i);
					ys[3] = yAxis.projectPixel(results.getStat(i,FitnessStats.MINIMUMFITNESS));
					g.setColor(makeTransparent(c, 0.3f));
					
					g.fillPolygon(xs, ys, 4);
					
					//draw stdDeviation polygon
					xs[0] = xAxis.projectPixel(i);
					ys[0] = yAxis.projectPixel(Math.min(1f,results.getStat(i,FitnessStats.AVERAGEFITNESS) + results.getStat(i,FitnessStats.STDDEVIATION)));
					xs[1] = xAxis.projectPixel(i+1);
					ys[1] = yAxis.projectPixel(Math.min(1f,results.getStat(i+1,FitnessStats.AVERAGEFITNESS) + results.getStat(i+1,FitnessStats.STDDEVIATION)));
					xs[2] = xAxis.projectPixel(i+1);
					ys[2] = yAxis.projectPixel(results.getStat(i+1,FitnessStats.AVERAGEFITNESS) - results.getStat(i+1,FitnessStats.STDDEVIATION));
					xs[3] = xAxis.projectPixel(i);
					ys[3] = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS) - results.getStat(i,FitnessStats.STDDEVIATION));
					g.setColor(makeTransparent(c, 0.2f));
					g.fillPolygon(xs, ys, 4);
						
					
		//			draw average line
					int x = xAxis.projectPixel(i);
					int y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					g.setColor(c);
					g.drawLine(x, y, xAxis.projectPixel(i+1), yAxis.projectPixel(results.getStat(i+1,FitnessStats.AVERAGEFITNESS)));
					
					x = xAxis.projectPixel(i);
					y = yAxis.projectPixel(results.getStat(i,FitnessStats.AVERAGEFITNESS));
					//draw average minibox
					g.drawRect(x-1,y-1,2,2);					
				} else {
					//ausfaden malen?
				}

			}
							
			
		}

	}

	public ArrayList<TestRunGenerationStatistics> getData() {
		return data;
	}

	public void setData(ArrayList<TestRunGenerationStatistics> data) {
		this.data = data;
	}


	


}

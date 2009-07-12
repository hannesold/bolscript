package algorithm.visualiser;

import gui.graphs.ResultsGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.swixml.SwingEngine;


public class AlgorithmTestVisualiser implements ActionListener, WindowListener{
	public JFrame frame;
	public JButton start, stop;
	public JPanel panelPlot;
	public JTextField txtRuns, txtGens;
	private AlgorithmPerformanceTests apt;
	private TimedUpdater graphUpdater;
	
	private ResultsGraph graph;
	private long graphUpdateFreq;
	
	public JProgressBar progress;
	
	public AlgorithmTestVisualiser(AlgorithmPerformanceTests apt) {
		this.apt = apt;
		try {
			new SwingEngine(this).render("AlgorithmTestVisualiser.xml").setVisible(true);
			
			txtRuns.setText(""+apt.getNrOfContests());
			txtGens.setText(""+apt.getNrOfGenerations());
			
			//panelPlot.getGraphics().drawLine(0,0,100,100);
			start.addActionListener(this);
			stop.addActionListener(this);
			
//			graph = new ResultsGraph(80,420,800,300, apt.getResults());
			graph = new ResultsGraph(80,350,800,300, apt.getResults());
			panelPlot.add(graph);
			frame.pack();
			graphUpdateFreq = 3000;
			graphUpdater = new TimedUpdater("graph updater", apt, graph, progress, graphUpdateFreq);
			
			frame.addWindowListener(this);
		} catch (Exception e ){
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}
	public void stop() {
		if ((apt.isAlive())) {//) && (apt.getState() == Thread.State.RUNNABLE)) {
			apt.setPleasePause(true);
			apt.interrupt();
			if (graphUpdater.isAlive()) {
				graphUpdater.interrupt();
			}
		}
	}
	

	public void start() {
		try {
			int runs = Integer.parseInt(txtRuns.getText());
			int generations = Integer.parseInt(txtGens.getText());
			if ((runs < 1)||(runs > 1000)||(generations<2||generations>1000)) {
				System.out.println("you may only enter full numbers in a decent range (maybe between 1 and 1000)");
			} else {
				apt.setNrOfContests(runs);
				apt.setNrOfGenerations(generations);
				graph.getXAxis().setValMax(generations);
			}	
		} catch (NumberFormatException e) {
			System.out.println("you may only enter full numbers");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}		
		
		if ((apt.getState() == Thread.State.NEW)&&(!apt.isAlive())) {
			apt.setPleasePause(false);
			apt.start();
			graphUpdater = new TimedUpdater("graph updater", apt, graph, progress, graphUpdateFreq);
			graphUpdater.start();
			
		} else if (apt.getState() == Thread.State.WAITING) {
			apt.setPleasePause(false);
			synchronized (apt) {	
				apt.notify();
			}
			graphUpdater = new TimedUpdater("graph updater", apt, graph, progress, graphUpdateFreq);
			graphUpdater.start();
		}
			
	}
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == start) {
			System.out.println("sender is start button");
			stop();
			start();
			graph.repaint();
		} else if (arg0.getSource() == stop) {
			System.out.println("sender is btnStop button");
			stop();
		} 
	}	
	
	
	public static void main (String[] args) {
		AlgorithmPerformanceTests apt = new AlgorithmPerformanceTests();
		AlgorithmTestVisualiser v = new AlgorithmTestVisualiser(apt);
	}
	
	public void drawPlot() {
		graph.paintComponent(graph.getGraphics());
	}
		
	public void windowClosing(WindowEvent e) {
		System.exit( 0 );	
	}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

}

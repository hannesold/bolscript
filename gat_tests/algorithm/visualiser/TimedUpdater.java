package algorithm.visualiser;


import gui.graphs.ResultsGraph;

import javax.swing.JProgressBar;


public class TimedUpdater extends Thread {
	private long intervall;
	private ResultsGraph graph;
	private boolean finished = false;
	private double progressLastTime;
	private AlgorithmPerformanceTests apt;
	private JProgressBar bar;
	
	public TimedUpdater(String name, AlgorithmPerformanceTests apt, ResultsGraph graph, JProgressBar bar, long intervall) {
		super(name);
		this.graph = graph;
		this.intervall = intervall;
		this.finished = false;
		this.apt = apt;
		progressLastTime = 0f;
		this.bar = bar;
	}
	
	public void run() {
		while (!isInterrupted() && !finished) {
			try {
				double prog = apt.getProgress();
				
				System.out.println("progress: " + prog);
				
				finished = (apt.getProgress() == 1.0f);
				
				if (progressLastTime != prog) {
					graph.repaint();
					progressLastTime = prog;
					bar.setValue((int) Math.round(prog*100f));
				}
				
				sleep(intervall);
			} catch (InterruptedException e) {
				graph.repaint();
//				graph.repaintLegend();
				interrupt();
			}
		}
		System.out.println("stoping timedupdater");
	}
	
	
}

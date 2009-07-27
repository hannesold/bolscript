package gui.bolscript;

import java.awt.TextArea;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTextPane;

import basics.Debug;
import bolscript.compositions.Composition;

public class RenderWorker implements Runnable {

	private CompositionPanel compPanel;
	private TextArea textArea;
	private Composition comp;
	private EditorFrame editor;
	private boolean stop = false;
	
	//private static ArrayList<Thread> workers;
	java.util.List<Thread> workers;

	
	static boolean running;
	
	public Thread renderThread;
	
	public RenderWorker (EditorFrame editor) {
		this.editor = editor;
		workers = Collections.synchronizedList(new ArrayList<Thread>());
		renderThread = new Thread(this);
		renderThread.setDaemon(true);
	}
	
	public void compFrameResized(ComponentEvent e) {
		Debug.debug(this, "called compFrameResized " + e);
		this.addUpdate();
	}
	
	public void addUpdate () {
		if (workers.size()>0) {
		if ((workers.get(workers.size()-1).getState() == Thread.State.NEW) ||
			(workers.get(workers.size()-1).getState() == Thread.State.TERMINATED)) {
			workers.clear();
		} 
		}
		
		Worker worker = new Worker(editor.getComposition(), editor.getCompositionPanel(), editor.getText());
		workers.add(new Thread(worker));
		renderThread.interrupt();
	}
	
	public void begin() {
		stop = false;
		Debug.debug(this, "starting...");
		renderThread.start();
	}
	
	public void stop() {
		stop = true;
		renderThread.interrupt();
	}
	
	public void run () {
		Debug.debug(this, "running");
		while (stop == false) {
			try {
				//Debug.debug(this, "Starting to wait now.");
				renderThread.sleep(60000);
			} catch (InterruptedException e) {
			}
			
			if (workers.size() > 0) {
				//Debug.debug(this, "starting worker...");
				Thread worker = workers.get(workers.size()-1);
				worker.setDaemon(true);
				worker.setName("CompositionRenderer");
				worker.run();
				workers.remove(worker);
			}
		}
		Debug.debug(this, "ending");
	}
	
	private class Worker implements Runnable {
		private String text;
		private Composition comp;
		private CompositionPanel compPanel;
		
		public Worker (Composition comp, CompositionPanel compPanel, String s) {
			this.text = s;
			this.comp = comp;
			this.compPanel = compPanel;
		}
		
		public boolean isRunning() {
			return running;
		}

		public void run() {
			comp.setRawData(text);
			comp.extractInfoFromRawData();
			compPanel.renderComposition(comp);
			editor.getDocument().updateStyles(comp.getPackets());
		}
		
		
	}
}

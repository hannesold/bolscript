package gui.bolscript;

import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;

import basics.Debug;
import bolscript.compositions.Composition;

/**
 * Contains a thread for parsing and rendering bolscripts.
 * This class is more of a sketch up, but it seems to work.
 * @author hannes
 *
 */
public class SkippingWorker implements Runnable {


	//	private EditorFrame editor;
	private boolean stop = false;

	private TaskFactory taskFactory;

	java.util.List<Thread> workers;

	static boolean running;

	/** A thread that runs an object of this class 
	 */
	public Thread thread;

	public SkippingWorker (TaskFactory taskFactory) {
		this.taskFactory = taskFactory;
		workers = Collections.synchronizedList(new ArrayList<Thread>());
		thread = new Thread(this);
		thread.setDaemon(true);
	}

	public void compFrameResized(ComponentEvent e) {
		Debug.debug(this, "called compFrameResized " + e);
		this.addUpdate();
	}

	public void addUpdate () {

		synchronized(workers) {
			if (workers.size()>0) {
				if ((workers.get(workers.size()-1).getState() == Thread.State.NEW) ||
						(workers.get(workers.size()-1).getState() == Thread.State.TERMINATED)) {
					workers.clear();
				} 
			}

			workers.add(new Thread(taskFactory.getNewTask()));
		}

		synchronized (thread) {
			if (thread.getState().equals(Thread.State.TIMED_WAITING)) {
				thread.interrupt();
			}
		}
	}

	public void begin() {
		stop = false;
		Debug.debug(this, "starting...");
		thread.start();
	}

	public void stop() {
		stop = true;
		thread.interrupt();
	}

	public void run () {
		Debug.debug(this, "running");
		while (stop == false) {
			try {
				//Debug.debug(this, "Starting to wait now.");
				Thread.sleep(60000); //pause the renderthread, wait for the next update.

			} catch (InterruptedException e) {
			}

			synchronized (workers) {
				if (workers.size() > 0) {
					Thread worker = workers.get(workers.size()-1);
					worker.setDaemon(true);
					worker.setName(taskFactory.getTaskName());
					worker.run();
					workers.remove(worker);
				}
			}
		}
		Debug.debug(this, "ending");
	}

}

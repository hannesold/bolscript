package gui.bolscript.tasks;

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

	private boolean stop = false;

	private TaskFactory taskFactory;

	java.util.List<Thread> workers;

	static boolean running;

	/** A thread that runs an object of this class 
	 */
	public Thread thread;

	long minimumUpdateIntervall = 100;

	long updateIntervall;

	boolean instantUpdates;
	/**
	 * 
	 * @param taskFactory The source to acquire new tasks from when addUpdate() is called.
	 * @param updateIntervallMillis The Intervall that the worker Thread waits before checking again for existing tasks (updates) (and processing them).
	 * @param instantUpdates If set to true the addUpdate method interrupts the sleeping Intervall, so the new update is executed instantly.
	 */
	public SkippingWorker (TaskFactory taskFactory, long updateIntervallMillis, boolean instantUpdates) {
		this.taskFactory = taskFactory;
		this.updateIntervall = Math.max(minimumUpdateIntervall, updateIntervallMillis);
		this.instantUpdates = instantUpdates;

		workers = Collections.synchronizedList(new ArrayList<Thread>());
		thread = new Thread(this);
		thread.setDaemon(true);
	}

	public void compFrameResized(ComponentEvent e) {
		Debug.debug(this, "called compFrameResized " + e);
		this.addUpdate();
	}

	public void addUpdate () {
		Runnable task = taskFactory.getNewTask();
		if (task != null) {

			synchronized(workers) {
				/**
				 * Skip all not yet processed workers
				 * remove any workers that have not been executed or are terminated
				 * 
				 */
				if (workers.size()>0) {
					if (workers.get(workers.size()-1).getState() == Thread.State.NEW) {
						workers.clear();
						Debug.temporary(this, "REMOVED SKIPPED WORKER");
					} 
				}
				workers.add(new Thread(task));
			}

			if (instantUpdates) {
				synchronized (thread) {
					if (thread.getState().equals(Thread.State.TIMED_WAITING)) {
						thread.interrupt();
					}
				}
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
		workers.clear();
		synchronized (thread) {
			if (thread.getState().equals(Thread.State.TIMED_WAITING)) thread.interrupt();
		}
	}

	public void run () {
		Debug.debug(this, "running");
		while (stop == false) {
			try {
				Thread.sleep(updateIntervall); //pause the renderthread, wait until the next update.
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

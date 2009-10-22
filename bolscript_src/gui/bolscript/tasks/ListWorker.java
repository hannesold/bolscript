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
public class ListWorker implements Runnable {


	private boolean stop = false;

	private TaskFactory taskFactory;

	java.util.List<Thread> workers;

	private ArrayList<TaskList> taskListQueue;


	/** A thread that runs an object of this class 
	 */
	public Thread thread;

	long minimumUpdateIntervall = 1;

	long updateIntervall;

	boolean instantUpdates;
	/**
	 * 
	 * @param taskFactory The source to acquire new tasks from when addUpdate() is called.
	 * @param updateIntervallMillis The Intervall that the worker Thread waits before checking again for existing tasks (updates) (and processing them).
	 * @param instantUpdates If set to true the addUpdate method interrupts the sleeping Intervall, so the new update is executed instantly.
	 */
	public ListWorker (long updateIntervallMillis, boolean instantUpdates) {
		this.updateIntervall = Math.max(minimumUpdateIntervall, updateIntervallMillis);
		this.instantUpdates = instantUpdates;
		
		taskListQueue = new ArrayList<TaskList>();
		
		thread = new Thread(this);
		thread.setDaemon(true);
	}


	public void addTaskList (TaskList newTaskList) {
		synchronized(taskListQueue) {
			taskListQueue.add(newTaskList);
			
			if (instantUpdates) taskListQueue.notifyAll();
		}
	}

	public void begin() {
		stop = false;
		Debug.debug(this, "starting...");
		thread.start();
	}

	public void stop() {
		stop = true;
		synchronized(taskListQueue) {
			taskListQueue.clear();
		}
		synchronized (thread) {
			if (thread.getState().equals(Thread.State.TIMED_WAITING)) thread.interrupt();
		}
	}

	public void run () {
		Debug.debug(this, "running");
		while (stop == false) {
			TaskList currentList = null;

			synchronized(taskListQueue) {
				while (taskListQueue.size() == 0 && stop == false) {
					try {
						taskListQueue.wait(updateIntervall);
					} catch(InterruptedException e) {
						Debug.temporary(this, "waiting interrupted");
					}
				}
				Debug.temporary(this, "choosing list to process from queue");
				//TODO
				//more elegantly please!
				currentList = taskListQueue.get(0);
				int indexOfListToBeProcessed = 0;
				int i = 1;
				while (i < taskListQueue.size() && taskListQueue.get(i).overridesPending(currentList)) {
					indexOfListToBeProcessed = i;
					currentList = taskListQueue.get(i);
					i++;
				}
				for (int j=0; j <= indexOfListToBeProcessed; j++) {
					taskListQueue.remove(0);
					Debug.temporary(this, "removing skipped lists");
				}
			}
			if (currentList!= null) {
				currentList.process();
				Debug.temporary(this, "completed processing of list");
				synchronized(taskListQueue) {
					taskListQueue.remove(currentList);
					//TODO
					// update this in case multiple instances of one list are in the queue
					Debug.temporary(this, "removing processed list from queue");
				}
			}
		}
		Debug.debug(this, "ending");
	}

	public synchronized boolean hasWork() {
		return workers.size() > 0;
	}
}



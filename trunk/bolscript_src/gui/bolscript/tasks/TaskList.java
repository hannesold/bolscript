package gui.bolscript.tasks;

import gui.bolscript.tasks.Task.ExecutionThread;

import java.awt.EventQueue;
import java.util.ArrayList;

import basics.Debug;

public abstract class TaskList {
	
	
	protected ArrayList<Task> tasks;
	protected String name;
	
	public abstract boolean overridesPending(TaskList taskList);
	
	public synchronized void process() {
		Debug.temporary(this, "process()");
		for (int i=0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			if (task.getThread() == ExecutionThread.AnyThread) {
				Debug.temporary(this, "running " + task.getName());
				task.run();
			} else if (task.getThread() == ExecutionThread.EventQueue) {
				Object lock = task.getLock();
				synchronized(lock) {
					Debug.temporary(this, "sending " + task.getName() +" to eventqueue");	
					EventQueue.invokeLater(task);					
					
					while (task.getState() != Task.State.Completed && task.getState() != Task.State.CompletedWithError) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							//e.printStackTrace();
						}
					}	
				}
			}
			
			if (task.getState() == Task.State.CompletedWithError) {
				Debug.critical(this, task.getName() + " had Error! Exception: " + task.getException());
				if (task.getException() != null) task.getException().printStackTrace();
			} else {
				Debug.temporary(this, task.getName() + " completed in " + task.getDuration() +"ms");
			}
		}
		
	}
}

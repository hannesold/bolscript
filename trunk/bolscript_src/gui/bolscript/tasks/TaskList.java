package gui.bolscript.tasks;

import gui.bolscript.tasks.Task.ExecutionThread;

import java.awt.EventQueue;
import java.util.ArrayList;

public abstract class TaskList {
	
	
	protected ArrayList<Task> tasks;
	
	public abstract boolean overridesPending(TaskList taskList);
	
	public synchronized void process() {

		for (int i=0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			if (task.getThread() == ExecutionThread.AnyThread) {
				task.run();
			} else if (task.getThread() == ExecutionThread.EventQueue) {
				
				synchronized(task.getLock()) {
					
					EventQueue.invokeLater(task);					
					
					while (task.getState() != Task.State.Completed) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			}
		}
		
	}
}

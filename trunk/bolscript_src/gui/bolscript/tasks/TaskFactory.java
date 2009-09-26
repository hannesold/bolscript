package gui.bolscript.tasks;

public interface TaskFactory {

	public Runnable getNewTask();
	
	String getTaskName();
}

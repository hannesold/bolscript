package gui.bolscript;

public interface TaskFactory {

	public Runnable getNewTask();
	
	String getTaskName();
}

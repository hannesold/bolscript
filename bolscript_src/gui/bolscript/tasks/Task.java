package gui.bolscript.tasks;

public abstract class Task implements Runnable{
	public enum ExecutionThread {
		/**
		 * 
		 */
		AnyThread,
		/**
		 * Swing related tasks shall be executed in the event queue
		 */
		EventQueue
	}
	public enum State {
		New,
		Running,
		Completed
	}
	
	protected String name;
	protected ExecutionThread thread;
	protected State state;
	protected Object lock;
	
	
	public Task(String name, ExecutionThread thread) {
		super();
		this.name = name;
		this.thread = thread;
		lock = new Object();
		state = State.New;
	}

	public Object getLock() {
		return lock;
	}
	
	public State getState() {
		return state;
	}

	public ExecutionThread getThread() {
		return thread;
	}

	public String getName() {
		return name;
	}
	
	public void run() {
		synchronized (lock) {
			state = State.Running;
			doTask();
			state = State.Completed;
			lock.notifyAll();
		}
	}
	
	public abstract void doTask();
}

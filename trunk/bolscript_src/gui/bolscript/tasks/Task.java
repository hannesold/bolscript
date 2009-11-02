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
		Completed,
		CompletedWithError
	}
	public class TaskException extends Exception{
		private Exception reason;
		private String message;
		
		public TaskException(String message, Exception reason) {
			super();
			this.message = message;
			this.reason = reason;
		}

		public Exception getReason() {
			return reason;
		}
		
	}

	protected String name;
	protected ExecutionThread thread;
	protected State state;
	protected Object lock;
	protected long duration = -1;
	public Exception getException() {
		return exception;
	}

	protected Exception exception = null;
	
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
			try {
				state = State.Running;
				long time = System.currentTimeMillis();
				doTask();
				duration = System.currentTimeMillis() - time;
				state = State.Completed;
			} catch (TaskException e) {
				state = State.CompletedWithError;
				if (e.getReason() != null) {
					exception = e.getReason();
				} else {
					exception = e;
				}
			} catch (Exception e) {
				state = State.CompletedWithError;
				exception = e;
			}
			lock.notifyAll();

		}
	}

	public long getDuration() {
		return duration;
	}

	public abstract void doTask() throws TaskException;
}

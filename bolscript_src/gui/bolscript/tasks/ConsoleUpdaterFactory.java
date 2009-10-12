package gui.bolscript.tasks;

import java.awt.EventQueue;

import basics.ErrorConsole;

public class ConsoleUpdaterFactory implements TaskFactory {

	public ConsoleUpdaterFactory(ErrorConsole console) {
		super();
		this.console = console;
	}

	private ErrorConsole console;
	
	@Override
	public Runnable getNewTask() {
		return new RefreshTask();
	}

	@Override
	public String getTaskName() {
		return "Console Updater";
	}

	class RefreshTask implements Runnable{
		public void run() {
			console.refreshLater();
		}
		
	}
}

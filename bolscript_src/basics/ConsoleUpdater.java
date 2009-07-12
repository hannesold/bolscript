package basics;

import java.util.EventObject;

public class ConsoleUpdater extends EventObject implements Runnable{

	ErrorConsole console;
	
	public ConsoleUpdater(Object source) {
		super(source);
	}

	public ConsoleUpdater(Object source, ErrorConsole console) {
		super(source);
		this.console = console;
	}

	public void run() {
		console.refreshTextField();
	}
	

}

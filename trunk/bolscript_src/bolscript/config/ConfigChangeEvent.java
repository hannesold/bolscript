package bolscript.config;

import java.util.EventObject;

public class ConfigChangeEvent extends EventObject implements Runnable {

	ConfigChangeListener listener;
	public ConfigChangeEvent(Object source, ConfigChangeListener listener) {
		super(source);
		this.listener = listener;
	}

	public void run() {
		listener.configChanged(this);
		
	}

}

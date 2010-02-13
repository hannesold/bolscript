package bolscript.config;

import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;

public class ConfigChangeEvent extends EventObject implements Runnable {

	HashSet<String> changedKeys;
	ConfigChangeListener listener;
	public ConfigChangeEvent(Object source, ConfigChangeListener listener, String[] changedPreferenceKeys) {
		super(source);
		changedKeys = new HashSet<String> ();
		for (String key:changedPreferenceKeys) {
			changedKeys.add(key);
		}
		
		this.listener = listener;
	}

	public boolean hasChanged(String PreferenceKey) {
		return (changedKeys.contains(PreferenceKey));
	}
	
	public void run() {
		listener.configChanged(this);
		
	}

}

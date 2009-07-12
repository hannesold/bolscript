package bolscript;

import basics.Debug;
import basics.GUI;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

public class MasterMac extends Master implements ApplicationListener{
	
	public static Application application;
	
	public MasterMac() {
		super();
		runningAsMacApplication = true;
	}
	
	@Override
	public void init() {
		super.init();
		application = Application.getApplication();
		application.addPreferencesMenuItem();
		application.setEnabledPreferencesMenu(true);
		application.addApplicationListener(this);
	}
	
	public static void main(String [] args) {
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		int i=0;
		while (i<args.length) {
			if (args[i].equalsIgnoreCase("showLayout")) {
				GUI.showLayoutStructure = true;
				debug.debug("showing layout");
			}
			if (args[i].equalsIgnoreCase("noDebug")) {
				Debug.setMute(true);
			}
			i++;
		}	
		master = new MasterMac();
		master.init();
	}
	
	@Override
	public void showPreferences() {
		super.showPreferences();
		browserFrame.setVisible(true);
	}
	
	public void handleAbout(ApplicationEvent arg0) {
	}

	public void handleOpenApplication(ApplicationEvent arg0) {
	}

	public void handleOpenFile(ApplicationEvent arg0) {
	}

	public void handlePreferences(ApplicationEvent arg0) {
		showPreferences();
	}

	public void handlePrintFile(ApplicationEvent arg0) {
	}

	public void handleQuit(ApplicationEvent arg0) {
		exit();
	}

	public void handleReOpenApplication(ApplicationEvent arg0) {
	}

}

package gui.bolscript.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;

import basics.Debug;
import basics.Tools;

public class OpenURL extends AbstractAction {
	Desktop desktop = null;
	String url = "";

	public OpenURL(String url, String title) {
		this.putValue(NAME, title);
		this.url=url;
		this.setEnabled(false);
		if (java.awt.Desktop.isDesktopSupported() ) {
			desktop = java.awt.Desktop.getDesktop();				
			if(desktop.isSupported( java.awt.Desktop.Action.BROWSE )) {
				this.setEnabled(true);
			}
		}		
	}
	
	public void actionPerformed(ActionEvent e) {
		Debug.debug(this, "trying to browse to " + url);
		if (desktop != null) {
			
			try {
				URI uri = Tools.URIFromDangerousPlainTextUrl(url);				
				Debug.debug(this, "telling desktop to browse to " + uri);				
				desktop.browse(uri);
			} catch (Exception ex) {
				
			}
		}
		
	}
		
		
}

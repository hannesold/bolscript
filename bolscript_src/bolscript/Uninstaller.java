package bolscript;

import javax.swing.JOptionPane;

import bolscript.config.UserConfig;

public class Uninstaller {
	public static void main(String [] args)  {
		
		try {
			UserConfig.uninstall();
			JOptionPane.showMessageDialog(null, "Bolscript preferences deleted");
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}

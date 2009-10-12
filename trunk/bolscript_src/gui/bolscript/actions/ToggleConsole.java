package gui.bolscript.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import basics.Debug;

public class ToggleConsole extends AbstractAction {

	private static ToggleConsole standard;

	public ToggleConsole() {
		super();
		refreshName();
	}
	
	private void refreshName() {
		if (Debug.isShowingConsole()) {
			this.putValue(NAME, "Hide Errorconsole");
		} else {
			this.putValue(NAME, "Show Errorconsole");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (Debug.isShowingConsole()) {
			Debug.hideErrorConsole();
		} else {
			Debug.showErrorConsole();
		}
		refreshName();
	}

	public static ToggleConsole getStandard() {
		if (standard == null) standard = new ToggleConsole();
		return standard;
	}

}

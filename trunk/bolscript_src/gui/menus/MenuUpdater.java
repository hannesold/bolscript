package gui.menus;

import gui.bolscript.actions.EnablingUpdatable;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import basics.Debug;

/**
 * Provides a method for updating the Enabled State (enabled or not) of each menu item in a menu bar.
 * @author hannes
 *
 */
public class MenuUpdater {
	JMenuBar menuBar;
	long menuUpdateCount = 0;
	
	public MenuUpdater(JMenuBar menuBar) {
		super();
		this.menuBar = menuBar;
	}

	/**
	 * Searches through all menus and menuitems and updates the enabling state of all
	 * menu entries that have an associated action of type EnablingUpdatable.
	 */
	public void updateEnabling() {
		Debug.temporary(this, "update Menus..." + menuUpdateCount++);
		int count = menuBar.getMenuCount();

		for (int i = 0; i < count; i++) {
			JMenu menu = menuBar.getMenu(i);
			Debug.debug(this, "processing menu: " + menu.getName());
			for ( int j = 0; j < menu.getItemCount(); j++) {
				JMenuItem item = menu.getItem(j);
				
				Debug.debug(this, "processing item:  " + item);
				if (item != null) {
					Action action = item.getAction();
					if (action != null) {
						Debug.debug(this, "processing action:  " + action.getValue("Name"));
						if (EnablingUpdatable.class.isInstance(action)) {
							Debug.debug(this, "is enablingUpdatable! ");
							((EnablingUpdatable) action).updateEnabling();
							Debug.debug(this, "set to " +  action.isEnabled());
							item.setEnabled(action.isEnabled());
						}
					}
				}
			}
		}
	}




}

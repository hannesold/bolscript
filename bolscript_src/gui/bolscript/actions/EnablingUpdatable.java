package gui.bolscript.actions;

public interface EnablingUpdatable {
	
	/**
	 * The Object finds out if it should be en- or disabled and sets itselfs state accordingly.
	 * @return the new enabling state (true = enabled, false = disabled)
	 */
	boolean updateEnabling();
}

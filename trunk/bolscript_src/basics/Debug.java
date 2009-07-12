package basics;

import gui.bolscript.CompositionFrame;
import gui.bolscript.CompositionPanel;
import gui.bolscript.FilterPanel;
import gui.bolscript.actions.ChooseTablaDir;
import gui.bolscript.dialogs.PreferencesDialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.HashMap;

import bols.BolBase;
import bols.BolBaseGeneral;
import bols.BolName;
import bols.tals.TalBase;
import bolscript.Master;
import bolscript.Reader;
import bolscript.filters.SearchTextFilterCategorized;
import config.Config;

/**
 * <p>This class is used for debug messages of different debug codes.</p>
 * <p>Two maps map classes and debug codes to their visibilities,
 * so the output of specific classes can be turned on or off. When exclusive mapping is used, only the classes who are mapped visible have visible output.</p>
 * <p>It can either be used statically, or a debug object can be instantiated. 
 * When using statically an Object or a Class has to be passed to each command.</p>
 * 
 * 
 * @see Debugcodes
 * @author hannes
 * 
 */
public class Debug {
	
	public static HashMap<Class, Boolean> classMap = new HashMap<Class,Boolean>();
	public static HashMap<Debugcodes, Boolean> codeMap = new HashMap<Debugcodes,Boolean>();
	
	public static ErrorConsole console;
	
	private static boolean exclusivelyMapped = true;
	private static boolean mute = false;
	private static boolean consoleShowing = false;
	private static boolean initialised = false;
	
	private Class owner;
	
	static {
		if (!initialised) {
			System.out.println("Debug: init");
			init();
		}

	}
	
	/**
	 * Sets the class-visibility-maps.
	 * @param visible
	 * @param invisible
	 */
	public static void initClassMaps(Class[] visible, Class[] invisible) {
		classMap.clear();
		for (Class c: visible) {
			classMap.put(c, true);
		}
		for (Class c: invisible) {
			classMap.put(c, false);
		}
	}
	
	/**
	 * Initializes Debug.
	 */
	public static void init () {
		initialised = true;
		console = new ErrorConsole(new Dimension(400,300));
		console.setVisible(false);
		consoleShowing = false;
	}
	

	
	public Debug (Class c) {
		this.owner = c;
	}
	
	/*public void critical (Object obj) {
		out(c, Debugcodes.CRITICAL, obj.toString());
	}*/
	
	public void critical (Object message) {
		out(owner, Debugcodes.CRITICAL, message);
	}
	public static void critical (Object caller, Object message) {
		out(caller.getClass(), Debugcodes.CRITICAL, message);
	}
	public static void critical (Class caller, Object message) {
		out(caller, Debugcodes.CRITICAL, message);
	}
	
	public void temporary (Object message) {
		out(owner, Debugcodes.TEMPORARY, message);
	}
	
	public static void temporary (Object caller, Object message) {
		out(caller.getClass(), Debugcodes.TEMPORARY, message);
	}
	public static void temporary (Class caller, Object message) {
		out(caller, Debugcodes.TEMPORARY, message);
	}
	
	public void debug (Object message) {
		out(owner, Debugcodes.DEBUG, message);
	}
	public static void debug (Object caller, Object message) {
		out(caller.getClass(), Debugcodes.DEBUG, message);
	}
	public static void debug (Class caller, Object message) {
		out(caller, Debugcodes.DEBUG, message);
	}
	
	public void user (Object message) {
		out(owner, Debugcodes.USER, message);
	}
	public static void user (Object caller, Object message) {
		out(caller.getClass(), Debugcodes.USER, message);
	}
	public static void user (Class caller, Object message) {
		out(caller, Debugcodes.USER, message);
	}
	
	/**
	 * This does the output of a message, it also adds it to an error console
	 * if it is activated.
	 * Muting does not effect this method.
	 * @param message
	 */
	public static void out (Object message) {
		System.out.println(message);
		if (consoleShowing) {
			console.addText(message + "\n");
			EventQueue.invokeLater(new ConsoleUpdater(console, console));
		}
	}
	
	/**
	 * Shows a message independantly from classes and class map.<br>
	 * Shows no message if muted, unless the debugcode is CRITICAL.
	 * @param d the debug code
	 * @param message the message
	 */
	public static void out (Debugcodes d, Object message) {
		if (mute == false || d == Debugcodes.CRITICAL) {
			if (codeMap.containsKey(d)) {
				if (codeMap.get(d) == true ) {
					out(d + ": " + message);
				} //else no output
			} else {
				out(message);
			}
		}
	}
	
	/**
	 * <p>Shows a message, composed of calling class, debug code and message.</p>
	 * <p>CRITICAL messages are displayed in any case, else messages are showed in the following cases<br>
	 * 
	 * <li>Exclusive mapping is turned off, the classes visibility is not mapped to false and the debug code is mapped to true.</li>
	 * <li>Exclusive mapping is turned on, the classes visibility is mapped to true and the debug code is mapped to true.</li>
	 * </p>
	 * @param c the class
	 * @param d the debug code
	 * @param message the message
	 */
	public static void out (Class c, Debugcodes d, Object message) {
		
		String classname = c.getSimpleName();
		if (d == Debugcodes.CRITICAL) {
			//show critical messages in any case
			out(d + ": " + classname + ": "  + message);
		} else {
			if (mute == false) {
				if (classMap.containsKey(c)) {
					if (classMap.get(c).booleanValue() == true) {
						if (codeMap.containsKey(d)) {
							if (codeMap.get(d).booleanValue() == true) {
								out(d + ": " + classname + ": " + message);

							} // sonst nix
						} else {
							out(d + ": " + classname + ": "  + message);
						}
					}//sonst nix
				} else if (!isExclusivelyMapped()) {
					if (codeMap.containsKey(d)) {
						if (codeMap.get(d).booleanValue() == true) {
							out(d + ": " + classname + ": " + message);

						} // sonst nix
					} else {
						out(d + ": " + classname + ": "  + message);
					}
				}
			}
		}
	}

	/**
	 * <p>Opens a SWING-based Error console, to which all output is forwarded</p>
	 */
	public static void showErrorConsole() {
		if (!consoleShowing) {
			consoleShowing = true;
			console.setVisible(true);
		}
	}
	

	/**
	 * <p>Clears the class map and adds only the passed class.</p>
	 * <p>This unMutes Debug and turns on exclusive mapping.
	 * All previous settings are lost.</p>
	 * <p>This method is useful for controlling the output of Test cases.</p>
	 * 
	 * @param c the class to exclusively show
	 */
	public static void exclusivelyShowClass(Class c) {
		setExclusivelyMapped(true);
		classMap.clear();
		classMap.put(c, true);
		unMute();
		
	}
	
	public static void setExclusivelyMapped(boolean exclusivelyMapped) {
		Debug.exclusivelyMapped = exclusivelyMapped;
	}

	public static boolean isExclusivelyMapped() {
		return exclusivelyMapped;
	}

	public static void mute() {
		setMute(true);
	}
	public static void unMute() {
		setMute(false);
	}
	
	public static void setMute(boolean turnoff) {
		Debug.mute = turnoff;
	}

	public static boolean isMuted() {
		return mute;
	}
}

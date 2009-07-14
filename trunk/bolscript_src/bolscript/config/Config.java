package bolscript.config;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.JTable;

import basics.Debug;
import basics.Rational;
import bols.BolName;
import bols.BundlingDepthToSpeedMap;

public class Config {

	public static String fileSeperator = System.getProperty("file.separator");
	public static String workingDir = 	System.getProperty("user.dir");
	public static String homeDir = 	System.getProperty("user.home");

	public static String tablaFolder = null;// = "";
	public static String pathToCompositions = "";// = "/Users/hannes/Projekte/Workspace/tabla/bols/";
	public static String pathToCompositionsNoSlash = "";// = "/Users/hannes/Projekte/Workspace/tabla/bols";
	public static String pathToTals = "";// = pathToCompositions + "tals";
	public static String pathToTalsNoSlash = "";// = pathToCompositions + "tals";
	public static String pathToFonts = "";
	public static String pdfExportPath = null;
	
	public static String pathToDevanageriFont = "";
	public static String bolscriptSuffix = ".txt";
	public static String talSuffix = "tal.txt";
	public static String pdfSuffix = ".pdf";
	
	public static String pathToBolBase = "";// = pathToCompositions + "bolbase.bolbase";

	public static final int BOLSCRIPT_MAXIMUM_SPEED = 128;
	public static final int BOLSCRIPT_MAXIMUM_REPETITIONS = 20;
	public static final Rational BOLSCRIPT_MAXIMUM_SPEED_R = new Rational(BOLSCRIPT_MAXIMUM_SPEED);

	public static int standardLanguage = BolName.SIMPLE;

	public static Color colorUnvenRows;
	public static Color colorEvenRows;
	public static Color tableBG;

	public static Font[] bolFonts = new Font[BolName.languagesCount];
	public static Font[] bolFontsBold = new Font[BolName.languagesCount];
	public static float[] bolFontSizeStd = new float[BolName.languagesCount];
	public static float[] bolFontSizeMin = new float[BolName.languagesCount];
	public static float[] bolFontSizeMax = new float[BolName.languagesCount];
	
	public static HashMap<Float, Font>[] bolFontsSized;
	public static HashMap<Float, Font>[] bolFontsSizedBold;
	public static float fontSizeStep = 2f;
	public static float fontSizeAtomicStep = 0.5f;
	
	/**
	 * The standard setting for bundling. Initially it is 0, meaning, there is no bundling active.
	 * This can be changed during the running of the program.
	 */
	public static int stdBundlingDepth = 0;
	
	/**
	 * This is the standard setting for fontsizeIncrease
	 */
	public static float stdFontSizeIncrease = 0f;
	
	/**
	 * A Map from maximum Speeds ocurring to depth-speed-maps.
	 */
	private static HashMap<Rational, BundlingDepthToSpeedMap> bundlingMaps;
	
	
	public static Preferences preferences;

	public static boolean firstRun = true;
	public static final String propertiesFilename = "settings.xml";
	public static final int MAC = 1111, WINDOWS = 2222;
	public static int OS = 0;
	public static boolean initialised = false;
	
	public static Config config;
	
	static ArrayList<ConfigChangeListener> listeners;

	static {
		if (!initialised) {
			init();
		}
	}
	public static void init () {
		config = new Config();
		listeners = new ArrayList<ConfigChangeListener>();
		
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			OS = MAC;
		} else {
			if (System.getProperty("os.name").toLowerCase().startsWith("windows")) OS = WINDOWS;
		}

		initFromPreferencesfile();
		
		initBundlingMaps();
		
		initColors();

		initFonts();
		
		initBolFontsSized();	

		initialised = true;
	}
	
	private static void initColors() {
		JTable table = new JTable();
		colorUnvenRows = table.getBackground();
		Color selBG = table.getSelectionBackground();
		colorEvenRows = new Color (241,245,250);//selBG.brighter().brighter().brighter().brighter();
		tableBG = new Color(217,217,217);
	}
	
	/**
	 * Most importantly attempts to load the String tablaFolder from the Properties file,
	 * then calling setTablaFolder.
	 */
	private static boolean initFromPreferencesfile() {
		preferences = Preferences.userNodeForPackage(Config.class);
		
		boolean failed = false;
		try {
			tablaFolder = preferences.get("tablaFolder", null);

			if (tablaFolder == null) {
				firstRun = true;
				tablaFolder = homeDir;
				pdfExportPath = null;
			} else {
				setTablaFolder(tablaFolder);
				firstRun = false;
			}
			pdfExportPath = preferences.get("pdfExportPath", tablaFolder);

		} catch (Exception e) {
			Debug.critical(Config.class, "Preferences could not be loaded " + e);
			failed = false;
		}

		return failed;
	}
	
	
	private static void initBundlingMaps() {
		bundlingMaps = new HashMap<Rational, BundlingDepthToSpeedMap> ();
	}
	
	/**
	 * Returns a Map of bundling depths to bundling speeds, according to the given maxSpeed.
	 */
	public static BundlingDepthToSpeedMap getBundlingDepthToSpeedMap(Rational maxSpeed) {
		BundlingDepthToSpeedMap map = bundlingMaps.get(maxSpeed);
		if (map == null) {
			map = BundlingDepthToSpeedMap.getDefault(maxSpeed);
			bundlingMaps.put(maxSpeed, map);
		}
		return map;
	}

	/**
	 * Inits the standard font sizes (std, min, max) for each language and 
	 * the default fonts for each language.
	 */
	public static void initFonts() {
		for (int i=0; i < BolName.languagesCount; i++) {
			bolFontSizeMin[i] = 6f;
			bolFontSizeStd[i] = 11f;
			bolFontSizeMax[i] = 48f;
			bolFonts[i] = new Font("Arial", Font.PLAIN, (int) bolFontSizeStd[i]);
			bolFontsBold[i] = new Font("Arial", Font.PLAIN, (int) bolFontSizeStd[i]);
		}
		bolFontSizeStd[BolName.DEVANAGERI] = 13f;

		//trying to load devanageri font from file
		try {
			bolFonts[BolName.DEVANAGERI] = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream(pathToDevanageriFont) ).deriveFont(bolFontSizeStd[BolName.DEVANAGERI]);
			bolFontsBold[BolName.DEVANAGERI] = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream(pathToDevanageriFont) ).deriveFont(bolFontSizeStd[BolName.DEVANAGERI]).deriveFont(Font.BOLD);
			Debug.temporary(Config.class, "Devanageri font loaded: " + bolFontsBold[BolName.DEVANAGERI]);
		} catch (FileNotFoundException e) {
			Debug.critical(Config.class, "Devanageri Font not found in '" + pathToDevanageriFont + "', using default.");
			e.printStackTrace();
		} catch (FontFormatException e) {
			Debug.critical(Config.class, "Devanageri Font could not load: " + pathToDevanageriFont+ "', using default.");
			e.printStackTrace();
		} catch (IOException e) {
			Debug.critical(Config.class, "Devanageri Font could not load: " + pathToDevanageriFont+ "', using default.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates maps from languages to size->font maps.
	 */
	private static void initBolFontsSized() {
		bolFontsSized = new HashMap[BolName.languagesCount];
		bolFontsSizedBold =new HashMap[BolName.languagesCount];
		
		for (int i=0; i < BolName.languagesCount;i++) {
			HashMap<Float, Font> currentMap = new HashMap<Float, Font>();
			HashMap<Float, Font> currentBoldMap = new HashMap<Float, Font>();
			float j = bolFontSizeMin[i];
			while (j <= bolFontSizeMax[i]) {
				currentMap.put(j, bolFonts[i].deriveFont(j));
				currentBoldMap.put(j, bolFontsBold[i].deriveFont(j).deriveFont(Font.BOLD));
				j+=fontSizeAtomicStep;
			}
			
			bolFontsSized[i] = currentMap;
			bolFontsSizedBold[i] = currentBoldMap;
			//Debug.temporary(Config.class, "bolfontssized: " + bolFontsSized[i]);
		}		
	}

	/**
	 * Expects the language and its size entry in bolFontsSized(Bold) entry to exist,
	 * otherwise returns default font.
	 * @param language
	 * @param size
	 * @param bold
	 * @return
	 */
	public static Font getBolFont(int language, float size, boolean bold) {
		Font f = (bold)?bolFontsSizedBold[language].get(size): bolFontsSized[language].get(size);
		if (f!=null) {
			return f;
		} else return bolFonts[language];
		
	}
	
	/**
	 * Stores the properties file.
	 * @throws Exception
	 */
	public static void storeProperties() throws Exception{
			if (tablaFolder != null) {
				preferences.put("tablaFolder", tablaFolder);
			}
			if (pdfExportPath != null) {
				preferences.put("pdfExportPath",pdfExportPath);
			}
			Debug.debug(Config.class, "storing properties under : " + new File(propertiesFilename).getAbsoluteFile());
		try {
			preferences.flush();
			Debug.debug(Config.class, "Preferences stored");
		} catch (Exception e) {
			Debug.critical(Config.class, "Preferences could not be stored: " + e + ", " + e.getMessage());
			throw e;
		}
	}
	
	public static void fireConfigChangedEvent() {
		Debug.debug(Config.class, "CONFIG CHANGED!");
		for (ConfigChangeListener listener:listeners) {
			EventQueue.invokeLater(new ConfigChangeEvent(config, listener));
		}
	}

	public static void addChangeListener(ConfigChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public static void removeChangeListener(ConfigChangeListener listener) {
		listeners.remove(listener);
	}
	
	public static void setPdfExportPath(String folder) {
		pdfExportPath = folder;
	}
	
	/**
	 * Sets and initialises the tablaFolder and the resulting subfolders settings compositions fonts etc,
	 * checking each for existence and creating the nonexistent.
	 * Especially pathToDevanageriFont is established.
	 * 
	 * @param chosenFolder
	 */
	public static void setTablaFolder(String chosenFolder) {
		Debug.temporary(Config.class, "setTablaFolder : " + chosenFolder + " (old: " + tablaFolder + ")");
		//if (!tablaFolder.equals(chosenFolder)) {
			tablaFolder = chosenFolder;
			preferences.put("tablaFolder", tablaFolder);

			File s = new File(chosenFolder + Config.fileSeperator + "settings");
			if (!s.exists()) {
				s.mkdir();
			}

			File c = new File(chosenFolder + Config.fileSeperator + "compositions");
			if (!c.exists()) { 
				c.mkdir();
			}
			
			pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
			File fontFolder = new File(pathToFonts);
			if (!fontFolder.exists()) {
				fontFolder.mkdir();
			}

			pathToBolBase = s.getAbsolutePath() + fileSeperator + "bolbase.bolbase";
			Debug.temporary(Config.class, pathToBolBase);
			pathToCompositionsNoSlash = c.getAbsolutePath();
			Debug.temporary(Config.class, pathToCompositionsNoSlash);
			pathToCompositions = pathToCompositionsNoSlash + fileSeperator;
			Debug.temporary(Config.class, pathToCompositions);
			pathToTalsNoSlash = c.getAbsolutePath() + fileSeperator + "tals";
			Debug.temporary(Config.class, pathToTalsNoSlash);
			pathToTals = pathToTalsNoSlash + fileSeperator;
			pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
			Debug.temporary(Config.class, pathToTals);
			pathToDevanageriFont = s.getAbsolutePath() + fileSeperator + "devanageri.ttf";
			

	//	}

	}

	public static void setStandardBundlingDepth(int bundlingDepth) {
		stdBundlingDepth = bundlingDepth;
	}

	public static void setStandardFontSizeIncrease(float fontsizeIncrease) {
		Config.stdFontSizeIncrease = fontsizeIncrease;
	}

}

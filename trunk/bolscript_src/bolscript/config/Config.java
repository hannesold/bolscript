package bolscript.config;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JTable;

import basics.Debug;
import basics.Rational;
import basics.ZipTools;
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
	//public static String pathToFonts = "";
	public static String pdfExportPath = null;
	
	public static String pathToDevanageriFont = "";
	public static String bolscriptSuffix = ".bols.txt";
	public static String talSuffix = ".tal.bols.txt";
	public static String bolBaseSuffix = ".bolbase.txt";
	public static String pdfSuffix = ".pdf";
	public static String bolBaseFilename = "bolbase" + bolBaseSuffix;
	
	public static String pathToBolBase = "";// = pathToCompositions + "bolbase.bolbase";

	public static final double BOLSCRIPT_MINIMUM_SPEED = 1d/128d;
	public static final int BOLSCRIPT_MAXIMUM_SPEED = 128;
	public static final int BOLSCRIPT_MAXIMUM_REPETITIONS = 100;
	public static final int BOLSCRIPT_MAXIMUM_TRUNCATION = 100;
	public static final Rational BOLSCRIPT_MAXIMUM_SPEED_R = new Rational(BOLSCRIPT_MAXIMUM_SPEED);

	public static int standardLanguage = BolName.SIMPLE;

	public static Font[] bolFonts = new Font[BolName.languagesCount];
	public static Font[] bolFontsBold = new Font[BolName.languagesCount];
	public static float[] bolFontSizeStd = new float[BolName.languagesCount];
	public static float[] bolFontSizeMin = new float[BolName.languagesCount];
	public static float[] bolFontSizeMax = new float[BolName.languagesCount];
	
	public static HashMap<Float, Font>[] bolFontsSized;
	public static HashMap<Float, Font>[] bolFontsSizedBold;
	public static final float fontSizeStep = 2f;
	public static final float fontSizeAtomicStep = 0.5f;
	
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
	public static final int MENU_SHORTKEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static int OS = 0;
	public static boolean initialised = false;
	
	public static Config config;
	
	static ArrayList<ConfigChangeListener> listeners;
	public static int maxBolscriptFileSize = 200 * 1024; // 200kb is maximum size.
	public static String compositionEncoding = "UTF-8";
	public static String bolBaseEncoding = "UTF-16";
	public static Object lineSeperator ="\n";
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
		GuiConfig.colorUnvenRows = table.getBackground();
		Color selBG = table.getSelectionBackground();
		GuiConfig.colorEvenRows = new Color (241,245,250);//selBG.brighter().brighter().brighter().brighter();
		GuiConfig.tableBG = new Color(217,217,217);
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
	 * @see BundlingDepthToSpeedMap
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
	 * Stores the part of config, that is kept in preferences.
	 * @throws Exception
	 */
	public static void storePreferences() throws Exception{
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
			
			/*pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
			File fontFolder = new File(pathToFonts);
			if (!fontFolder.exists()) {
				fontFolder.mkdir();
			}*/

			pathToBolBase = s.getAbsolutePath() + fileSeperator + bolBaseFilename;
			//Debug.temporary(Config.class, pathToBolBase);
			
			
			pathToCompositionsNoSlash = c.getAbsolutePath();
			//Debug.temporary(Config.class, pathToCompositionsNoSlash);
			pathToCompositions = pathToCompositionsNoSlash + fileSeperator;
			//Debug.temporary(Config.class, pathToCompositions);
			pathToTalsNoSlash = c.getAbsolutePath() + fileSeperator + "tals";
			//Debug.temporary(Config.class, pathToTalsNoSlash);
			pathToTals = pathToTalsNoSlash + fileSeperator;
			//pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
			//Debug.temporary(Config.class, pathToTals);
			pathToDevanageriFont = s.getAbsolutePath() + fileSeperator + "devanageri.ttf";
			
			String[] requiredFiles = new String[]{pathToBolBase, pathToDevanageriFont};
			boolean allEssentialsExist = true;
			for (int i=0; i < requiredFiles.length; i++ ) {
				allEssentialsExist = allEssentialsExist & (new File(requiredFiles[i])).exists();
				if (!allEssentialsExist) break;
			}
			if (!allEssentialsExist) {
				//defaults are copied non-destructively from the jar archives resources.zip
				try {
					extractDefaultTablafolder(chosenFolder);
				} catch (Exception e) {
					Debug.critical(Config.class, "Default tabla folder could not be extracted.");
					Debug.critical(Config.class, e);
				}
			}

	//	}

	}
	
	public static void extractDefaultTablafolder(String targetFolder) throws Exception {
		Debug.temporary(Config.class, "extracting default tabla folder to target folder");
		URI uri = Config.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		Debug.temporary(Config.class, "uri.getpath " + uri.getPath());
		
		String jarPath;
		if (uri.getPath().endsWith(".jar")) {
			//the program is running from a jar file
			jarPath = uri.getPath();
		} else { 
			//the program is running from eclipse or so
			//a builds/bolscript.jar is needed in any case
			jarPath = (new File(uri.getPath()).getParent()) + fileSeperator + "builds" + fileSeperator + "bolscript.jar"; 
		}
		
		Debug.temporary(Config.class, "jarPath " + jarPath);
		JarFile jar = new JarFile(jarPath);
		
		//this is launched from a jar or app bundle.
		//the resources.zip file needs to be extracted to a temporary destination
		//before proceeding. It shall be deleted afterwards

		ZipEntry entry = jar.getEntry("resources.zip");
		String resourcesTempPath = targetFolder + fileSeperator + "resources_temp.zip";
		Debug.debug(Config.class, "Extracting temporary resources zip to " + resourcesTempPath);
		
		//extract the resources zip file from the jar bundle to a temporary destination
		File tempFile = ZipTools.extractOneFile(entry.toString(), resourcesTempPath, jar.getInputStream(entry));			
		
		//zip file can now be opened
		Debug.temporary(Config.class, "opening zipfile: " + resourcesTempPath);
		ZipFile resources = new ZipFile(tempFile);

		ZipTools.extractSubentries(targetFolder, resources, resources.getEntry("tablafolder_default"));

		tempFile.delete();
		
	}

	public static void setStandardBundlingDepth(int bundlingDepth) {
		stdBundlingDepth = bundlingDepth;
	}

	public static void setStandardFontSizeIncrease(float fontsizeIncrease) {
		Config.stdFontSizeIncrease = fontsizeIncrease;
	}

}

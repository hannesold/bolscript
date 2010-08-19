package bolscript.config;

import java.awt.EventQueue;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import basics.Debug;
import basics.Rational;
import basics.ZipTools;
import bols.BundlingDepthToSpeedMap;

public class Config {

	public static String fileSeperator 	= System.getProperty("file.separator");
	public static String workingDir 	= 	System.getProperty("user.dir");
	public static String homeDir 		= 	System.getProperty("user.home");

	public static String pathToTals = "";// = pathToCompositions + "tals";
	public static String pathToTalsNoSlash = "";// = pathToCompositions + "tals";
	public static String pathToDevanageriFont = "";
	
	public static String pathToCompositionsNoSlash = "";// = "/Users/hannes/Projekte/Workspace/tabla/bols";
	public static String pathToCompositions = "";// = "/Users/hannes/Projekte/Workspace/tabla/bols/";

	public static String bolscriptSuffix = ".bols.txt";
	public static String bolBaseSuffix = ".bolbase.txt";
	public static String pdfSuffix = ".pdf";
	public static String bolBaseFilename = "bolbase" + bolBaseSuffix;

	public static String pathToBolBase = "";// = pathToCompositions + "bolbase.bolbase";

	public static final double 	BOLSCRIPT_MINIMUM_SPEED = 1d/128d;
	public static final int 	BOLSCRIPT_MAXIMUM_SPEED = 128;
	public static final int 	BOLSCRIPT_MAXIMUM_REPETITIONS = 100;
	public static final int 	BOLSCRIPT_MAXIMUM_TRUNCATION = 100;
	public static final Rational BOLSCRIPT_MAXIMUM_SPEED_R = new Rational(BOLSCRIPT_MAXIMUM_SPEED);

	public static final int 	MAC = 1111, WINDOWS = 2222;
	public static int 			OS = 0;
	public static boolean 		initialised = false;

	public static Config config;

	static ArrayList<ConfigChangeListener> listeners;
	public static int maxBolscriptFileSize = 200 * 1024; // 200kb is maximum size.
	public static String compositionEncoding = "UTF-8";
	public static String bolBaseEncoding = "UTF-16";
	public static Object bolscriptStandardLineSeperator ="\n";
	
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

		UserConfig.initFromStoredPreferences();
		
		GuiConfig.initColors();
		GuiConfig.initFonts();
		GuiConfig.initBolFontsSized();	
		
		initialised = true;
	}

	public static void fireConfigChangedEvent(String ... changedPreferenceKeys) {
		Debug.debug(Config.class, "CONFIG CHANGED!");
		for (ConfigChangeListener listener:listeners) {
			EventQueue.invokeLater(new ConfigChangeEvent(config, listener, changedPreferenceKeys));
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

	public static String getJarPath() throws Exception {

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
		return jarPath;
	}
	
	public static void extractDefaultLibraryFolder(String targetFolder) throws Exception {
		Debug.temporary(Config.class, "extracting default library to target folder");

		String jarPath = getJarPath();
		Debug.temporary(Config.class, "jarPath " + jarPath);
		JarFile jar = new JarFile(jarPath);

		//this is launched from a jar or app bundle.
		//the resources.zip file needs to be extracted to a temporary destination
		//before proceeding. It shall be deleted afterwards
		
		ZipEntry entry = jar.getEntry("resources.zip");
		
		String resourcesTempPath = targetFolder + fileSeperator + "resources_temp.zip";
		Debug.debug(Config.class, "Extracting temporary resources zip to " + resourcesTempPath);

		URL zipUrl = Config.class.getResource("/resources.zip");
		Debug.temporary(Config.class, "zipurl: " + zipUrl);
		//ZipFile zipFile = new ZipFil
		
		//extract the resources zip file from the jar bundle to a temporary destination
		File tempFile = ZipTools.extractOneFile(entry.toString(), resourcesTempPath, jar.getInputStream(entry));			

		//zip file can now be opened
		Debug.temporary(Config.class, "opening zipfile: " + resourcesTempPath);
		ZipFile resources = new ZipFile(tempFile);

		ZipTools.extractSubentries(targetFolder, resources, resources.getEntry("libraryfolder_default"));
		
		resources.close();
		

		tempFile.delete();

	}
	
	public static VersionInfo getVersionInfoFromJar(String jarPath) {
		try {
		
			Debug.temporary(Config.class, "attempt to read version info from jarPath " + jarPath);
			JarFile jar = new JarFile(jarPath);		
			VersionInfo versionInfo = new VersionInfo(jar.getManifest());
			return versionInfo;
		} catch (Exception ex ){
			return null;
		}
	}

	
}

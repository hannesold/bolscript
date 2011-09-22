package bolscript.config;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import basics.Debug;
import basics.Tools;
import bols.BolName;

import static bolscript.config.PreferenceKeys.*;

public class UserConfig {

	public static String libraryFolder = null;
	
	public static String pdfExportPath = null;
	
	private static String userId = "Unknown";
	
	private static UserConfig standard = null;
	
	public static final String USER_ID_REGEX = "(\\w+)(\\w|\\s|[\\.\\-_])*";
	private static final String DEFAULT_USER_ID = "Unknown";
	
	/**
	 * This is the standard setting for fontsizeIncrease
	 * 0 means that there is no increase, so the standard value bolFontSizeStd is taken
	 */
	public static float stdFontSizeIncrease = 0f;
	
	/**
	 * The standard setting for bundling. Initially it is 0, meaning, there is no bundling active.
	 * This can be changed during the running of the program.
	 */
	public static int stdBundlingDepth = 0;

	public static int standardLanguage = BolName.SIMPLE;

	public static Preferences preferences;

	public static boolean firstRun = true;
	public static boolean hasChosenLibraryFolderThisRun = false;
	
	public static String tableSettingsAsXml = null;
	
	public UserConfig() {
	}
	
	public static UserConfig getCurrentUser() {
		if (standard == null) {
			standard = new UserConfig();
		}
		return standard;
	}

	/**
	 * Stores the part of config, that is kept in preferences.
	 * @throws Exception
	 */
	public static void storePreferences() throws Exception{
		if (libraryFolder != null) {
			UserConfig.preferences.put(LIBRARY_FOLDER, libraryFolder);
		}
		if (pdfExportPath != null) {
			UserConfig.preferences.put(PDF_EXPORT_PATH,pdfExportPath);
		}
		if (getUserId() != null) {
			UserConfig.preferences.put(USER_ID, getUserId());
		}
		
		preferences.putInt(		STD_BUNDLING_DEPTH, 	stdBundlingDepth);
		preferences.putFloat(	STD_FONT_SIZE_INCREASE, stdFontSizeIncrease);
		preferences.putInt(		STANDARD_LANGUAGE, 		standardLanguage);
		
		TableDisplaySettings tablesettings = GuiConfig.getCurrentTableDisplaySettings();
		if (tablesettings != null) {
			try {				
				String xml = tablesettings.ToXml();
				preferences.put(PreferenceKeys.TABLE_SETTINGS, xml);
				tableSettingsAsXml = xml;
			} catch(Exception ex){
				Debug.debug(UserConfig.class, "Could not serialize comp-table display settings for storage in preferences");
			}			
		}
		
		//Debug.debug(Config.class, "storing properties under : " + new File(propertiesFilename).getAbsoluteFile());
		try {
			UserConfig.preferences.flush();
			Debug.debug(Config.class, "Preferences stored");
		} catch (Exception e) {
			Debug.critical(Config.class, "Preferences could not be stored: " + e + ", " + e.getMessage());
			throw e;
		}
	}

	public static void uninstall() throws Exception {
		if (preferences == null) {
			preferences = Preferences.userNodeForPackage(Config.class);
		}
		preferences.clear();
		preferences.flush();
		Debug.temporary(UserConfig.class, "uninstalled");
	}
	
	/**
	 * Most importantly attempts to load the String libraryFolder from the Properties file,
	 * then calling setLibraryFolder.
	 */
	static boolean initFromStoredPreferences() {
		UserConfig.preferences = Preferences.userNodeForPackage(Config.class);
	
		boolean failed = false;
		
		try {
	
			libraryFolder = preferences.get(LIBRARY_FOLDER, 
					preferences.get(PreferenceKeys.DeprecatedKeys.TABLA_FOLDER,
							null));
	
			if (libraryFolder == null) {
				UserConfig.firstRun = true;
				libraryFolder = Config.homeDir;
				pdfExportPath = null;
			} else {
				UserConfig.setLibraryFolder(libraryFolder);
				UserConfig.firstRun = false;
			}
			pdfExportPath = preferences.get(PDF_EXPORT_PATH, libraryFolder);			
			
			setUserIdAfterValidation(preferences.get(USER_ID, DEFAULT_USER_ID));
			
			stdBundlingDepth 	= Tools.assure(0, preferences.getInt(STD_BUNDLING_DEPTH, 0), 4);
			stdFontSizeIncrease = preferences.getFloat(STD_FONT_SIZE_INCREASE, 0);
			standardLanguage 	= Tools.assure(0, 
					preferences.getInt(STANDARD_LANGUAGE, BolName.SIMPLE), 
					BolName.languagesCount);
			
			
			tableSettingsAsXml = preferences.get(PreferenceKeys.TABLE_SETTINGS, null); 
			
		} catch (Exception e) {
			Debug.critical(Config.class, "Preferences could not be loaded " + e);
			failed = false;
		}
	
		return failed;
	}

	
	public static void setPdfExportPath(String folder) {
		pdfExportPath = folder;
	}

	/**
	 * Sets and initialises the libraryFolder and the resulting subfolders settings compositions fonts etc,
	 * checking each for existence and creating the nonexistent.
	 * Especially pathToDevanageriFont is established.
	 * 
	 * @param chosenFolder
	 */
	public static void setLibraryFolder(String chosenFolder) {
		hasChosenLibraryFolderThisRun = true;
		Debug.temporary(Config.class, "setLibraryFolder : " + chosenFolder + " (old: " + libraryFolder + ")");
		//if (!libraryFolder.equals(chosenFolder)) {
		libraryFolder = chosenFolder;
		preferences.put(LIBRARY_FOLDER, libraryFolder);
	
	
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
	
		Config.pathToBolBase = s.getAbsolutePath() + Config.fileSeperator + Config.bolBaseFilename;
		//Debug.temporary(Config.class, pathToBolBase);
	
		Config.pathToCompositionsNoSlash = c.getAbsolutePath();
		Debug.temporary(Config.class, "pathToCompositionsNoSlash: " + Config.pathToCompositionsNoSlash);
		Config.pathToCompositions = Config.pathToCompositionsNoSlash + Config.fileSeperator;
		Debug.temporary(Config.class, Config.pathToCompositions);
		Config.pathToTalsNoSlash = c.getAbsolutePath() + Config.fileSeperator + "tals";
		//Debug.temporary(Config.class, pathToTalsNoSlash);
		Config.pathToTals = Config.pathToTalsNoSlash + Config.fileSeperator;
		//pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
		//Debug.temporary(Config.class, pathToTals);
		Config.pathToDevanageriFont = s.getAbsolutePath() + Config.fileSeperator + "devanageri.ttf";
	
		String[] requiredFiles = new String[]{Config.pathToBolBase, Config.pathToDevanageriFont};
		boolean allEssentialsExist = true;
		for (int i=0; i < requiredFiles.length; i++ ) {
			allEssentialsExist = allEssentialsExist & (new File(requiredFiles[i])).exists();
			if (!allEssentialsExist) break;
		}
		if (!allEssentialsExist) {
			//defaults are copied non-destructively from the jar archives resources.zip
			try {
				Config.extractDefaultLibraryFolder(chosenFolder);
			} catch (Exception e) {
				Debug.critical(Config.class, "Default library could not be extracted.");
				Debug.critical(Config.class, e);
			}
		}
	
		GuiConfig.initFonts();
		GuiConfig.initBolFontsSized();
		
		//	}
	
	}

	public static void setStandardFontSizeIncrease(float fontsizeIncrease) {
		UserConfig.stdFontSizeIncrease = fontsizeIncrease;
	}

	public static void setStandardBundlingDepth(int bundlingDepth) {
		UserConfig.stdBundlingDepth = bundlingDepth;
	}


	public static boolean validateUserId(String candidate) {
		String userIdRegex = "(\\w+)(\\w|\\s|[\\.\\-_])*";
		Pattern pattern = Pattern.compile(userIdRegex);
		if (pattern.matcher(candidate).matches()) return true;
		return false;	
	}

	public static boolean setUserIdAfterValidation(String candidate) {
		if (validateUserId(candidate)) {
			userId = candidate;
			
			return true;
		} else {
			return false;
		}
	}

	public static String getUserId() {
		return userId;
	}

	public static boolean userIdIsOnDefaultSetting() {
		return userId.equals(DEFAULT_USER_ID);
	}

}

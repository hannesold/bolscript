package bolscript.config;

import java.util.HashMap;

public class PreferenceKeys {
	
	public class DeprecatedKeys {
		public static final String TABLA_FOLDER = "tablaFolder";
	}
	
	public static final String USER_ID = "USER_ID";
	
	public static final String STD_BUNDLING_DEPTH = "stdBundlingDepth";

	public static final String PDF_EXPORT_PATH = "pdfExportPath";

	public static final String STANDARD_LANGUAGE = "standardLanguage";

	public static final String STD_FONT_SIZE_INCREASE = "stdFontSizeIncrease";

	public static final String LIBRARY_FOLDER = "libraryFolder";
	
	public static final String TABLE_SETTINGS = "tableSettings";
	
	public static final String WINDOW_WIDTH = "browserWindowWidth";
	
	public static final String WINDOW_HEIGHT= "browserWindowHeight";
	
	/*private static HashMap<String, String> deprecatedMap = new HashMap<String,String>();
	static {
		deprecatedMap.put(DeprecatedKeys.TABLA_FOLDER, LIBRARY_FOLDER);
	}
	public static String deprecatedKeyToCurrentKey(String oldKey) {
		return deprecatedMap.get(oldKey);
	}*/
}

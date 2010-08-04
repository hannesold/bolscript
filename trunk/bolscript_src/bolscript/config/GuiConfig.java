package bolscript.config;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTable;

import basics.Debug;
import bols.BolName;
import bols.tals.Vibhag;

/**
 * A class for storing the gui configuration
 * @author hannes
 *
 */
public class GuiConfig {

	//Menu settings
	/**
	 * This is the OS-Dependent Shortkey Mask. On Windows it will be assigned to CTRL, on Mac to CMD
	 */
	public static final int MENU_SHORTKEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	//Table colors
	public static Color colorEvenRows;
	public static Color colorUnvenRows;
	public static Color tableBG;
	
	//Footnote Panels
	public static Color footnotePanelColor =Color.getHSBColor(0.5f, 0.7f, 0.1f);
	public static int  footnoteFontSize = 9;
	public static Font footnoteFont = new Font("Arial", Font.BOLD, GuiConfig.footnoteFontSize);
	
	//Comment Packets
	public static int commentFontSize = 12;
	public static Font commentFont = new Font("Arial", Font.PLAIN, commentFontSize);
	
	//Composition panel
	public static int compositionPanelMarginSide = 10;
	public static Color compositionPanelBackgroundColor = (new JPanel()).getBackground();
	
	//Sequence Panels
	public static Color sequencePanelHighlightColor = new Color (68, 117, 207);
	public static Color sequencePanelBorderColor = (new JPanel()).getBackground().darker();
	public static Color sequencePanelBackgroundColor = Color.WHITE;
	
	//BolPanelGeneral BolPanel BolBundlePanel
	public static Color bolNonWellDefinedColor = new Color(200,50,50);
	public static Color bolFontColor = Color.black;
	public static Color bolHighlightedColor = colorEvenRows;
	public static Color bolNeighbourColor = new Color (240,240,255);
	public static Color bolFontHighlightColor = new Color(50,50,255);
		
	//CommaPanel
	public static Color commaColor =Color.getHSBColor(0.5f, 0.7f, 0.1f);
	public static Font 	commaFont= new Font("Arial", Font.BOLD, GuiConfig.commaFontSize);
	public static int 	commaFontSize = 9;
	
	//Vibhag Panels
	public static Color vibhagPanelBackgroundColor = Color.WHITE;
	public static Font vibhagSymbolFont = new Font("Arial",Font.PLAIN,11);
	public static int[] vibhagLineThicknesses;
	static {
		GuiConfig.vibhagLineThicknesses = new int[3];
		GuiConfig.vibhagLineThicknesses[Vibhag.SAM] = 2;
		GuiConfig.vibhagLineThicknesses[Vibhag.TALI] = 1;
		GuiConfig.vibhagLineThicknesses[Vibhag.KALI] = 1;
	}
	
	//Bol Panels
	public static Font[] bolFonts = new Font[BolName.languagesCount];
	public static Font[] bolFontsBold = new Font[BolName.languagesCount];
	
	/**
	 * A cached map: display-language -> Fontsize -> Font
	 */
	public static HashMap<Float, Font>[] bolFontsSized;
	/**
	 * A cached map: display-language -> Fontsize -> Font (Bold)
	 */
	public static HashMap<Float, Font>[] bolFontsSizedBold;
	public static float[] 	bolFontSizeStd = new float[BolName.languagesCount];
	public static float[] 	bolFontSizeMin = new float[BolName.languagesCount];
	public static float[] 	bolFontSizeMax = new float[BolName.languagesCount];
	public static final float fontSizeStep = 2f;
	public static final float fontSizeAtomicStep = 0.5f;
	
	static Dimension limitByScreen(int wantedWidth, int wantedHeight, double maxPercentOfScreenWidth, double maxPercentOfScreenHeight) {
		//get local graphics environment
		GraphicsEnvironment graphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
				
		//get maximum window bounds
		Rectangle maximumWindowBounds=graphicsEnvironment.getMaximumWindowBounds();

		
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
        //Dimension screenDim = toolkit.getScreenSize();
        return new Dimension(Math.min(
        		(int)Math.round(
        				maxPercentOfScreenWidth* ((double)maximumWindowBounds.width)), 
        				wantedWidth
        				),
        		Math.min(
        				(int)Math.round(
        				maxPercentOfScreenHeight* ((double)maximumWindowBounds.height)), 
        				wantedHeight));
	}

	public static Dimension getBrowserFrameDimension() {
		return limitByScreen(400,800,0.5,1);
	}
	public static Dimension getCompositionViewerSize() {
		return limitByScreen(600,700,0.5,1);
	}
	public static Dimension getEditorSize() {
		return limitByScreen(410,700,0.5,1);
	}

	public static void initFonts() {
		String[][] bolFontNamesPreferred = new String[BolName.languagesCount][];
		
		//Set all preferred font names to default
		String bolFontDefaultName = "Arial";
		
		if (!existsFontFamily(bolFontDefaultName)) {
			Debug.critical(GuiConfig.class, "Default Font: " + bolFontDefaultName + " could not be found!");
		}
		
		for (int i=0; i < BolName.languagesCount; i++) {
			bolFontNamesPreferred[i] = new String[]{bolFontDefaultName};
		} 
		//Set preferred fonts for translit
		bolFontNamesPreferred[BolName.TRANSLITERATION] = new String[]{"Tahoma", bolFontDefaultName};
		
		for (int i=0; i < BolName.languagesCount; i++) {
			bolFontSizeMin[i] = 6f;
			bolFontSizeStd[i] = 12f;
			bolFontSizeMax[i] = 48f;
			
			//Attempt to set preferred fonts
			for (int j=0; j < bolFontNamesPreferred.length; j++) {
				if (existsFontFamily(bolFontNamesPreferred[i][j])) {
					bolFonts[i] 	= new Font(bolFontNamesPreferred[i][j], Font.PLAIN, (int) bolFontSizeStd[i]);
					bolFontsBold[i] = new Font(bolFontNamesPreferred[i][j], Font.PLAIN, (int) bolFontSizeStd[i]);
					break;
				}
			}
		}
		
		bolFontSizeStd[BolName.DEVANAGERI] = 13f;
	
		//trying to load devanageri font from file
		try {
			bolFonts[BolName.DEVANAGERI] 		= Font.createFont( Font.TRUETYPE_FONT, 
					new FileInputStream(Config.pathToDevanageriFont) ).deriveFont(bolFontSizeStd[BolName.DEVANAGERI]);
			bolFontsBold[BolName.DEVANAGERI] 	= Font.createFont( Font.TRUETYPE_FONT, 
					new FileInputStream(Config.pathToDevanageriFont) ).deriveFont(bolFontSizeStd[BolName.DEVANAGERI]).deriveFont(Font.BOLD);
			
			Debug.temporary(Config.class, "Devanageri font loaded: " + bolFontsBold[BolName.DEVANAGERI]);
		} catch (FileNotFoundException e) {
			Debug.critical(Config.class, "Devanageri Font not found in '" + Config.pathToDevanageriFont + "', using default.");
			//e.printStackTrace();
		} catch (FontFormatException e) {
			Debug.critical(Config.class, "Devanageri Font could not load: " + Config.pathToDevanageriFont+ "', using default.");
			//e.printStackTrace();
		} catch (IOException e) {
			Debug.critical(Config.class, "Devanageri Font could not load: " + Config.pathToDevanageriFont+ "', using default.");
			//e.printStackTrace();
		} catch (Exception e) {
			Debug.critical(Config.class, "Devanageri Font could not load: " + Config.pathToDevanageriFont+ "', using default.");
			//e.printStackTrace();
		}
	}

	public static void setAllBolFonts(Font[] fonts) {
		for (int i=0; i < BolName.languagesCount; i++) {
			bolFonts[i] 	= fonts[i];
			bolFontsBold[i] = fonts[i].deriveFont(Font.BOLD);
		}
		GuiConfig.initBolFontsSized();
	}

	/**
	 * Generates maps from languages to size->font maps.
	 */
	static void initBolFontsSized() {
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
	
	static void initColors() {
		JTable table = new JTable();
		colorUnvenRows = table.getBackground();
		Color selBG = table.getSelectionBackground();
		colorEvenRows = new Color (241,245,250);//selBG.brighter().brighter().brighter().brighter();
		tableBG = new Color(217,217,217);
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
	 * Inits the standard font sizes (std, min, max) for each language and 
	 * the default fonts for each language.
	 */
	
	public static boolean existsFontFamily(String name) {
		String [] availableFontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String familyName: availableFontFamilies) {
			if (name.equalsIgnoreCase(familyName)) {
				return true;
			}
		}
		return false;
	}

	static Image windowsFrameIcon;




	public static Image getWindowsFrameIcon() {
		if (windowsFrameIcon==null) {
			URL url = Config.class.getResource("/bolscript-logo-32x32.png");
			if (url == null) {
				Debug.critical(Config.class, "could not load image icon resource");
				windowsFrameIcon =  null;
			} else {
				ImageIcon icon = new ImageIcon(url);
				if (icon == null) {
					Debug.critical(Config.class, "could not instantiate image icon");
					windowsFrameIcon = null;
				} else {
					windowsFrameIcon = icon.getImage();
				}
			}
		}
	
		return windowsFrameIcon;
	}

	
}

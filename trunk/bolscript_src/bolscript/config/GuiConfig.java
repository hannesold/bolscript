package bolscript.config;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JPanel;

import bols.tals.Vibhag;

/**
 * A class for storing the gui configuration
 * @author hannes
 *
 */
public class GuiConfig {

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
	public static Font commaFont= new Font("Arial", Font.BOLD, GuiConfig.commaFontSize);
	public static int commaFontSize = 9;
	
	//CommaPanel
	public static Color commaColor =Color.getHSBColor(0.5f, 0.7f, 0.1f);
	
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
	

	static Dimension limitByScreen(int wantedWidth, int wantedHeight, double maxPercentOfScreenWidth, double maxPercentOfScreenHeight) {
		//get local graphics environment
		GraphicsEnvironment graphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
				
		//get maximum window bounds
		Rectangle maximumWindowBounds=graphicsEnvironment.getMaximumWindowBounds();

		
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
        Dimension screenDim = toolkit.getScreenSize();
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
	
}
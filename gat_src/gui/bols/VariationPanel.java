package gui.bols;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JPanel;

import bols.BolBase;
import bols.BolName;
import bols.Variation;
import bols.tals.LayoutCycle;
import bols.tals.Tal;
import bols.tals.TalBase;
import bolscript.sequences.RepresentableSequence;
import config.Config;
import config.Themes;

/**
 * This class is used in Gat and adds some constructors to SequencePanel
 * @author hannes
 *
 */
public class VariationPanel extends SequencePanel {

	private static final long serialVersionUID = 1420874154544081182L;
	
	
	public VariationPanel(RepresentableSequence sequence, Tal tal,
			Dimension size, int minRows, String fixedLargestWidthBol,
			int fixedMaxSpeed, int language, float fontSize) {
		super(sequence, tal, size, minRows, fixedLargestWidthBol, fixedMaxSpeed,
				language, fontSize);
	}

	public VariationPanel() {
		this(Themes.getTheme01(BolBase.getStandard()), 
				TalBase.standard().getTalFromName("Teental"),
				new Dimension(400,200), 0, "", 0);
	}
	
	public VariationPanel(RepresentableSequence sequence, Tal tal, Dimension size) {
		super(sequence,tal,size,1, "Dhin", 4, bols.BolName.SIMPLE, Config.bolFontSizeStd[BolName.SIMPLE]);
	}
	public VariationPanel(Variation variation, Tal tal, Dimension size) {
		this(variation,tal,size,1, "Dhin", 4);
	}
	
	public VariationPanel(Variation variation, Tal tal, Dimension size, int minRows, String fixedLargestWidthBol, int fixedMaxSpeed) {
		super( variation.getAsRepresentableSequence(), tal, size, minRows, fixedLargestWidthBol, fixedMaxSpeed, BolName.SIMPLE, Config.bolFontSizeStd[BolName.SIMPLE]);
	}
	/**
	 * @deprecated Use {@link #VariationPanel(RepresentableSequence,Tal,Dimension,int,String,int,int, float)} instead
	 */
	public VariationPanel(RepresentableSequence sequence, Tal tal, Dimension size, int minRows, String fixedLargestWidthBol, int fixedMaxSpeed) {
		super(sequence, tal, size, minRows, fixedLargestWidthBol, fixedMaxSpeed,
				BolName.SIMPLE, Config.bolFontSizeStd[BolName.SIMPLE]);
	}

	
	

}


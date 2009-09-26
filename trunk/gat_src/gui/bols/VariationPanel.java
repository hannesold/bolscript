package gui.bols;

import gui.bolscript.packets.SequencePanel;

import java.awt.Dimension;

import bols.BolBase;
import bols.BolName;
import bols.Variation;
import bols.tals.Tal;
import bols.tals.Teental;
import bolscript.config.Config;
import bolscript.sequences.RepresentableSequence;
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
				Teental.getDefaultTeental(),
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


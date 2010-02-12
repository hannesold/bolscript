package gui.bolscript.sequences;

import java.awt.Dimension;
import java.awt.event.MouseListener;

import bols.BolBundle;
import bolscript.sequences.RepresentableSequence;

public class BolBundlePanel extends BolPanelGeneral implements MouseListener{

	private BolBundle bundle; 
	
public BolBundlePanel(BolBundle bundle, Dimension size, boolean isEmphasized, int language, float fontSize) {
		
		this.bundle = bundle;
		
		init(bundle.getBolNameBundle(), size, isEmphasized, language, fontSize);
		
		StringBuilder tip = new StringBuilder("<html>");
		tip.append("Played at " + bundle.getPlayingStyle().getSpeed() + " bols per beat.<br>");
		tip.append("Bundle : ");
		
		tip.append(bundle.getSequence().toString(RepresentableSequence.SHOW_ALL, language));
		tip.append("<br>Speed within this bundle: " + bundle.getSequence().getMaxSpeedByCheckingBols());
		tip.append("</html>");
		this.setToolTipText(tip.toString());
		
		
	}

	public BolBundle getBolBundle() {
		return bundle;
	}
	
}

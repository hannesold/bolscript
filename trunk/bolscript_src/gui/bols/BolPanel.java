package gui.bols;

import java.awt.Dimension;
import java.awt.event.MouseListener;

import midi.MidiStationSimple;
import bols.Bol;
import bols.BolName;

public class BolPanel extends BolPanelGeneral implements MouseListener {
	
	private Bol bol;
	
	/**
	 * @deprecated Use {@link #BolPanel(Bol,Dimension,boolean,int, float)} instead
	 */
	public BolPanel(Bol bol, Dimension size, boolean isEmphasized) {
		this(bol, size, isEmphasized, bols.BolName.SIMPLE, config.Config.bolFontSizeStd[bols.BolName.SIMPLE]);
	}

	public BolPanel(Bol bol, Dimension size, boolean isEmphasized, int language, float fontSize) {
		
		this.bol = bol;
		BolName bn = bol.getBolName();
		
		init(bn, size, isEmphasized, language, fontSize);
		
		label.setForeground(bn.isWellDefinedInBolBase() ? bolFontColor : nonWellDefinedColor);
		String tip = bn.isWellDefinedInBolBase()?bn.toStringForToolTip():"Attention: This Bol is not known to the program.";
		this.setToolTipText("<html>" + tip + "<br>Played at " + bol.getPlayingStyle().getSpeed() + " bols per beat</html>");
		
		if (MidiStationSimple.getStandard() != null) {
			this.addMouseListener(MidiStationSimple.getStandard().getSingleBolClickListener());
		}

		
	}
	
	public Bol getBol() {
		return bol;
	}

	public void setBol(Bol bol) {
		this.bol = bol;
	}


}

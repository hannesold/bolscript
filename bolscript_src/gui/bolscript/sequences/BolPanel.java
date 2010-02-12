package gui.bolscript.sequences;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import midi.MidiStationSimple;
import basics.Debug;
import bols.Bol;
import bols.BolName;
import bolscript.config.GuiConfig;

public class BolPanel extends BolPanelGeneral implements MouseListener {
	
	private Bol bol;
	
	/**
	 * @deprecated Use {@link #BolPanel(Bol,Dimension,boolean,int, float)} instead
	 */
	public BolPanel(Bol bol, Dimension size, boolean isEmphasized) {
		this(bol, size, isEmphasized, bols.BolName.SIMPLE, bolscript.config.GuiConfig.bolFontSizeStd[bols.BolName.SIMPLE]);
	}

	public BolPanel(Bol bol, Dimension size, boolean isEmphasized, int language, float fontSize) {
		
		this.bol = bol;
		BolName bn = bol.getBolName();
		
		init(bn, size, isEmphasized, language, fontSize);
		
		label.setForeground(bn.isWellDefinedInBolBase() ? GuiConfig.bolFontColor : GuiConfig.bolNonWellDefinedColor);
		String tip = bn.isWellDefinedInBolBase()?bn.toStringForToolTip():"Attention: This Bol is not known to the program.";
		this.setToolTipText("<html>" + tip + "<br>Played at " + bol.getPlayingStyle().getSpeed() + " bols per beat</html>");
		
		if (MidiStationSimple.getStandard() != null) {
			this.addMouseListener(MidiStationSimple.getStandard().getSingleBolClickListener());
		}
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				
				onClick(e);
			}
			
		});
		
		
	}
	
	public void onClick(MouseEvent e) {
		Debug.temporary(this, "clicked, textRef: " + bol.getTextReference());
		
	}
	public Bol getBol() {
		return bol;
	}

	public void setBol(Bol bol) {
		this.bol = bol;
	}


}

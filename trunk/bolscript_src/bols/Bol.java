/**
 * 
 */
package bols;

import basics.Rational;
import bolscript.packets.TextReference;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.SpeedUnit;


/**
 * @author Hannes
 *
 */
public class Bol implements Representable, HasPlayingStyle {
	protected BolName bolName;
	protected PlayingStyle style; //speedindex?
	protected boolean emphasized = false;
	protected TextReference textReference;
	
	public Bol (BolName bolName, PlayingStyle style) {
		this.bolName = bolName;
		this.style = style;
	}
	
	public String toString(){
		return bolName.toString();
	}

	public BolName getBolName() {
		return bolName;
	}

	public void setBolName(BolName bolName) {
		this.bolName = bolName;
	}

	public double getSpeed() {
		return style.getSpeedValue();
	}

	public void setSpeed(double d) {
		style.setSpeedValue(d);
	}
	
	public PlayingStyle getPlayingStyle() {
		return style;
	}
	
	public void setPlayingStyle (PlayingStyle style) {
		this.style = style;
	}
	
	public Bol getCopy() {
		return new Bol(bolName, style.getCopy());
	}
	
	public boolean equals(Bol bol) {
		return (bol.getBolName().equals(bolName))&&(bol.getPlayingStyle().equals(style));
	}

	public int getType() {
		return Representable.BOL;
	}
	
	public boolean isEmphasized() {
		return emphasized;
	}

	public void setEmphasized(boolean emphasized) {
		this.emphasized = emphasized;
	}

	@Override
	public TextReference getTextReference() {
		return textReference;
	}

	@Override
	public void setTextReference(TextReference textReference) {
		this.textReference = textReference;
	}
	

	@Override
	public SpeedUnit addFlattenedToSequence(RepresentableSequence seq, SpeedUnit basicSpeedUnit, int currentDepth) {
		seq.add(this);
		return basicSpeedUnit;
		
	}
	
	
}

package bolscript.sequences;

import static bolscript.sequences.Representable.BOL;
import static bolscript.sequences.Representable.REFERENCED_BOL_PACKET;
import static bolscript.sequences.Representable.SEQUENCE;
import basics.Rational;
import bolscript.packets.TextReference;

public abstract class Unit implements Representable {

	protected int type;
	protected Object obj;
	protected TextReference textReference = null;
	

	public Unit(int type, Object obj, TextReference textReference) {
		super();
		this.type = type;
		this.obj = obj;
		this.textReference = textReference;
	}
	
	public Unit() {
		this.type = Representable.UNDEFINED;
		this.obj = "undetermined";
	}

	public TextReference getTextReference() {
		return textReference;
	}

	public void setTextReference(TextReference textReference) {
		this.textReference = textReference;
	}

	public Object getObject() {
		return obj;
	}

	public int getType() {
		return this.type;
	}
	
	public String toString() {
		return obj.toString();
	}
	
	public RepresentableSequence flatten(){
		RepresentableSequence rep = new RepresentableSequence();
		rep.add(this);
		return rep;
	}
	
	public SpeedUnit addFlattenedToSequence(RepresentableSequence seq, SpeedUnit basicSpeedUnit, int currentDepth) {
		seq.add(this);
		return basicSpeedUnit;
	}
}

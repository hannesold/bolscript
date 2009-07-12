package bols;

/**
 * This class maps a BolName to 2 Midimaps: leftHand and RightHand
 * Included are velocity scale factors of double type.
 * Empty hands are realised with Midimaps of handType MidiMap.NONE
 * @author hannes
 *
 */
public class BolMap {
	
	private BolName bolName;
	private MidiMap leftHand;
	private MidiMap rightHand;
	private double leftHandVelocityScale;
	private double rightHandVelocityScale;
	
	
	public BolMap(BolName name, MidiMap hand, MidiMap hand2) {
		super();
		bolName = name;
		leftHand = hand;
		rightHand = hand2;
		leftHandVelocityScale = 1;
		rightHandVelocityScale = 1;
	}
	
	public BolMap(BolName name, MidiMap hand, MidiMap hand2, double scale, double scale2) {
		super();
		bolName = name;
		leftHand = hand;
		rightHand = hand2;
		leftHandVelocityScale = scale;
		rightHandVelocityScale = scale2;
	}

	
	public String toString() {
		String s = "";
		s += "[BolMap: "+bolName.toString()+ " -> left: " + leftHand + " ("+leftHandVelocityScale
		+"), right: " + rightHand + " ("+rightHandVelocityScale+")]";
		return s;
	}
	
	//getters/setters
	public BolName getBolName() {
		return bolName;
	}

	public void setBolName(BolName bolName) {
		this.bolName = bolName;
	}

	public MidiMap getLeftHand() {
		return leftHand;
	}
	
	public MidiMap[] getMidiMaps() {
		MidiMap[] mms = new MidiMap[2];
		mms[0] = leftHand;
		mms[1] = rightHand;
		
		return mms;
	}

	public void setLeftHand(MidiMap leftHand) {
		this.leftHand = leftHand;
	}

	public double getLeftHandVelocityScale() {
		return leftHandVelocityScale;
	}

	public void setLeftHandVelocityScale(double leftHandVelocityScale) {
		//limits?		
		this.leftHandVelocityScale = leftHandVelocityScale;
	}

	public MidiMap getRightHand() {
		return rightHand;
	}

	public void setRightHand(MidiMap rightHand) {
		this.rightHand = rightHand;
	}

	public double getRightHandVelocityScale() {
		//limits?		
		return rightHandVelocityScale;
	}

	public void setRightHandVelocityScale(double rightHandVelocityScale) {
		this.rightHandVelocityScale = rightHandVelocityScale;
	}
	
}

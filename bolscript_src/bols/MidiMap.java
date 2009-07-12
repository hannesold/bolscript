package bols;

/**
 * This Maps a BolName to a MidiNote and an abscract coordinate.
 * The coordinate shall later represent a sound description, but
 * is currently useless.
 * @author hannes
 *
 */
public class MidiMap {
	
	public static int LEFT = 0;
	public static int RIGHT = 1;
	public static int NONE = 2;
	
	private BolName bolName;
	private int coordinate;
	private int midiNote;
	private int hand;
	
	public MidiMap(BolName name, int coordinate, int note, int hand) {
		super();
		// TODO Auto-generated constructor stub
		bolName = name;
		this.coordinate = coordinate;
		midiNote = note;
		
		//check!
		this.hand = hand;
	}

	//getters & setters
	
	public BolName getBolName() {
		return bolName;
	}

	public void setBolName(BolName bolName) {
		this.bolName = bolName;
	}

	public int getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(int coordinate) {
		this.coordinate = coordinate;
	}

	public int getMidiNote() {
		return midiNote;
	}

	public void setMidiNote(int midiNote) {
		this.midiNote = midiNote;
	}

	public int getHand() {
		return hand;
	}

	public void setHand(int hand) {
		this.hand = hand;
	}
	
	
	public String toString() {
		String s = "";
		String sHand = (hand==LEFT)?"left hand":(hand==RIGHT)?"right hand":(hand==NONE)?"no hand":"hand undefined"; 
			
		s += "[midiMap: " + bolName.toString() + " := {" + sHand + " -> midiNote: " + midiNote + "}, (coordinate "+coordinate+")]";
		return s;
	}

	
}

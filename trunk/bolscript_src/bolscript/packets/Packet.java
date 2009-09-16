package bolscript.packets;

import java.util.HashMap;

/**
 * <p>Packet represents one Key/Value packet in a bolscript string. The class  
 * contains the key, the string value, a type identifier (e.g. BOLS, SPEED, EDITOR...)
 * and a link to obj, which is the interpreted version of the string value. Also it is stored if the object is visible.</p>
 * <p>An Example:<br />
 * After parsed by the Reader-class the string "Speed: 2" would be stored as one Packet with Key:"Speed", Value:"2", obj: a Rational with value 2, visible: false</p>
 * 
 * @author hannes
 * @see Reader, Packets
 */
public class Packet {
	public static final int META = 0;
	public static final int BOLS = 1;
	public static final int FOOTNOTE = 2;
	public static final int SPEED = 3;
	public static final int FAILED = 4;
	public static final int LAYOUT = 5;
	public static final int LENGTH = 6;
	public static final int NAME = 7;
	public static final int VIBHAGS = 8;
	public static final int TAL = 9;
	public static final int TYPE = 10;
	public static final int GHARANA = 11;
	public static final int RIGHTS = 12;
	public static final int EDITOR = 13;
	public static final int DESCRIPTION = 14;
	public static final int COMMENT = 15;
	public static final int COMPOSER = 16;
	public static final int SOURCE = 17;
	public static final int nrOfTypes = 18;
	
	public static final int[] METATYPES = new int[]{ META, FOOTNOTE, SPEED, 
		LAYOUT, LENGTH, NAME, VIBHAGS, TAL, TYPE, 
		GHARANA, RIGHTS, EDITOR, DESCRIPTION, COMMENT, COMPOSER, SOURCE};
	
	/**
	 * Maps packet Keys to their types.
	 */
	public static HashMap<String, Integer> keyPacketTypes;
	
	/**
	 * Maps the packet types to their packets default visibility in output.
	 */
	public static boolean [] visibilityMap;
	
	static {
		keyPacketTypes = new HashMap<String, Integer>();
		keyPacketTypes.put("TAL", TAL);
		keyPacketTypes.put("TALA", TAL);
		keyPacketTypes.put("TYPE", TYPE);
		keyPacketTypes.put("TYPES", TYPE);
		keyPacketTypes.put("SPEED", SPEED);
		keyPacketTypes.put("LAYOUT", LAYOUT);
		keyPacketTypes.put("LENGTH", LENGTH);
		keyPacketTypes.put("NAME", NAME);
		keyPacketTypes.put("VIBHAGS", VIBHAGS);
		keyPacketTypes.put("GHARANA", GHARANA);
		keyPacketTypes.put("GHARANAS", GHARANA);
		keyPacketTypes.put("DESCRIPTION", DESCRIPTION);
		keyPacketTypes.put("RIGHTS", RIGHTS);
		keyPacketTypes.put("EDITOR", EDITOR);
		keyPacketTypes.put("EDITORS", EDITOR);
		keyPacketTypes.put("COMMENT", COMMENT);
		keyPacketTypes.put("COMMENTS", COMMENT);
		keyPacketTypes.put("COMPOSER", COMPOSER);
		keyPacketTypes.put("COMPOSERS", COMPOSER);
		keyPacketTypes.put("SOURCE", SOURCE);

		
		visibilityMap = new boolean[nrOfTypes]; //default value is false
		visibilityMap[BOLS] = true;
		visibilityMap[FOOTNOTE] = true;
		visibilityMap[COMMENT] = true;
		
	}
	
	private String key;
	private String value;
	private boolean visible;
	private Object object;
	private int type;
	private PacketType packetType;
	private TextReference textRefPacket;
	private TextReference textRefKey;
	private TextReference textRefValue;
	
	public Packet(String key, String value, int type, boolean visible) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
		this.visible = visible;
		this.object = null;
		this.textRefPacket = null;
		this.textRefKey = null;
		this.textRefValue = null;
	}


	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		if (object != null){
			s.append(key + ":: " + object.toString() + "\n");
		} else s.append(key + ": " + value+ "\n");//+value.split(" ").length;
		if (textRefPacket!=null) {
			s.append(textRefPacket + "\n");
		}
		return s.toString();
		
	}
	public int getType() {
		return type;
	}
	
	public PacketType getPType() {
		return packetType;
	}
	
	public void setPType(PacketType type) {
		this.packetType = type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public Packet replaceValue(String val) {
		Packet p = new Packet(new String(this.key), val, type, visible);
		if (textRefPacket != null) {
			p.setTextReferencePacket(textRefPacket.clone());
		} if (textRefKey != null) p.setTextRefKey(textRefKey.clone());
		if (textRefValue != null) p.setTextRefValue(textRefValue.clone());
		return p;
	}
	
	public TextReference getTextReference() {
		return textRefPacket;
	}
	public boolean hasTextReferences() {
		return (textRefPacket!=null && textRefKey!=null && textRefValue!=null);
	}
	
	public void setTextReferencePacket(TextReference textReference) {
		this.textRefPacket = textReference;
	}
	public void setTextReferencePacket(int startIndex, int endIndex) {
		this.textRefPacket = new TextReference(startIndex,endIndex);
	}


	public TextReference getTextRefKey() {
		return textRefKey;
	}


	public void setTextRefKey(TextReference textRefKey) {
		this.textRefKey = textRefKey;
	}


	public TextReference getTextRefValue() {
		return textRefValue;
	}


	public void setTextRefValue(TextReference textRefValue) {
		this.textRefValue = textRefValue;
	}
	
	
}

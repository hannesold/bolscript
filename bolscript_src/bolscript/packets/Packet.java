package bolscript.packets;

import java.util.HashMap;

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
	public static final int nrOfTypes = 16;
	
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
	private TextReference textReference;
	
	public Packet(String key, String value, int type, boolean visible) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
		this.visible = visible;
		this.object = null;
		this.textReference = null;
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
		if (textReference!=null) {
			s.append(textReference + "\n");
		}
		return s.toString();
		
	}
	public int getType() {
		return type;
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
		return new Packet(new String(this.key), val, type, visible);
	}
	
	public TextReference getTextReference() {
		return textReference;
	}
	public boolean hasTextReference() {
		return (this.textReference!=null);
	}
	
	public void setTextReference(TextReference textReference) {
		this.textReference = textReference;
	}
	public void setTextReference(int startIndex, int endIndex) {
		this.textReference = new TextReference(startIndex,endIndex);
	}
	
}

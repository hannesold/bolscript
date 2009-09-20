package bolscript.packets;

import java.util.HashMap;

import basics.Debug;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;

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
	
	/**
	 * Maps packet Keys to their types.
	 */
	//public static HashMap<String, Integer> keyPacketTypes;
	
	/**
	 * Maps the packet types to their packets default visibility in output.
	 */
	public static boolean [] visibilityMap;
	
	static {
	/*	keyPacketTypes = new HashMap<String, Integer>();
		keyPacketTypes.put("TAL", PacketTypeFactory.TAL);
		keyPacketTypes.put("TALA", PacketTypeFactory.TAL);
		keyPacketTypes.put("TYPE", PacketTypeFactory.TYPE);
		keyPacketTypes.put("TYPES", PacketTypeFactory.TYPE);
		keyPacketTypes.put("SPEED", PacketTypeFactory.SPEED);
		keyPacketTypes.put("LAYOUT", PacketTypeFactory.LAYOUT);
		keyPacketTypes.put("LENGTH", PacketTypeFactory.LENGTH);
		keyPacketTypes.put("NAME", PacketTypeFactory.NAME);
		keyPacketTypes.put("VIBHAGS", PacketTypeFactory.VIBHAGS);
		keyPacketTypes.put("GHARANA", PacketTypeFactory.GHARANA);
		keyPacketTypes.put("GHARANAS", PacketTypeFactory.GHARANA);
		keyPacketTypes.put("DESCRIPTION", PacketTypeFactory.DESCRIPTION);
		keyPacketTypes.put("RIGHTS", PacketTypeFactory.RIGHTS);
		keyPacketTypes.put("EDITOR", PacketTypeFactory.EDITOR);
		keyPacketTypes.put("EDITORS", PacketTypeFactory.EDITOR);
		keyPacketTypes.put("COMMENT", PacketTypeFactory.COMMENT);
		keyPacketTypes.put("COMMENTS", PacketTypeFactory.COMMENT);
		keyPacketTypes.put("COMPOSER", PacketTypeFactory.COMPOSER);
		keyPacketTypes.put("COMPOSERS", PacketTypeFactory.COMPOSER);
		keyPacketTypes.put("SOURCE", PacketTypeFactory.SOURCE);

		*/
		/*
		visibilityMap = new boolean[PacketTypeFactory.nrOfTypes]; //default value is false
		visibilityMap[PacketTypeFactory.BOLS] = true;
		visibilityMap[PacketTypeFactory.FOOTNOTE] = true;
		visibilityMap[PacketTypeFactory.COMMENT] = true;*/
		
	}
	
	private String key;
	private String value;
	private boolean visible;
	private Object object;
	private PacketType packetType;
	private TextReference textRefPacket;
	private TextReference textRefKey;
	private TextReference textRefValue;
	
	public Packet(String key, String value, int type, boolean visible) {
		super();
		this.key = key;
		this.value = value;
		//this.type = type;
		this.visible = visible;
		this.object = null;
		this.textRefPacket = null;
		this.textRefKey = null;
		this.textRefValue = null;
		setType(type);
	}
	
	public Packet(String key, String value, PacketType type, boolean visible) {
		super();
		this.key = key;
		this.value = value;
		this.packetType = type;
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
		s.append("type: " + packetType + "\n");
		if (textRefPacket!=null) {
			s.append(textRefPacket + "\n");
		}
		return s.toString();
		
	}
	public int getType() {
		if (packetType == null) {
			Debug.critical(this, "type is null: " + this.toString());
		}
		return packetType.getId();
	}
	
	public PacketType getPType() {
		return packetType;
	}
	
	public void setPType(PacketType type) {
		this.packetType = type;
	}
	
	public void setType(int type) {
		/*if (PacketTypeFactory.getType(type) == null) {
			PacketTypeFactory.init();
		}*/
		this.packetType = PacketTypeFactory.getType(type);
		if (this.packetType == null) {
			Debug.critical(this, "type "+type+" could not be set!!");
		}
		
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public Packet replaceValue(String val) {
		Packet p = new Packet(new String(this.key), val, packetType, visible);
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
	public void setTextReferencePacket(int startIndex, int endIndex, int line) {
		this.textRefPacket = new TextReference(startIndex,endIndex, line);
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

	/**
	 * Returns an independent clone of the packet,
	 * however the obj is set to null. 
	 */
	public Packet cloneClearObjAndTextRef() {
		return new Packet(new String(this.key), new String(this.value), this.packetType.getId(), this.visible);
	}
	
	
}

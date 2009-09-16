package bolscript.packets.types;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import basics.Debug;
import bolscript.packets.types.PacketType.Kardinality;
import bolscript.packets.types.PacketType.ParseMode;

public class PacketTypeFactory {
	public static final int META = 0;		//none
	public static final int BOLS = 1;		//other,other
	public static final int FOOTNOTE = 2;	//multiple, string
	public static final int SPEED = 3;		//multiple, other
	public static final int FAILED = 4;		//multipl
	public static final int LAYOUT = 5;		//single, other
	public static final int LENGTH = 6;		//single, other
	public static final int NAME = 7;		//single, string
	public static final int VIBHAGS = 8;	//single, other
	public static final int TAL = 9;		//multiple, string
	public static final int TYPE = 10;		//multiple, commaSeperated
	public static final int GHARANA = 11;	//multiple, commaSeperated
	public static final int RIGHTS = 12;	//multiple, commaSeperated
	public static final int EDITOR = 13;	//multiple, commaSeperated
	public static final int DESCRIPTION = 14;	//single
	public static final int COMMENT = 15;
	public static final int COMPOSER = 16;
	public static final int SOURCE = 17;
	public static final int nrOfTypes = 18;

	private static PacketType[] types=null;

	private static Color metaKeyColor = new Color(20,200,20);
	private static Color bolKeyColor = Color.BLACK;
	private static Color failedKeyColor = bolKeyColor;

	private static HashMap<String, PacketType> keyTypeMap = null;
	private static PacketType[] metaTypes;
	
	private static boolean initialized = false;

	static {
		Debug.debug(PacketTypeFactory.class, "init");
		init();
	}

	/**
	 * Depends on no other class to be initialized.
	 */
	public static void init() {
		if (!initialized) {

			types = new PacketType[PacketTypeFactory.nrOfTypes];
			types[META] = new PacketTypeStandard(PacketTypeFactory.META,"Meta", "Meta",new String[]{},false,0,Kardinality.none,ParseMode.none,false,true,false,metaKeyColor);
			types[BOLS] = new PacketTypeStandard(BOLS, "Bol", "Bols", new String[]{},false,0,Kardinality.other,ParseMode.other,true,false,true,bolKeyColor);
			types[FOOTNOTE] = new PacketTypeStandard(FOOTNOTE,"Footnote", "Footnotes", new String[]{}, false,0,Kardinality.multiple,ParseMode.string,true,true,true,metaKeyColor);
			types[SPEED] = new PacketTypeStandard(SPEED, "Speed", "Speeds", new String[]{"SPEED"},true,0,Kardinality.multiple,ParseMode.other,false,true,true,metaKeyColor);
			types[FAILED] = new PacketTypeStandard(FAILED,"Failed","Failed",new String[]{},false,0,Kardinality.none,ParseMode.none,false,true,false,failedKeyColor);
			types[LAYOUT] = new PacketTypeStandard(LAYOUT,"Layout","Layouts", new String[]{"LAYOUT"},false,0,Kardinality.unique, ParseMode.other,false,true,false,metaKeyColor);
			types[LENGTH] = new PacketTypeStandard(LENGTH, "Length", "Lengths", new String[]{"LENGTH"},false,0,Kardinality.unique,ParseMode.other,false,true,false,metaKeyColor);
			types[NAME] = new PacketTypeStandard(NAME, "Name", "Names", new String[]{"NAME"},true,0,Kardinality.unique,ParseMode.string,false,true,true,metaKeyColor);
			types[VIBHAGS] = new PacketTypeStandard(VIBHAGS, "Vibhag", "Vibhags", new String[]{"VIBHAGS"},false,0,Kardinality.unique,ParseMode.other,false,false,false,metaKeyColor);
			types[TAL] = new PacketTypeStandard(TAL,"Tal","Tals",new String[]{"TAL", "TALS", "TALA"},true, 0, Kardinality.multiple,ParseMode.other,false,true,true,metaKeyColor);
			types[TYPE] = new PacketTypeStandard(TYPE,"Type","Types", new String[]{"TYPE","TYPES"},true,0,Kardinality.multiple,ParseMode.commaSeperated,false,true,true,metaKeyColor);
			types[GHARANA] = new PacketTypeStandard(GHARANA,"Gharana", "Gharanas", new String[]{"GHARANA","GHARANAS"},true,0,Kardinality.multiple,ParseMode.commaSeperated,false,false,true,metaKeyColor);
			types[RIGHTS] = new PacketTypeStandard(RIGHTS, "Right", "Rights", new String[]{},false,0,Kardinality.none,ParseMode.none,false,true,false,metaKeyColor);
			types[EDITOR] = new PacketTypeStandard(EDITOR, "Editor", "Editors", new String[]{"EDITOR","EDITORS"},true,0,Kardinality.multiple,ParseMode.commaSeperated,false,true,true,metaKeyColor);
			types[DESCRIPTION] = new PacketTypeStandard(DESCRIPTION, "Description", "Descriptions", new String[]{"DESCRIPTION"},true,0,Kardinality.unique,ParseMode.string,false,true,true,metaKeyColor);
			types[COMMENT] = new PacketTypeStandard(COMMENT, "Comment", "Comments", new String[]{"COMMENT","COMMENTS"}, true, 0, Kardinality.multiple, ParseMode.string, true, true, true, metaKeyColor);
			types[COMPOSER] = new PacketTypeStandard(COMPOSER, "Composer", "Composers", new String[]{"COMPOSER","COMPOSERS"}, true, 0, Kardinality.multiple, ParseMode.commaSeperated, false, true, true, metaKeyColor);
			types[SOURCE] = new PacketTypeStandard(SOURCE, "Source", "Sources", new String[]{"SOURCE","SOURCES"}, true, 0, Kardinality.multiple, ParseMode.string, false, true, true, metaKeyColor);

			keyTypeMap = new HashMap<String, PacketType>();
			for (int i=0; i < nrOfTypes; i++) {
				if (types[i].getParseMode() != ParseMode.none) {
					for (int j=0; j < types[i].getKeys().length; j++) {
						keyTypeMap.put(types[i].getKeys()[j], types[i]);
					}
				}
			}
			
			
			ArrayList<PacketType> metaTypeList = new ArrayList<PacketType>();
			for (int i =0; i < nrOfTypes; i++) {
				if (types[i].isMetaPaket()) { 
					metaTypeList.add(types[i]);
				}
			}
			metaTypes = new PacketType[metaTypeList.size()];
			metaTypes = metaTypeList.toArray(metaTypes);
		
			initialized = true;
		} //if !initialized
	}

	public static PacketType getType(int type) {
		if (types[type] == null) {
			Debug.critical(PacketTypeFactory.class, "type not found: " + type );
		}
		return types[type];
	}

	public static PacketType getType(String key) {
		PacketType type = keyTypeMap.get(key);
		if (type != null)
			return type;
		else {
			Debug.temporary(PacketTypeFactory.class, "type not found: " + key + ", using " + types[BOLS]);
			return types[BOLS];
		}
		
	}
	
	public static PacketType [] getMetaTypes() {
		return metaTypes;
	}


}

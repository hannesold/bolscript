package bolscript.packets.types;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import basics.Debug;
import bolscript.packets.types.PacketType.StorageType;
import bolscript.packets.types.PacketType.ParseMode;

public class PacketTypeDefinitions {
	public static final int KEYS = 0;
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
	public static final int SNIPPET = 12;
	public static final int EDITOR = 13;	//multiple, commaSeperated
	public static final int COMMENT = 14;
	public static final int COMPOSER = 15;
	public static final int SOURCE = 16;
	public static final int HISTORY = 17;
	public static final int CREATED = 18;
	public static final int LAST_MODIFIED = 19;
	
	public static final int nrOfTypes = 20;

	private static PacketType[] types=null;

	private static Color metaKeyColor = new Color(20,200,20);
	private static Color bolKeyColor = Color.BLACK;
	private static Color failedKeyColor = bolKeyColor;

	private static HashMap<String, PacketType> keyTypeMap = null;
	private static PacketType[] metaTypes;
	private static PacketType[] columnTypes;
	
	private static boolean initialized = false;

	static {
		Debug.debug(PacketTypeDefinitions.class, "init");
		init();
	}

	/**
	 * Depends on no other class to be initialized.
	 */
	public static void init() {
		if (!initialized) {

			types 			= new PacketType[PacketTypeDefinitions.nrOfTypes];
			types[BOLS] 	= new PacketTypeStandard(BOLS, "Bol", "Bols", 				new String[]{},false,0,							StorageType.OTHER,		ParseMode.OTHER,true,true,false,true, bolKeyColor);
			types[FOOTNOTE] = new PacketTypeStandard(FOOTNOTE,"Footnote", "Footnotes", 	new String[]{}, false,0,						StorageType.STRINGLIST,	ParseMode.NONE,true,true,true,true, metaKeyColor);
			types[SPEED] 	= new PacketTypeStandard(SPEED, "Speed", "Speeds",			new String[]{"SPEED"},false,-60,				StorageType.STRINGLIST,	ParseMode.OTHER,true,false,true,true, metaKeyColor);
			types[FAILED] 	= new PacketTypeStandard(FAILED,"Failed","Failed",			new String[]{},false,0,							StorageType.NONE,		ParseMode.NONE,true,false,true,false, failedKeyColor);
			types[LAYOUT] 	= new PacketTypeStandard(LAYOUT,"Layout","Layouts",			new String[]{"LAYOUT"},false,0,					StorageType.STRING, 	ParseMode.OTHER,true,false,true,false, metaKeyColor);
			types[LENGTH] 	= new PacketTypeStandard(LENGTH, "Length", "Lengths", 		new String[]{"LENGTH"},false,0,					StorageType.STRING,		ParseMode.OTHER,true,false,true,false, metaKeyColor);
			types[NAME] 	= new PacketTypeStandard(NAME, "Name", "Names", 			new String[]{"NAME"},true,-100,					StorageType.STRING,		ParseMode.STRING,true,false,true,true, metaKeyColor);
			types[VIBHAGS] 	= new PacketTypeStandard(VIBHAGS, "Vibhag", "Vibhags", 		new String[]{"VIBHAGS"},false,0,				StorageType.STRING,		ParseMode.OTHER,true,false,false,false, metaKeyColor);
			types[TAL] 		= new PacketTypeStandard(TAL,"Tal","Tals",					new String[]{"TAL", "TALS", "TALA"},true, -90, 	StorageType.STRINGLIST, ParseMode.STRING,true,false,true,true, metaKeyColor);
			types[TYPE]		= new PacketTypeStandard(TYPE,"Type","Types", 				new String[]{"TYPE","TYPES"},true,-80,			StorageType.STRINGLIST,	ParseMode.COMMASEPERATED,true,false,true,true, metaKeyColor);
			types[GHARANA] 	= new PacketTypeStandard(GHARANA,"Gharana", "Gharanas", 	new String[]{"GHARANA","GHARANAS"},false,	0,	StorageType.STRINGLIST,	ParseMode.COMMASEPERATED,true,false,true,true, metaKeyColor);
			types[EDITOR] 	= new PacketTypeStandard(EDITOR, "Editor", "Editors", 		new String[]{"EDITOR","EDITORS"},false,0,		StorageType.STRINGLIST,	ParseMode.COMMASEPERATED,true,false,true,true, metaKeyColor);
			types[COMMENT] 	= new PacketTypeStandard(COMMENT, "Comment", "Comments", 	new String[]{"COMMENT","COMMENTS"}, false, 0, 	StorageType.STRINGLIST, ParseMode.STRING, true, true, true, true, metaKeyColor);
			types[COMPOSER]	= new PacketTypeStandard(COMPOSER, "Composer", "Composers", new String[]{"COMPOSER","COMPOSERS"}, false, 0, StorageType.STRINGLIST, ParseMode.COMMASEPERATED, true, false, true, true, metaKeyColor);
			types[SOURCE]	= new PacketTypeStandard(SOURCE, "Source", "Sources", 		new String[]{"SOURCE","SOURCES"}, true, -50, 	StorageType.STRINGLIST, ParseMode.STRING, true, false, true, true, metaKeyColor);
			types[KEYS]		= new PacketTypeStandard(KEYS,"Key","Keys",					new String[]{}, false,0,						StorageType.STRINGLIST,	ParseMode.NONE,true,false,true,true, metaKeyColor);
			types[SNIPPET]	= new PacketTypeStandard(SNIPPET, "Snippet", "Snippets", 	new String[]{"Snippet","Snippets"},true,-40,	StorageType.STRING,		ParseMode.STRING,true,false,true,true, metaKeyColor);
			types[HISTORY]  = new PacketTypeStandard(HISTORY, "History", "History", 	new String[]{"HISTORY"},false,0, 				StorageType.NONE,		ParseMode.OTHER,false,false,true,true, metaKeyColor);
			types[CREATED]  = new PacketTypeStandard(CREATED, "Created", "Created", 	new String[]{},true,0, 							StorageType.STRING,		ParseMode.OTHER,true,false,true,true, metaKeyColor);
			types[LAST_MODIFIED]  = new PacketTypeStandard(LAST_MODIFIED, "Modified", "Modified", 	new String[]{},true,0, 				StorageType.STRING,		ParseMode.OTHER,true,false,true,true, metaKeyColor);
			
			keyTypeMap = new HashMap<String, PacketType>();
			for (int i=0; i < nrOfTypes; i++) {
				if (types[i].getParseMode() != ParseMode.NONE) {
					for (int j=0; j < types[i].getKeys().length; j++) {
						keyTypeMap.put(types[i].getKeys()[j], types[i]);
					}
				}
			}
			
			
			ArrayList<PacketType> metaTypeList = new ArrayList<PacketType>();
			ArrayList<PacketType> columnTypeList = new ArrayList<PacketType>();
			for (int i =0; i < nrOfTypes; i++) {
				if (types[i].isMetaPaket()) { 
					metaTypeList.add(types[i]);
				}
				if (types[i].displayInTable()) {
					columnTypeList.add(types[i]);
				}
			}
			metaTypes = new PacketType[metaTypeList.size()];
			metaTypes = metaTypeList.toArray(metaTypes);
			columnTypes = new PacketType[columnTypeList.size()];
			columnTypes = columnTypeList.toArray(columnTypes);
		
			initialized = true;
		} //if !initialized
	}

	public static PacketType getType(int type) {
		if (types[type] == null) {
			Debug.critical(PacketTypeDefinitions.class, "type not found: " + type );
		}
		return types[type];
	}

	public static PacketType getType(String key) {
		PacketType type = keyTypeMap.get(key);
		if (type != null)
			return type;
		else {
			//Debug.debug(PacketTypeFactory.class, "type not found: " + key + ", using " + types[BOLS]);
			return types[BOLS];
		}
		
	}
	
	/**
	 * Returns all PacketTypes that are to find in the metaValues of each composition.
	 * Especially this does NOT include BOLS
	 * @return
	 */
	public static PacketType [] getMetaTypes() {
		return metaTypes;
	}
	
	/**
	 * Returns all PacketTypes that are to be shown in the composition browser table.
	 */
	public static PacketType [] getColumnTypes() {
		return columnTypes;
	}


}

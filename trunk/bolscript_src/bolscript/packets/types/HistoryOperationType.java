package bolscript.packets.types;

import java.util.HashMap;

import bolscript.packets.types.PacketType.ParseMode;

public class HistoryOperationType {
	
	private int id;
	private String name;
	private String description;	
	
	public final static int CREATED = 0;
	public final static int VIEWED = 1;
	public final static int MODIFIED = 2;
	public final static int nrOfTypes = 3;
	
	private static String regExp = null;
	
	private static HistoryOperationType[] types;
	private static HashMap<String, HistoryOperationType> keyTypeMap;
	static {
		init();
	}
	
	public HistoryOperationType(int id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public static void init() {
		types 			= new HistoryOperationType[nrOfTypes];
		types[CREATED] 	= new HistoryOperationType(CREATED, 	"created","The File was created.");
		types[VIEWED] 	= new HistoryOperationType(VIEWED, 		"viewed","The File was viewed but not modified.");
		types[MODIFIED] = new HistoryOperationType(MODIFIED, 	"modified","The File was modified.");
		
		keyTypeMap = new HashMap<String, HistoryOperationType>();
		for (int i=0; i < nrOfTypes; i++) {
			keyTypeMap.put(types[i].getParsableVersion(), types[i]);
		}
		
		regExp = "";
		for (int i = 0; i < nrOfTypes; i++) {
			if (i!=0) regExp += "|";
			regExp += types[i].getParsableVersion();			
		}
		
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getParsableVersion() {
		return name;
	}
	
	public static HistoryOperationType getType(String type) {
		return keyTypeMap.get(type);
	}

	/**
	 * returns a regexp of the form OPERATION1|OPERATION2|OPERATION3where each entry is the parsableVersion of a operation type.
	 */
	public static String getRegexp() {
		return regExp;
	}
	public String getDescription() {
		return description;
	}

	public static HistoryOperationType getHistoryOperationType(int id) {
		return types[id];		
	}
		
	public String toString() {
		return id + " " + name + " : " + description;
	}
	
}

package bolscript.packets.types;

import java.awt.Color;

import bolscript.packets.types.PacketType.StorageType;
import bolscript.packets.types.PacketType.ParseMode;

public class PacketTypeStandard implements PacketType {

	protected int id;
	protected String displayNameSingular;
	protected String displayNamePlural;	
	protected String displayNameTable;
	
	protected String[] keys;
	protected boolean displayInTable;
	protected int tableWeight;
	protected StorageType storageType;
	protected ParseMode parseMode;
	protected boolean displayInCompositionView;
	protected boolean metaPaket;
	protected boolean searchable;
	protected Color keyColor;

	
	public PacketTypeStandard(int id, String displayNameSingular,
			String displayNamePlural, String[] keys, boolean displayInTable,
			int tableWeight, StorageType storageType, ParseMode parseMode,
			boolean displayInCompositionView, boolean metaPaket,
			boolean searchable, Color keyColor) {
		super();
		this.id = id;
		this.displayNameSingular = displayNameSingular;
		this.displayNamePlural = displayNamePlural;
		this.keys = keys;
		this.displayInTable = displayInTable;
		this.tableWeight = tableWeight;
		this.storageType = storageType;
		this.parseMode = parseMode;
		this.displayInCompositionView = displayInCompositionView;
		this.metaPaket = metaPaket;
		this.searchable = searchable;
		this.keyColor = keyColor;
		
		switch (storageType) {
			case STRINGLIST:
				this.displayNameTable = displayNamePlural;
			break;
			case STRING:
				this.displayNameTable = displayNameSingular;
				break;
			default:
				this.displayNameTable = "";
		} 
	}

	public int getId() {
		return id;
	}
	
	public String toString () {
		return displayNameSingular;
	}

	public String getDisplayNameSingular() {
		return displayNameSingular;
	}

	public String getDisplayNamePlural() {
		return displayNamePlural;
	}

	public String[] getKeys() {
		return keys;
	}

	public int getTableWeight() {
		return tableWeight;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public ParseMode getParseMode() {
		return parseMode;
	}

	public boolean displayInTable() {
		return displayInTable;
	}

	public boolean displayInCompositionView() {
		return displayInCompositionView;
	}

	public boolean isMetaPaket() {
		return metaPaket;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public Color getKeyColor() {
		return keyColor;
	}


	/**
	 * Compares two packet types by comparing their weight.
	 */
	public int compareTo(PacketType otherType) {
		return getTableWeight() - otherType.getTableWeight();
	}

	public String getDisplayNameTable() {
		return displayNameTable;
	}

}

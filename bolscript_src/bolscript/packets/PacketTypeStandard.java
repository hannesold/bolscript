package bolscript.packets;

import java.awt.Color;

import bolscript.packets.PacketType.Kardinality;
import bolscript.packets.PacketType.ParseMode;

public abstract class PacketTypeStandard implements PacketType {

	protected int id;
	protected String displayNameSingular;
	protected String displayNamePlural;
	protected String[] keys;
	protected boolean displayInTable;
	protected int tableWeight;
	protected Kardinality kardinality;
	protected ParseMode parseMode;
	protected boolean displayInCompositionView;
	protected boolean metaPaket;
	protected boolean searchable;
	protected Color keyColor;

	public int getId() {
		return id;
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

	public Kardinality getKardinality() {
		return kardinality;
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


	

}

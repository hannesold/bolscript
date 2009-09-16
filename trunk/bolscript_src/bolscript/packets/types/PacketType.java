package bolscript.packets.types;

import java.awt.Color;

public interface PacketType {
	
	public enum Kardinality {
		/**
		 * This Packet is not stored in compositions
		 */
		none,
		/** 
		 * This Packet is stored only once per composition
		 */
		unique, 
		
		/**
		 * This Packet is stored multiple times per composition (in an array)
		 */
		multiple,
		
		/**
		 * This Packet is specially treated
		 */
		other
	}
	
	public enum ParseMode{
		/**
		 * This packet is subject to parsing
		 */
		none,
		/**
		 * A Packets Value is parsed as one string
		 */
		string,
		/**
		 * A Packets Value is parsed as a comma seperated list
		 */
		commaSeperated,
		
		/**
		 * This Packet cannot be parsed in a trivial way
		 */
		other
	}
	
	/**
	 * An identifier to make switch statements easy.
	 * This id is supposed to be unique.
	 * @return
	 */
	int getId();
	
	/**
	 * The Keys which are associated with this PacketType
	 */
	String[] getKeys();
	
	/**
	 * Returns the parse mode.
	 */
	ParseMode getParseMode();
	
	/**
	 * Returns the kardinality, that is, if it is supposed to have a single or list of values.
	 */
	Kardinality getKardinality();
	
	/**
	 * Shall this Type be Part of the display in the composition browsers table.
	 */
	boolean displayInTable();
	
	/**
	 * Returns a weight. Columns are sorted by this in the table view.
	 */
	int getTableWeight();
	
	/**
	 * The display name in singular form. This is for use in table header.
	 */
	String getDisplayNameSingular();
	
	
	/**
	 * The display name in plural form. This is for use in table header.
	 */
	String getDisplayNamePlural();
	
	/**
	 * Is this a Packet which is parsed as a meta packet?
	 */
	boolean isMetaPaket();
	
	/**
	 * Shall this PacketType be included in the search-scope?
	 */
	boolean isSearchable();
	
	/**
	 * Shall this Type be Part of the display in the composition viewer.
	 * @return
	 */
	boolean displayInCompositionView();
	
	/**
	 * The color of the key in the composition editor.
	 */
	Color getKeyColor();
	
}

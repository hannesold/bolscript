package bolscript.packets.types;

import java.awt.Color;

public interface PacketType extends Comparable<PacketType> {
	
	public enum StorageType {
		/**
		 * This Packet is not stored in compositions
		 */
		NONE,
		/** 
		 * This Packet is stored only once per composition
		 */
		STRING, 
		
		/**
		 * This Packet is stored multiple times per composition (in an array)
		 */
		STRINGLIST,
		
		/**
		 * This Packet is specially treated
		 */
		OTHER
	}
	
	public enum ParseMode{
		/**
		 * This packet is NOT subject to parsing
		 */
		NONE,
		/**
		 * A Packets Value is parsed as one string
		 */
		STRING,
		/**
		 * A Packets Value is parsed as a comma seperated list
		 */
		COMMASEPERATED,
		
		/**
		 * This Packet cannot be parsed in a trivial way
		 */
		OTHER
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
	StorageType getStorageType();
	
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
	 * The display name for a table header
	 */
	String getDisplayNameTable();
	
	/**
	 *  The display name for a filter header
	 */
	String getDisplayNameFilter();
	
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

	/**
	 * <ul>
	 * <li>true: Packets of this type are shown in the text-based editor</li>
	 * <li>false: Packets of this type are not shown in the editor</li>
	 * </ul>
	 */
	boolean displayForEditingInTextEditor();
	
	
}

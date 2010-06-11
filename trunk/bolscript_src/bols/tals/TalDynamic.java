package bols.tals;

import java.io.File;

import basics.FileReadException;
import bolscript.compositions.Composition;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.sequences.RepresentableSequence;

public class TalDynamic extends Composition implements Tal {

	/**
	 * This constructs a TalDynamic from the rawData.
	 * RawData has to be given in bolscript format.
	 * @param rawData A String in bolscript format.
	 */
	public TalDynamic(String rawData, TalBase talBase) {
		super(rawData, talBase);
	}
	
	/**
	 * Inits a Tal from a bolscript file.
	 * Uses reader to get the rawData from the file.
	 * Sets the linklocal to the files absolute path
	 * and the datastate to CONNECTED.
	 * Makes a first backup of rawData.
	 * 
	 * @param file
	 * @throws FileReadException
	 */
	public TalDynamic(File file, TalBase talBase) throws FileReadException {
		super(file, talBase);
	}
	
	public int getLength() {
		return talInfo.getLength();
	}
	public LayoutChooser getLayoutChooser() {
		return talInfo.getLayoutChooser();
	}
	
	public RepresentableSequence getTheka() {
		return talInfo.getTheka();
	}

	public Vibhag[] getVibhags() {
		return talInfo.getVibhags();
	}

	public String getVibhagsAsString() {
		return talInfo.getVibhagsAsString();
	}	
	
	public String toString() {
		return metaValues.getString(PacketTypeDefinitions.NAME) + ", " + talInfo.getLength() + " beats";
	}
	

}

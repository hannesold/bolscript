package bols.tals;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.FileReadException;
import bols.BolBaseGeneral;
import bolscript.Reader;
import bolscript.compositions.Composition;
import bolscript.compositions.State;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.sequences.RepresentableSequence;

public class TalDynamic extends Composition implements Tal {

	/**
	 * This constructs a TalDynamic from the rawData.
	 * RawData has to be given in bolscript format.
	 * @param rawData A String in bolscript format.
	 */
	public TalDynamic(String rawData) {
		super(rawData);
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
	public TalDynamic(File file) throws FileReadException {
		super(file);
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
		return metaValues.getString(PacketTypeFactory.NAME) + ", " + talInfo.getLength() + " beats";
	}
	

}

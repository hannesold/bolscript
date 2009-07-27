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
import bolscript.sequences.RepresentableSequence;

public class TalDynamic extends Composition implements Tal {

	private int length;
	private LayoutChooser layoutChooser;
	private RepresentableSequence theka;
	
	private ArrayList<RepresentableSequence> variations;
	private Vibhag[] vibhags;
	
	BolBaseGeneral bolBase = null;
	

	/**
	 * This constructs a TalDynamix from the rawData.
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
	
	
	/**
	 * Constructs a Tal from a Composition object,
	 * using its rawData for initialisation, and copying the additional
	 * properties link local and datastate.
	 * @param comp
	 */
	public TalDynamic(Composition comp) {
		this(comp.getRawData());
		this.setLinkLocal(comp.getLinkLocal());
		this.setDataState(comp.getDataState());
	}

	/**
	 * Generates a new TalDynamic from empty packets,
	 * all entries have to be set manually for this tal to be usefull.
	 * Most efficiently using initFromPackets(Packets).
	 * @param bolBase
	 * @return
	 */
	public static TalDynamic emptyTal() {
		return new TalDynamic("");
	}

	
	/**
	 * Calls the super method for extracting the composition infos. Then 
	 * retrieves the tal-specific information from the packets,
	 * that is not yet extracted by Composition:
	 * length, theka, vibhags, layout
	 */
	@Override
	protected void extractInfoFromPackets(Packets packets) {
		super.extractInfoFromPackets(packets);
		
		Packet layoutPacket = null;

		//gather aditional infos needed for tal
		for (int i=0; i<packets.size(); i++) {
			Packet p = packets.get(i);
			if (p.getType() == Packet.LENGTH)  {
				String s = p.getValue().replaceAll(Reader.SN, "");
				int length = Integer.parseInt(s);
				this.setLength(length);
			} else if  ((p.getType() == Packet.BOLS) && 
					(p.getKey().equalsIgnoreCase("Theka"))) {
				//get the Theka
				this.setTheka((RepresentableSequence) p.getObject());
			} else if (p.getType() == Packet.LAYOUT) {
				// do this after length!! So we postpone it and do it after the for loop
				layoutPacket = p;
			} else if (p.getType() == Packet.VIBHAGS) {
				Matcher m = Pattern.compile(Reader.SN +"*(\\d+)\\s*([kK]|Kali|Khali|kali|khali)?").matcher(p.getValue());

				int k = 0;
				int position = 0;
				int length = 0;
				ArrayList<Vibhag> vibhags = new ArrayList<Vibhag>();

				while (m.find()) {
					length = Integer.parseInt(m.group(1));

					if (m.group(2) != null) {
						//is khali
						vibhags.add(new Vibhag(position, length, Vibhag.KALI));
					} else if (k==0) {
						//is sam
						vibhags.add(new Vibhag(position, length, Vibhag.SAM));
					} else {
						//is tali
						vibhags.add(new Vibhag(position, length, Vibhag.TALI));
					}
					position += length;		
					k++;
				}

				Vibhag[] vibs = new Vibhag[vibhags.size()];
				vibhags.toArray(vibs);

				if (vibs.length >0) {
					this.setVibhags(vibs);
				} else p.setType(Packet.FAILED);
			}

		}
		if (this.getLength() == 0) {
			//try to set it by checking out the theka or the vibhags?
			if (theka != null) {
				length = (int) Math.ceil(theka.getDuration());
			}
		}
		if ((layoutPacket != null)&&(this.getLength()!=0)) {
			String s = layoutPacket.getValue();
			LayoutChooser lc = LayoutChooser.fromString(s, this.getLength());
			this.setLayoutChooser(lc);	
		}
	}
	
	/*

	*/
	/*public TalDynamic(String name, int length, LayoutChooser layoutChooser,
			RepresentableSequence theka,
			ArrayList<RepresentableSequence> variations, Vibhag[] vibhags, BolBaseGeneral bolBase) {
		super();
		this.name = name;
		this.length = length;
		this.layoutChooser = layoutChooser;
		this.theka = theka;
		this.variations = variations;
		this.vibhags = vibhags;
		this.bolBase = bolBase;
	}*/
	
	/*public TalDynamic(String filename, BolBaseGeneral bolBase) throws FileReadException{
		this.bolBase = bolBase;
		Packets packets = Reader.compilePacketsFromFile(filename, bolBase);
		initFromPackets(packets);
	}
	
	public TalDynamic(Packets packets, BolBaseGeneral bolBase){
		this.bolBase = bolBase;
		this.DEBUG = false;
		initFromPackets(packets);
		
	}
	*/
	
	public void initFromPackets(Packets packets) {
		Packet layoutPacket = null;

		for (int i=0; i<packets.size(); i++) {
			Packet p = packets.get(i);
			if (p.getType() == Packet.NAME) {
				String s = p.getValue().replaceAll(Reader.SNatBeginningOrEnd, "");
				this.setName(s);
			} else if (p.getType() == Packet.LENGTH)  {
				String s = p.getValue().replaceAll(Reader.SN, "");
				int length = Integer.parseInt(s);
				this.setLength(length);
			} else if  ((p.getType() == Packet.BOLS) && 
					(p.getKey().equalsIgnoreCase("Theka"))) {
				//get the Theka
				this.setTheka((RepresentableSequence) p.getObject());
			} else if (p.getType() == Packet.LAYOUT) {
				// do this after length!! So we postpone it and do it after the for loop
				layoutPacket = p;
			} else if (p.getType() == Packet.VIBHAGS) {
				Matcher m = Pattern.compile(Reader.SN +"*(\\d+)\\s*([kK]|Kali|Khali|kali|khali)?").matcher(p.getValue());

				int k = 0;
				int position = 0;
				int length = 0;
				ArrayList<Vibhag> vibhags = new ArrayList<Vibhag>();

				while (m.find()) {
					length = Integer.parseInt(m.group(1));

					if (m.group(2) != null) {
						//is khali
						vibhags.add(new Vibhag(position, length, Vibhag.KALI));
					} else if (k==0) {
						//is sam
						vibhags.add(new Vibhag(position, length, Vibhag.SAM));
					} else {
						//is tali
						vibhags.add(new Vibhag(position, length, Vibhag.TALI));
					}
					position += length;		
					k++;
				}

				Vibhag[] vibs = new Vibhag[vibhags.size()];
				vibhags.toArray(vibs);

				this.setVibhags(vibs);
			}

		}
		if (this.getLength() == 0) {
			//try to set it by checking out the theka or the vibhags?
			if (theka != null) {
				length = (int) Math.ceil(theka.getDuration());
			}
		}
		if ((layoutPacket != null)&&(this.getLength()!=0)) {
			String s = layoutPacket.getValue();
			LayoutChooser lc = LayoutChooser.fromString(s, this.getLength());
			this.setLayoutChooser(lc);	
		}
	}
	
	
	
	
	
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public LayoutChooser getLayoutChooser() {
		return layoutChooser;
	}
	public void setLayoutChooser(LayoutChooser layoutChooser) {
		this.layoutChooser = layoutChooser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RepresentableSequence getTheka() {
		return theka;
	}
	public void setTheka(RepresentableSequence theka) {
		this.theka = theka;
	}
	public ArrayList<RepresentableSequence> getVariations() {
		return variations;
	}
	public void setVariations(ArrayList<RepresentableSequence> variations) {
		this.variations = variations;
	}
	public Vibhag[] getVibhags() {
		return vibhags;
	}
	public void setVibhags(Vibhag[] vibhags) {
		this.vibhags = vibhags;
	}
	public String getVibhagsAsString() {
		String s = "";
		if (vibhags != null){

			for (int i= 0; i < vibhags.length; i++) {
				s = s + vibhags[i] + "\n";
			}
			return s;
		}else {
			return "No Vibhags defined";
		}
		
	}	
	public String toString() {
		return name + ", " + length + " beats";
	}
	

}

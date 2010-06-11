package bolscript.compositions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bols.tals.LayoutChooser;
import bols.tals.Tal;
import bols.tals.Vibhag;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.scanner.Parser;
import bolscript.sequences.RepresentableSequence;

public class TalInfo implements Tal {

	private int length;
	private LayoutChooser layoutChooser;
	private RepresentableSequence theka;
	
	//private ArrayList<RepresentableSequence> variations;
	private Vibhag[] vibhags;
	
	private Composition owner;
	
	
	public TalInfo(Composition owner) {
		this.owner = owner;
	}
	
	protected boolean extractTalInfoFromPackets(Packets packets) {		
		length = 0;
		layoutChooser = null;
		vibhags = null;
		
		Packet layoutPacket = null;
		
		//gather aditional infos needed for tal
		for (int i=0; i<packets.size(); i++) {
			Packet p = packets.get(i);
			if (p.getType() == PacketTypeDefinitions.LENGTH)  {
				String s = p.getValue().replaceAll(Parser.SN, "");
				int length = Integer.parseInt(s);
				this.setLength(length);
			} else if  ((p.getType() == PacketTypeDefinitions.BOLS) && 
					(p.getKey().equalsIgnoreCase("Theka"))) {
				//get the Theka
				this.setTheka((RepresentableSequence) p.getObject());
			} else if (p.getType() == PacketTypeDefinitions.LAYOUT) {
				// do this after length!! So we postpone it and do it after the for loop
				layoutPacket = p;
			} else if (p.getType() == PacketTypeDefinitions.VIBHAGS) {
				Matcher m = Pattern.compile(Parser.SN +"*(\\d+)\\s*([kK]|Kali|Khali|kali|khali)?").matcher(p.getValue());

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
				} else p.setType(PacketTypeDefinitions.FAILED);
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
		
		boolean success = !(theka==null || length == 0 ||layoutChooser == null || vibhags == null);
		return success;
	}
	
	public String getName() {
		return owner.getMetaValues().getString(PacketTypeDefinitions.NAME);
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
	
	
	public RepresentableSequence getTheka() {
		return theka;
	}
	public void setTheka(RepresentableSequence theka) {
		this.theka = theka;
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
		return owner.getMetaValues().getString(PacketTypeDefinitions.NAME) + ", " + length + " beats";
	}
	

}

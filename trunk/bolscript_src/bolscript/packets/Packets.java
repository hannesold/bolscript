package bolscript.packets;

import java.util.ArrayList;

import bolscript.packets.types.PacketTypeDefinitions;

/**
 * Wraps an Arraylist of Packet objects.
 * @author hannes
 *
 */
public class Packets extends ArrayList<Packet> {

	private static final long serialVersionUID = 2675512803109497706L;

	/**
	 * Copy constructor. The elements are not "copied".
	 * @param packets
	 */
	private Packets(Packets packets) {
		super(packets);
	}

	public Packets() {
		super();
	}
	/**
	 * Returns the Paket in which the given CaretPosition was found, or null
	 * if no paket was found.
	 * @param caretPosition
	 * @return
	 */
	public Packet getPacketAtCaretPosition(int caretPosition) {

		for (Packet p: this) {
			if (p.hasTextReferences()) {
				if (p.getTextReference().contains(caretPosition)) {
					return p;
				}
			}
		}
		return null;
	}

	
	public Packet findReferencedBolPacket(Packet caller, String bolCandidate) {
		int callerIndex = this.indexOf(caller);
		if (callerIndex < 0) callerIndex = size();
		
		for (int i= callerIndex-1; i>=0; i--) {
			Packet p = get(i);
			if (p.getType() == PacketTypeDefinitions.BOLS &&
					p.getKey().equalsIgnoreCase(bolCandidate)) {
				return p;	
			}
		}

		return null;

	}

	public Packets clone() {
		Packets clone = (Packets) (new Packets(this));
		return clone;
	}

}

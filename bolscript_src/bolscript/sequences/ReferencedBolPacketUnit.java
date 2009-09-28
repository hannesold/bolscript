package bolscript.sequences;

import bolscript.packets.Packet;
import bolscript.packets.TextReference;

public class ReferencedBolPacketUnit extends Unit implements Representable {
	
	private Packet referencedPacket;
	
	public ReferencedBolPacketUnit(Packet referencedPacket,
			TextReference textReference) {
		super(Representable.REFERENCED_BOL_PACKET, referencedPacket, textReference);
	}
	
	public RepresentableSequence getSequence() {
		return (RepresentableSequence) ((Packet) obj).getObject();
	}
	
	public String toString() {
		return "Packet_Reference{"+((Packet) obj).getKey()+"}";
	}

	public RepresentableSequence flatten() {
		RepresentableSequence rep = ((RepresentableSequence) ((Packet) obj).getObject()).flatten();
		return rep;
	}
}

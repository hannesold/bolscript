package bolscript.sequences;

import bolscript.packets.Packet;
import bolscript.packets.TextReference;

public class ReferencedBolPacketUnit extends Unit implements Representable {
	
	private Packet referencedPacket;
	
	public ReferencedBolPacketUnit(Packet referencedPacket,
			TextReference textReference) {
		super(Representable.REFERENCED_BOL_PACKET, referencedPacket, textReference);
		this.referencedPacket = referencedPacket;
	}
	
	public Packet getReferencedPacket() {
		return referencedPacket;
	}
	
	public void setReferencedPacket(Packet p) {
		this.referencedPacket = p;
		this.obj = p;
	}

	public RepresentableSequence getSequence() {
		return (RepresentableSequence) ((Packet) obj).getObject();
	}
	
	public String toString() {
		return "Packet_Reference{"+((Packet) obj).getKey()+"}";
	}

	@Override
	public SpeedUnit addFlattenedToSequence(RepresentableSequence seq, SpeedUnit basicSpeedUnit, int currentDepth) {
		return ((RepresentableSequence) ((Packet) obj).getObject()).addFlattenedToSequence(seq, basicSpeedUnit, currentDepth);
	}
	
}

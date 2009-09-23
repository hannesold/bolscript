package bolscript.packets;

import java.util.ArrayList;

/**
 * Wraps an Arraylist of Packet objects.
 * @author hannes
 *
 */
public class Packets extends ArrayList<Packet> {

	private static final long serialVersionUID = 2675512803109497706L;
	
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
	
	

}

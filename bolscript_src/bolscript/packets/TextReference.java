package bolscript.packets;

/**
 * Stores a reference to a section in a string,<br>
 * for example in the raw data of a composition.<br>
 * Stores start and endindex.
 * @author hannes
 *
 */
public class TextReference {
	public int startIndex;
	public int endIndex;
	
	public TextReference (int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public String toString() {
		return "(Start: " + startIndex + ", End: " + endIndex + ")";
	}
	
}

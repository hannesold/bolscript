package bolscript.packets.types;

import java.util.Comparator;

public class HistoryEntryDateComparator implements Comparator<HistoryEntry> {
	
	private static HistoryEntryDateComparator standard = new HistoryEntryDateComparator();
	
	public static HistoryEntryDateComparator getStandard() {
		return standard;
	}
	
	@Override
	public int compare(HistoryEntry o1, HistoryEntry o2) {
		return o1.compareDateTo(o2);
	}

}

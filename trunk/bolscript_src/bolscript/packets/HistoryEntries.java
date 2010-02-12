package bolscript.packets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import bolscript.packets.types.HistoryEntry;
import bolscript.packets.types.HistoryEntryDateComparator;
import bolscript.packets.types.HistoryOperationType;

public class HistoryEntries implements BolscriptFormattableValue{
	private ArrayList<HistoryEntry> entries;
	private boolean changedSinceLastSorting = false;
	
	public HistoryEntries() {
		entries = new ArrayList<HistoryEntry>();
	}
	
	public void add(HistoryEntry entry) {
		entries.add(entry);
		changedSinceLastSorting = true;
		sort();
	}
	
	public HistoryEntry get(int i) {
		return entries.get(i);
	}
	
	public int size() {
		return entries.size();
	}
	
	private void sort() {
		if (!changedSinceLastSorting) {
			Collections.sort(entries, HistoryEntryDateComparator.getStandard());
			changedSinceLastSorting = true;
		}
	}
	
	/**
	 * Returns the earliest Entries date, or null, if none was found
	 * @return
	 */
	public HistoryEntry getCreationEntry() {
		if (entries.size() <= 0) {
			return null;
		} else {
			return entries.get(0);
		}
	}

	public HistoryEntry getLastModifiedEntry() {
		if (entries.size() <= 0) {
			return null;
		} else {
			HistoryEntry lastModificationEntry = null;
			for (int i=0; i < entries.size(); i++) {
				if (entries.get(i).getOperationTypeId() == HistoryOperationType.MODIFIED) {
					lastModificationEntry = entries.get(i);
				}
			}
			return lastModificationEntry;
		}
	}
	
	public String getCreationDateAsString() {
		HistoryEntry creationEntry = getCreationEntry();
		if (creationEntry != null) {
			if (creationEntry.getDate() != null) {
				return new SimpleDateFormat("yyyy.MM.dd").format(creationEntry.getDate());
			}
		}
		return "";
	}
	
	public String getModifiedDateAsString() {
		HistoryEntry modifiedEntry = getLastModifiedEntry();
		if (modifiedEntry != null) {
			if (modifiedEntry.getDate() != null) {
				return new SimpleDateFormat("yyyy.MM.dd").format(modifiedEntry.getDate());
			}
		}
		return "";
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < entries.size(); i++) {			
			builder.append(entries.get(i));
			builder.append("\n");			
		}
		return builder.toString();
	}

	@Override
	public String formatAsBolscriptValue() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < entries.size(); i++) {			
			builder.append(entries.get(i).formatForBolscript() + "\n");
		}
		return builder.toString();
		
	}


}

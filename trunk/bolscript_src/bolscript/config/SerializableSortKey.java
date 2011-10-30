package bolscript.config;

import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SerializableSortKey  {
	@XmlAttribute
	public int column;
	
	@XmlAttribute
	public SortOrder sortOrder;
	
	public SerializableSortKey() {
	}
	
	public SerializableSortKey(SortKey sortKey) {
		column = sortKey.getColumn();
		sortOrder = sortKey.getSortOrder();
	}
	
	public SortKey ToSortKey() {
		return new SortKey(column, sortOrder);
	}
}

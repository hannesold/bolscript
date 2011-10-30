package bolscript.config;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableColumn;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.xml.sax.InputSource;

import basics.Debug;

@XmlRootElement
public class TableDisplaySettings implements RowSorterListener {
	@XmlElement
	private ArrayList<TableDisplaySetting> columns = new ArrayList<TableDisplaySetting>();
	
	@XmlElement
	private ArrayList<ColumnMove> columnMoveProtocoll = new ArrayList<ColumnMove>();
	 
	@XmlElement
	private ArrayList<SerializableSortKey> sortKeys = new ArrayList<SerializableSortKey>();
	
	private JTable table = null;
	
	public TableDisplaySettings() {
		
	}
	public TableDisplaySettings (JTable table){
		this.table = table;
	}
	
	public void addColumn(String name, int index, int preferredWidth) {
		columns.add(new TableDisplaySetting(name,index,preferredWidth));
	}
	
	static JAXBContext context;
	static Unmarshaller unmarshaller;
	static Marshaller marshaller;
	
	public static TableDisplaySettings FromXml(String xml) throws Exception{
		try {			
			if (context == null) {
				context = JAXBContext.newInstance(TableDisplaySetting.class, TableDisplaySettings.class, ColumnMove.class);
			}
			if (unmarshaller == null) {
				unmarshaller = context.createUnmarshaller();
			}
			InputSource s = new InputSource(new StringReader(xml));
			TableDisplaySettings settings = (TableDisplaySettings) unmarshaller.unmarshal(s);
			return settings;
			
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void initFromTable() {
		initFromTable(table);
	}
	public void initFromTable(JTable table) {
		columns.clear();
		for (int i = 0; i < table.getModel().getColumnCount(); i++) {						
			addColumn(table.getColumnName(i), i, table.getColumnModel().getColumn(i).getPreferredWidth());
		}
		sortKeys = new ArrayList<SerializableSortKey>();
		for (SortKey k : table.getRowSorter().getSortKeys()) {
			sortKeys.add(new SerializableSortKey(k));
		}		
	}
	
	public String ToXml() throws Exception {
		JTable oldTable = table;
		try {
			table = null;
			if (context == null) {
				context = JAXBContext.newInstance(TableDisplaySetting.class, TableDisplaySettings.class, ColumnMove.class);
			}
			if (marshaller == null) {
				marshaller = context.createMarshaller();
			}
			StringWriter s = new StringWriter();
			marshaller.marshal(this, s);
			return s.toString();			
		} catch (Exception ex) {
			throw ex;
		} finally {
			table = oldTable;
		}
	}

	public void CopyValues(TableDisplaySettings other) {
		for (TableDisplaySetting settingFound : other.columns) {
			for (TableDisplaySetting existing : columns) {
				if (settingFound.columnname.equals(existing.columnname)) {
					existing.preferredWidth = settingFound.preferredWidth;
				}
			}
		}
		for (ColumnMove m : other.columnMoveProtocoll) {
			this.columnMoveProtocoll.add(m);
		}
		this.sortKeys = other.sortKeys;		
	}
	
	public void copySettingsToTable() {
		copySettingsToTable(table);
	}
	
	public void copySettingsToTable(JTable table) {
		for (int i = 0; i < table.getColumnCount();i++) {
			TableColumn c = table.getColumnModel().getColumn(i);
			for (int j = 0 ; j < columns.size(); j++) {
				if (columns.get(j).columnname.equals(table.getColumnName(i))) {
					c.setPreferredWidth(columns.get(j).preferredWidth);
				}
			}		
		}
		for (ColumnMove m : columnMoveProtocoll) {
			table.moveColumn(m.fromIndex, m.toIndex);				
		}
		ArrayList<SortKey> list = new ArrayList<SortKey>();
		for (SerializableSortKey k : this.sortKeys) {
			list.add(k.ToSortKey());
		}
		table.getRowSorter().setSortKeys(list);
	}
	public void addColumnMoveProtocol(int fromIndex, int toIndex) {
		ColumnMove m = new ColumnMove();
		m.fromIndex = fromIndex;
		m.toIndex = toIndex;
		columnMoveProtocoll.add(m);
	}
	@Override
	public void sorterChanged(RowSorterEvent arg0) {
		this.sortKeys = new ArrayList<SerializableSortKey>();
		for(SortKey k : table.getRowSorter().getSortKeys()) {
			this.sortKeys.add(new SerializableSortKey(k));
		}
		StringBuilder b = new StringBuilder();
		for (SerializableSortKey k : this.sortKeys) {
			b.append(k.column + ":"+k.sortOrder+", ");
		}
		Debug.debug(this, "updated sortkeys..." + b.toString());
	}
	
}
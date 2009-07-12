package gui.bolscript;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

public class FilterList extends JList {

	private int callNr=0;

	public FilterList() {
	}

	public FilterList(ListModel dataModel) {
		super(dataModel);
	}

	public FilterList(Object[] listData) {
		super(listData);
	}

	public FilterList(Vector<?> listData) {
		super(listData);
	}

	public void setSelectedIndices(int[] indices, int callNr) {
		this.callNr = callNr;
		super.setSelectedIndices(indices);
	}
	
	public int getCallNr() {
		return callNr;
	}

	public void setCallNr(int callNr) {
		this.callNr = callNr;
	}
	

}

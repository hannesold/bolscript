package gui.bolscript;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class FilterListPanel extends JPanel{

	private JLabel title;
	
	public FilterListPanel(String title, FilterList list) {
		super(true);
		this.setLayout(new BorderLayout());
		this.title = new JLabel(title);
		this.add(this.title,BorderLayout.NORTH);
		
		this.add( new JScrollPane(list),BorderLayout.CENTER);
	}

	public String getTitle() {
		return title.getText();
	}

	public void setTitle(String title) {
		this.title.setText(title);
		
	}
	
	
	
	
	

}

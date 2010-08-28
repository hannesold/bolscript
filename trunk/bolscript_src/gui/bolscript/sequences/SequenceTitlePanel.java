package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.GUI;
import bolscript.config.RunParameters;

public class SequenceTitlePanel extends JPanel {

	public JLabel title = null;
	private static Color cDistinctBackground = new Color(220,220,230);
	private static Color cDistinctBackgroundLabel = new Color(220,220,250);
	
//	public String titleAsText = null;

	public String getTitle() {
		return title.getText();
	}

	public void setTitle(String titleAsText) {
		
		this.title.setText(titleAsText);
		title.setPreferredSize(new Dimension(GUI.getPrefferedSize(title, 100).width, 40));

	}

	public SequenceTitlePanel() {
		super();
		initLabels();
		if (RunParameters.showLayoutStructure) {
			this.setBackground(cDistinctBackground);
			this.setOpaque(true);
			title.setBackground(cDistinctBackgroundLabel);
			title.setOpaque(true);
		} else this.setOpaque(false);
		
		
		// TODO Auto-generated constructor stub
	}
	
	public SequenceTitlePanel(String s) {
		this();
		setTitle(s);
	}
	public void initLabels() {
		title = new JLabel("unnamed");
		this.add(title);
	}

	public SequenceTitlePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initLabels();
	}

	public SequenceTitlePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initLabels();
	}

	public SequenceTitlePanel(LayoutManager layout) {
		super(layout);
		initLabels();
	}
	
	public void Render() {
		
		this.revalidate();

	}
	
}

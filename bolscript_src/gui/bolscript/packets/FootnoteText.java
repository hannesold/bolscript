package gui.bolscript.packets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.GUI;
import bolscript.packets.Packet;
import bolscript.sequences.FootnoteUnit;

public class FootnoteText extends JPanel {

	public JLabel text = null;
	public Packet packet = null;
	private FootnoteUnit commentUnit = null;
	public static int fontSize = 12;
	public static Font commentFont = new Font("Arial", Font.PLAIN, fontSize);
	private static Color cDistinctBackground = new Color(250,250,220);
	private static Color cDistinctBackgroundLabel = new Color(220,220,250);
	
//	public String titleAsText = null;

	public FootnoteText() {
		super();
		init();
	}
	
	public FootnoteText (Packet p) {
		super();
		
		this.packet = p;
		try {
			commentUnit = new FootnoteUnit(p.getKey());
		} catch (Exception e) {
			commentUnit = null;
		}
		init();
	}
	
	public void init() {
	
		text = new JLabel("Sorry, Comment could not be read!");
		text.setFont(commentFont);
		
		if ((commentUnit != null)&&(packet != null)) {
			text.setText((commentUnit.footnoteNrGlobal+1) + ")  " + packet.getValue() +" ");
		}
		//text.setBounds(0, 0, text.getPreferredSize().width+30, text.getPreferredSize().height);
		//GUI.setAllSizes(this, new Dimension(text.getPreferredSize().width+60, text.getPreferredSize().height+20));
		
		if (GUI.showLayoutStructure) {
			this.setBackground(cDistinctBackground);
			this.setOpaque(true);
			text.setBackground(cDistinctBackgroundLabel);
			text.setOpaque(true);

		} else this.setOpaque(false);
		
		this.add(text);
	}

	public FootnoteText(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public FootnoteText(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public FootnoteText(LayoutManager layout) {
		super(layout);
		init();
	}
	
	public void Render() {
		
		this.revalidate();

	}
	
}

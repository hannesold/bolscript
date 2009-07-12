package gui.bolscript.composition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.GUI;
import bolscript.packets.Packet;
import bolscript.sequences.FootnoteUnit;

public class CommentText extends JPanel {

	public JLabel text = null;
	public Packet packet = null;
	public static int fontSize = 12;
	public static Font commentFont = new Font("Arial", Font.PLAIN, fontSize);
	private static Color cDistinctBackground = new Color(250,250,220);
	private static Color cDistinctBackgroundLabel = new Color(220,220,250);
	
//	public String titleAsText = null;

	public CommentText() {
		super();
		init();
	}
	
	public CommentText (Packet p) {
		super();
		
		this.packet = p;
		init();
	}
	
	public void init() {
	
		
		text = new JLabel("Sorry, Comment could not be read!");
		text.setFont(commentFont);
		
		if ((packet != null)&& (packet.getObject() != null)) {
			text.setText((String) packet.getObject());
		}
		
		if (GUI.showLayoutStructure) {
			this.setBackground(cDistinctBackground);
			this.setOpaque(true);
			text.setBackground(cDistinctBackgroundLabel);
			text.setOpaque(true);

		} else this.setOpaque(false);
		
		this.add(text);
	}

	public CommentText(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public CommentText(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public CommentText(LayoutManager layout) {
		super(layout);
		init();
	}
	
	public void Render() {
		
		this.revalidate();

	}
	
}

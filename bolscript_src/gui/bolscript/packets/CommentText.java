package gui.bolscript.packets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import bolscript.config.GuiConfig;
import bolscript.config.RunParameters;
import bolscript.packets.Packet;
import bolscript.sequences.FootnoteUnit;

public class CommentText extends JPanel {

	public JLabel text = null;
	public Packet packet = null;
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
		text.setFont(GuiConfig.commentFont);
		
		if ((packet != null)&& (packet.getObject() != null)) {
			text.setText((String) packet.getObject());
		}
		
		if (RunParameters.showLayoutStructure) {
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

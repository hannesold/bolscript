package gui.bolscript.packets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.Debug;
import bolscript.config.RunParameters;
import bolscript.packets.Packet;
import bolscript.sequences.FootnoteUnit;

public class FootnoteText extends JPanel {

	public JLabel text = null;
	public Packet packet = null;
	private FootnoteUnit footnoteUnit = null;
	public static int fontSize = 12;
	public static Font footnoteFont = new Font("Arial", Font.PLAIN, fontSize);
	private static Color cDistinctBackground = new Color(250,250,220);
	private static Color cDistinctBackgroundLabel = new Color(220,220,250);
	
//	public String titleAsText = null;

	public FootnoteText() {
		super();
		init();
	}
	
	public FootnoteText (FootnoteUnit footnoteUnit) {
		super();
		this.footnoteUnit = footnoteUnit;
		//Debug.temporary(this, "building footnotetext for display " + footnoteUnit);
		init();
	}
	
	public void init() {
	
		text = new JLabel("Sorry, Footnote could not be read!");
		text.setFont(footnoteFont);
		
		if (footnoteUnit != null) {
			text.setText((footnoteUnit.footnoteNrGlobal) + ")  " + footnoteUnit.getFootnoteText() +" ");
		}
		
		if (RunParameters.showLayoutStructure) {
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

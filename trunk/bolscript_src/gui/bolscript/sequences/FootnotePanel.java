package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.GUI;
import bolscript.sequences.FootnoteUnit;

public class FootnotePanel extends JPanel{
	public static int fontSize = 9;
	public static Font commentFont= new Font("Arial", Font.BOLD, fontSize);
	public static int marginBottom = 0;
	public static int marginLeft = 0;
	public static int marginTop = 10;
	public static int marginRight = 5;
	
	
	public static Dimension maxSize = new Dimension();
	static {
		JLabel l = new JLabel("99)");
		l.setFont(commentFont);
		maxSize = l.getPreferredSize();
	}
	
	private JLabel label;
	
	private FootnoteUnit footnoteUnit;

	public FootnotePanel(FootnoteUnit footenoteUnit, Dimension size) {
		super(null, true);
		this.footnoteUnit = footenoteUnit;
		
		label = new JLabel("" + (footenoteUnit.footnoteNrGlobal+1) + ")");
//		Font f = new Font()
		label.setFont(commentFont);
		label.setForeground(Color.getHSBColor(0.5f, 0.7f, 0.1f));

		int width = maxSize.width; //label.getPreferredSize().width;
		int height = maxSize.height;//label.getPreferredSize().height;
		
		int x = marginLeft;
		int y = size.height - height - marginBottom;
		
		//System.out.println(x +", "+ y +", "+ width +", "+ height);
		label.setBounds(x,y,width,height);
		
		this.add(label);
		this.setSize(size);
		this.setPreferredSize(GUI.getPrefferedSize(label,100));
		
		if (GUI.showLayoutStructure) {
			this.setOpaque(true);
			this.setBackground(new Color(220,180,180));
		} else this.setOpaque(false);
		
		this.setToolTipText("See footnote " + (footenoteUnit.footnoteNrGlobal+1) + ") bellow.");
		//this.setToolTipText(bol.getPlayingStyle().toString());
		
	}
	
	
}

package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.GUI;
import bolscript.config.GuiConfig;
import bolscript.config.RunParameters;
import bolscript.sequences.FootnoteUnit;

public class FootnotePanel extends JPanel{
	public static int marginBottom = 0;
	public static int marginLeft = 0;
	public static int marginTop = 10;
	public static int marginRight = 5;
	
	public static Dimension maxSize = new Dimension();
	static {
		JLabel l = new JLabel("99)");
		l.setFont(GuiConfig.footnoteFont);
		maxSize = l.getPreferredSize();
	}
	
	private JLabel label;
	
	private FootnoteUnit footnoteUnit;

	public FootnotePanel(FootnoteUnit footenoteUnit, Dimension size) {
		super(null, true);
		this.footnoteUnit = footenoteUnit;
		
		label = new JLabel("" + (footenoteUnit.footnoteNrGlobal) + ")");
		label.setFont(GuiConfig.footnoteFont);
		label.setForeground(GuiConfig.footnotePanelColor);

		int width = maxSize.width; //label.getPreferredSize().width;
		int height = maxSize.height;//label.getPreferredSize().height;
		
		int x = marginLeft;
		int y = size.height - height - marginBottom;
		
		label.setBounds(x,y,width,height);
		
		this.add(label);
		this.setSize(size);
		this.setPreferredSize(GUI.getPrefferedSize(label,100));
		
		if (RunParameters.showLayoutStructure) {
			this.setOpaque(true);
			this.setBackground(new Color(220,180,180));
		} else this.setOpaque(false);
		
		this.setToolTipText("See footnote " + (footenoteUnit.footnoteNrGlobal) + ")");
		
	}
	
	
}

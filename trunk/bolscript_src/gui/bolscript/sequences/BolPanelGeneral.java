package gui.bolscript.sequences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import basics.GUI;
import bols.NamedInLanguages;
import bolscript.config.GuiConfig;
import bolscript.config.RunParameters;

public abstract class BolPanelGeneral extends JPanel implements MouseListener{
	
	public static int marginBottom = 2;
	public static int marginLeft = 2;
	public static int marginTop = 10;
	public static int marginRight = 4; //vormals 5
	public static float fontSize;
	
	public static BolPanel representative;
	
	protected JLabel label;

	protected boolean emphasized;
	protected boolean centered;
	
	public static int THIS = 100;
	public static int DIRECTNEIGHBOURS = 50;
	
	
	public void init(NamedInLanguages nameGiver, Dimension size, boolean isEmphasized, int language, float fontSize) {
		this.setLayout(null);
		this.emphasized = isEmphasized;
		this.fontSize = fontSize;
		
		label = new JLabel(nameGiver.getNameForDisplay(language));
		Font f = GuiConfig.getBolFont(language, fontSize, emphasized); //Config.bolFonts[language].deriveFont(fontSize);
		label.setFont(f);
		
		Dimension labelPrefSize = GUI.getPrefferedSize(label,100);
		
		int x;
		centered = false;//(labelPrefSize.width > size.width/2);
		
		if (centered) {
			x = (size.width - labelPrefSize.width) /2;
		} else x = marginLeft;
		
		int y = size.height - labelPrefSize.height - marginBottom;
		
		label.setBounds(x,y,labelPrefSize.width,labelPrefSize.height);
		
		this.add(label);
		this.setPreferredSize(label.getSize());
		
		
		if ( RunParameters.showLayoutStructure) {
			this.setOpaque(true);
			label.setOpaque(true);
			this.setBackground(new Color(130,130,170,150));
			label.setBackground(new Color(190,190,240,255));
			this.setBorder(new LineBorder(Color.gray,1,true));
		} else {
			this.setOpaque(false);
		}
		
		this.addMouseListener(this);
		
	}

	public boolean isCentered(){
		return centered;
	}
	public void setCentered(boolean centered) {
		this.centered = centered;
		int x;
		
		if (centered) {
			x = marginLeft; //(size.width - labelPrefSize.width) /2;
		} else x = marginLeft;
		label.setLocation(x, label.getLocation().y);
		
	}



	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		highLight();
		/*if (neighbours != null) {
			for (int i=0; i<neighbours.size(); i++) {
				neighbours.get(i).highLight(DIRECTNEIGHBOURS);
			}
		}*/
	}
	public void mouseExited(MouseEvent e) {
		normalDisplay();
		/*if (neighbours != null) {
			for (int i=0; i<neighbours.size(); i++) {
				neighbours.get(i).normalDisplay();
			}
		}*/
		
	}
	
	public void normalDisplay() {
		this.setOpaque(false);
		label.setForeground(GuiConfig.bolFontColor);
		this.setBorder(null);
	}	

	
	public void highLight(int strength) {
		if (strength == THIS) {
			this.setBackground(GuiConfig.bolHighlightedColor);
			this.setOpaque(true);
			label.setForeground(GuiConfig.bolFontHighlightColor);
			this.setBorder(new LineBorder(Color.black,1,true));				
		} else if (strength == DIRECTNEIGHBOURS){
			/*this.setBackground(cNeighbour);
			this.setOpaque(true);
			this.setBorder(new CellBorder(Color.BLACK,1));*/	
		}
	}
	

	public void highLight() {
		highLight(THIS);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}



}

package gui.fonts;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.Debug;
import basics.GUI;
import basics.SuffixFilter;
import bolscript.config.Config;

public class FontTests  implements ItemListener, WindowListener{
	private  JPanel examplePanel;
	private  JLabel exampleLabel;
	private  JComboBox fontSelector, fontSelectorLocal;
	private String pathToFonts = "INSERT PATH HERE";
	
public FontTests(){
	init();
}
public void init() {
	Debug.init();
	Debug.setExclusivelyMapped(false);
	//Debug.showErrorConsole();
	Config.init();
	
	JFrame mainFrame = new JFrame("Font chooser");
	JPanel panel = new JPanel();
	
	
	Font[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	
	try {
		File fontFolder = new File(pathToFonts);
		File[] fontFiles = fontFolder.listFiles(new SuffixFilter(".ttf"));
		Font[] localFonts = new Font[fontFiles.length];
		for (int i=0; i < fontFiles.length; i++){
			localFonts[i] = Font.createFont(Font.TRUETYPE_FONT, fontFiles[i]);
		}
		fontSelectorLocal = new JComboBox(localFonts);
		panel.add(fontSelectorLocal);
		fontSelectorLocal.addItemListener(this);
		
	} catch ( Exception e) {
	}
	
	fontSelector = new JComboBox(systemFonts);
	
	examplePanel = new JPanel();
	
	exampleLabel = new JLabel("0904-0906: " + new String(new char []{'\u0904',',', '\u0905', ',','\u0906'}));
	exampleLabel.setBackground(new Color(170,170,250));
	exampleLabel.setOpaque(true);
	
	fontSelector.addItemListener(this);

	
	examplePanel.add(exampleLabel);
	BoxLayout bx = new BoxLayout(panel,BoxLayout.Y_AXIS);
	panel.setLayout(bx);
	
	panel.add(fontSelector);
	mainFrame.setContentPane(panel);
	mainFrame.pack();
	mainFrame.setVisible(true);
	mainFrame.addWindowListener(this);
	
	JFrame exampleFrame = new JFrame("Example");
	exampleFrame.setContentPane(examplePanel);
	exampleFrame.pack();
	exampleFrame.setVisible(true);
	exampleFrame.addWindowListener(this);
	exampleFrame.setLocation(GUI.getRight(mainFrame), GUI.getTop(mainFrame));
}
	public static void main(String args[]) {
		FontTests ft = new FontTests();
	}
	
	public void itemStateChanged(ItemEvent e) {
		Font f = ((Font) ((JComboBox) e.getSource()).getSelectedItem()).deriveFont(12f);
		Debug.debug(this, f);
		exampleLabel.setFont(f);
	}
	public void windowActivated(WindowEvent e) {
	}
	public void windowClosed(WindowEvent e) {
	}
	public void windowClosing(WindowEvent e) {
		System.exit(0);		
	}
	public void windowDeactivated(WindowEvent e) {
	}
	public void windowDeiconified(WindowEvent e) {
	}
	public void windowIconified(WindowEvent e) {
	}
	public void windowOpened(WindowEvent e) {
	}
}

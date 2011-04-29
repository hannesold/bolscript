package gui.fonts;

import gui.bolscript.sequences.SequencePanel;
import gui.bolscript.sequences.SequenceTitlePanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import basics.Debug;
import basics.GUI;
import basics.SuffixFilter;
import bols.Bol;
import bols.BolBase;
import bols.BolName;
import bols.PlayingStyle;
import bols.tals.Teental;
import bolscript.config.Config;
import bolscript.config.GuiConfig;
import bolscript.config.UserConfig;
import bolscript.sequences.RepresentableSequence;

public class FontTests implements WindowListener, ListSelectionListener{
	private  JPanel examplePanel;
	private  JList fontSelector, fontSelectorLocal;
	
	private String pathToFonts = UserConfig.libraryFolder + Config.fileSeperator + "settings";
	
	JFrame exampleFrame = new JFrame("Examples");
	RepresentableSequence completeSequence;
	JFrame mainFrame;
	
public FontTests(){
	init();
}

public void init() {
	GUI.setNativeLookAndFeel();
	Debug.init();
	Debug.setExclusivelyMapped(false);
	//Debug.showErrorConsole();
	Config.init();
	BolBase.init(this.getClass());
	
	completeSequence = new RepresentableSequence();
	for (BolName bolName: BolBase.getStandard().getBolNames()) {
		completeSequence.add(new Bol(bolName, new PlayingStyle(1)));
	}

	
	JPanel panel = new JPanel();
	
	Font[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	
	try {
		File fontFolder = new File(pathToFonts);
		if (!fontFolder.exists()) throw new Exception("font folder not found"); 
		
		File[] fontFiles = fontFolder.listFiles(new SuffixFilter(".ttf"));
		
		Font[] localFonts = new Font[fontFiles.length];
		for (int i=0; i < fontFiles.length; i++){
			localFonts[i] = Font.createFont(Font.TRUETYPE_FONT, fontFiles[i]);
		}
		
		fontSelectorLocal = new JList(localFonts);
		panel.add(new JScrollPane(fontSelectorLocal));
		fontSelectorLocal.addListSelectionListener(this);
		fontSelectorLocal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
	} catch ( Exception e) {
		//fontSelectorLocal = null;
		Debug.critical(this, "could not load local fonts");
		e.printStackTrace();
	}
	
	fontSelector = new JList(systemFonts);
	panel.add(new JScrollPane(fontSelector));
	fontSelector.addListSelectionListener(this);
	fontSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	

	BoxLayout bx = new BoxLayout(panel,BoxLayout.Y_AXIS);
	panel.setLayout(bx);
	
	
	
	mainFrame = new JFrame("Font chooser");
	mainFrame.setContentPane(panel);
	mainFrame.pack();
	GuiConfig.setVisibleAndAdaptFrameLocation(mainFrame);
	mainFrame.addWindowListener(this);
	
	examplePanel = new JPanel();
	examplePanel.setLayout(new BoxLayout(examplePanel, BoxLayout.Y_AXIS));
	JScrollPane scrollPane = new JScrollPane(examplePanel);
	exampleFrame.setContentPane(scrollPane);
	exampleFrame.pack();
	GuiConfig.setVisibleAndAdaptFrameLocation(exampleFrame);
	exampleFrame.setLocation(GUI.getRight(mainFrame), GUI.getTop(mainFrame));
	
	refreshSequenceFrame();
	
	
	exampleFrame.addWindowListener(this);
	
	
	

}
	private void refreshSequenceFrame() {
		examplePanel.removeAll();
		
		
		//sequencePanels = new SequencePanel[BolName.languagesCount];
		for ( int i = BolName.DEVANAGERI; i <= BolName.TRANSLITERATION; i++) {
			SequencePanel sequencePanel = new SequencePanel(completeSequence, Teental.getDefaultTeental(),
					new Dimension(800,80), 0, "", 0, i, 14f, null, null);
			SequenceTitlePanel t = new SequenceTitlePanel();
			t.setTitle(BolName.languageNames[i]);
			examplePanel.add(t);
			examplePanel.add(sequencePanel);
		}
		
		exampleFrame.pack();
		
		
		
	}

	public static void main(String args[]) {
		FontTests ft = new FontTests();
	}
	

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Font f = ((Font) ((JList) e.getSource()).getSelectedValue());
		Debug.debug(this, f);
		
		Font[] fonts = new Font[BolName.languagesCount];
		for (int i=0; i < fonts.length; i++) {
			fonts[i] = f;
		}
		GuiConfig.setAllBolFonts(fonts);
		refreshSequenceFrame();
		//exampleLabel.setFont(f);
		
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

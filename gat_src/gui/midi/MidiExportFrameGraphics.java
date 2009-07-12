package gui.midi;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JFileChooser;

public class MidiExportFrameGraphics extends JFrame {

	private JPanel jContentPane = null;
	protected JFileChooser jFileChooser = null;

	/**
	 * This is the default constructor
	 */
	public MidiExportFrameGraphics() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(513, 367);
		this.setContentPane(getJContentPane());
		this.setTitle("Export Midi File");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJFileChooser(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jFileChooser	
	 * 	
	 * @return javax.swing.JFileChooser	
	 */
	private JFileChooser getJFileChooser() {
		if (jFileChooser == null) {
			jFileChooser = new JFileChooser();
			jFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		}
		return jFileChooser;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

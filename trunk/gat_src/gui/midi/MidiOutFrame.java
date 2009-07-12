package gui.midi;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import midi.MidiStation;

public class MidiOutFrame extends JFrame {

	private JPanel jContentPane = null;

	/**
	 * This is the default constructor
	 */
	public MidiOutFrame(MidiStation midiStation) {
		super();
		initialize();
		this.getContentPane().add(new MidiOutSelectorPanel(midiStation));
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 176);
		this.setContentPane(getJContentPane());
		this.setTitle("Midi Out Settings");
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
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

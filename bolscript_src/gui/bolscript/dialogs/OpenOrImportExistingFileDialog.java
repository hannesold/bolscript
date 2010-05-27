package gui.bolscript.dialogs;

import gui.bolscript.BrowserFrame;

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import basics.Debug;
import basics.GUI;

public class OpenOrImportExistingFileDialog extends JDialog implements PropertyChangeListener{
	//private EditorFrame editor;
	public static final Object[] options = new Object[] {
		"Just open",
		"Import to Library and open" 
		};
	public static final int IMPORT = 1;
	public static final int JUST_OPEN = 0;
	
	private JOptionPane optionPane;
	
	public OpenOrImportExistingFileDialog (BrowserFrame browser) {
		super(browser);
		this.setModal(true);
		optionPane = new JOptionPane("<html>" +
				"<span style=\"font-size:105%; font-weight:bold\">Import to your library?</span><br />" +
				"The file you opened is not in your library yet.<br /> " +
				"If you choose to import it, a physical copy of the file <br />" +
				"will be put in your tabla folder, so, when you run bolscript <br />" +
				"the next time it will still appear in your list of compositions.",
				JOptionPane.QUESTION_MESSAGE ,
				JOptionPane.YES_NO_CANCEL_OPTION,
				null,
				options);
		this.setContentPane(optionPane);
		
		optionPane.addPropertyChangeListener(this);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(GUI.proxyWindowCloseListener(this, "onClickCloseWindow"));

		this.pack();
		this.setBounds(browser.getBounds().x, browser.getBounds().y, this.getPreferredSize().width, this.getPreferredSize().height);
		
	}

	public void onClickCloseWindow(WindowEvent e) {
		optionPane.setValue(options[JUST_OPEN]);
		this.setVisible(false);
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		Debug.debug(this, "property Change");
	     String prop = e.getPropertyName();

	     if (this.isVisible() 
	             && (e.getSource() == optionPane)
	             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
	    	 Debug.debug(this, "listened to changed property!");
	                //If you were going to check something
	                //before closing the window, you'd do
	                //it here.
	                this.setVisible(false);
	       }
	     
	}

	public int getChoice() {
		String val = (String) optionPane.getValue();
		for (int i = 0; i< options.length; i++) {
			if (val.equals(options[i])) return i;
		}
		return JUST_OPEN;
	}
}

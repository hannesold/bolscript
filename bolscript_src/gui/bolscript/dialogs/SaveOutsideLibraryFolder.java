package gui.bolscript.dialogs;

import gui.bolscript.EditorFrame;

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import basics.Debug;
import basics.GUI;

public class SaveOutsideLibraryFolder extends JDialog implements PropertyChangeListener{
	private EditorFrame editor;
	public static final Object[] options = new Object[] {"Proceed", "Change Folder", "Cancel"};
	public static final int PROCEED = 0;
	public static final int CHANGE_FOLDER = 1;
	public static final int CANCEL = 2;
	
	private JOptionPane optionPane;
	
	public SaveOutsideLibraryFolder (EditorFrame editor) {
		super(editor);
		this.setModal(true);
		optionPane = new JOptionPane(				"<html>" +
				"<span style=\"font-size:105%; font-weight:bold\">" +
				"Are you sure you want to save the file outside of your <b>Compositions folder</b> ?</span><br />" +
				"This way your composition will not stay<br/>" +
				"in your list upon relaunching bolscript.",
				JOptionPane.QUESTION_MESSAGE ,
				JOptionPane.YES_NO_CANCEL_OPTION,
				null,
				options);
		this.setContentPane(optionPane);
		
		optionPane.addPropertyChangeListener(this);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(GUI.proxyWindowCloseListener(this, "onClickCloseWindow"));

		this.pack();
		this.setBounds(editor.getBounds().x, editor.getBounds().y, this.getPreferredSize().width, this.getPreferredSize().height);
		
	}

	public void onClickCloseWindow(WindowEvent e) {
		optionPane.setValue(options[CANCEL]);
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
		return CANCEL;
	}
}

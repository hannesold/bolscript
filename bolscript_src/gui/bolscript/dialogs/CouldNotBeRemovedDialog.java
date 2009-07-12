package gui.bolscript.dialogs;

import gui.bolscript.BrowserFrame;

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import basics.Debug;
import basics.GUI;
import bolscript.compositions.Composition;

public class CouldNotBeRemovedDialog extends JDialog implements PropertyChangeListener{
	public static final Object[] options = new Object[] {"OK"};
	public static final int OK = 0;
	
	
	private JOptionPane optionPane;
	
	public CouldNotBeRemovedDialog (BrowserFrame browser, ArrayList<Composition> failed) {
		super(browser);
		this.setModal(true);
		StringBuilder failedComps = new StringBuilder();
		for(int i=0; i<failed.size(); i++) {
			failedComps.append(failed.get(i).getName());
			if (i!=failed.size()-1) {
				failedComps.append("<br />");
			}
		}
		
		optionPane = new JOptionPane("<html><span style=\"font-size:105%; font-weight:bold\">Some compositions could not be removed, <br />because they are currently being edited:<br/></span>" +
			    failedComps.toString() + "<html>",
				JOptionPane.QUESTION_MESSAGE ,
				JOptionPane.YES_OPTION,
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
		optionPane.setValue(options[OK]);
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
		return OK;
	}
}

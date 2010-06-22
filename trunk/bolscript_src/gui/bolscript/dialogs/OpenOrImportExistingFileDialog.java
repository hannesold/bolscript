package gui.bolscript.dialogs;

import gui.bolscript.BrowserFrame;

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import basics.Debug;
import basics.GUI;
import bolscript.compositions.Composition;

public class OpenOrImportExistingFileDialog extends JDialog implements PropertyChangeListener{
	//private EditorFrame editor;
	public static final Object[] options = new Object[] {
		"Just open",
		"Import to Library and open" 
		};
	public static final int IMPORT = 1;
	public static final int JUST_OPEN = 0;
	
	private JOptionPane optionPane;
	
	public OpenOrImportExistingFileDialog (BrowserFrame browser, List<Composition> comps) {
		super(browser);
		this.setModal(true);
		
		
		
		
		String message = "";
		if (comps.size() >1) {
			message= "<html>" +
			"<span style=\"font-size:105%; font-weight:bold\">Import to your library?</span><br />" +
			"The following files you opened are not in your library yet:<br /> " +
			comps.get(0).getName()
			+
			"If you choose to import them, a physical copy of each<br />" +
			"will be placed in your library folder, so, when you run bolscript<br />" +
			"the next time it will still appear in your list of compositions.";
		} else {
			String descriptions = new String();		
			descriptions += "<ul>";
			for(Composition comp:comps) {
				descriptions += "<li>"+comp.getName()+"</li>";
			}
			descriptions +="</ul>";
			message= "<html>" +
			"<span style=\"font-size:105%; font-weight:bold\">Import to your library?</span><br />" +
			"The file you opened is not in your library yet:<br /> " +
			descriptions
			+
			"If you choose to import it, a physical copy of it <br />" +
			"will be placed in your library, so, when you run bolscript <br />" +
			"the next time it will still appear in your list of compositions.";
		}
		optionPane = new JOptionPane(message,
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

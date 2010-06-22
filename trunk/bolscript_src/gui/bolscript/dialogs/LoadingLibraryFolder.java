package gui.bolscript.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadingLibraryFolder extends JFrame {
Dimension minDimension =new Dimension(300,200);
	public LoadingLibraryFolder() {
		super();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//this.setSize(dimension);
		this.setLocation(400,300);
		this.initContentPane();
		
	}
	
	private void initContentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("<html><p>Loading Library from folder...</p></html>");
		contentPane.add(label,BorderLayout.CENTER);
		
		contentPane.setPreferredSize(minDimension);
		this.setContentPane(contentPane);
		
		this.pack();
		
	}
}

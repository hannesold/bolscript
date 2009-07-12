package gui.composers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import algorithm.composers.ComposerStandard;

public class ComposersPanel extends JPanel {
	private ArrayList<ComposerPanel> composers;
	private JPanel composersPanel;
	private Component lastSpacer;
	public static Color cBackground = new Color(200,200,200);
	public ComposersPanel() {
		super(true);
		BoxLayout bl = new BoxLayout(this, BoxLayout.LINE_AXIS);
		
		composersPanel= new JPanel();
		BoxLayout bl2 = new BoxLayout(composersPanel, BoxLayout.LINE_AXIS);
		composersPanel.setLayout(bl2);
		composersPanel.setBackground(cBackground);
		
		//this.setLayout(bl);
		Border border = new LineBorder(cBackground,15);
		setBackground(cBackground);
		this.setBorder(border);
		composers = new ArrayList<ComposerPanel>();
		//lastSpacer = Box.createRigidArea(new Dimension(15,0));
		// TODO Auto-generated constructor stub
		this.add(composersPanel,BorderLayout.CENTER);
	}
	
	public void add(ComposerPanel cp) {
		//remove(lastSpacer);
		if (composers.size()>0) {
			composersPanel.add(Box.createRigidArea(new Dimension(15,0)));
		}
		composersPanel.add((JPanel)cp);
		composers.add(cp);
	}
	
	public void setSolo(ComposerPanel cp) {
		for (ComposerPanel composer: composers) {
			if (cp != composer) {
				composer.informToBeMuted();
			}
		}
	}

	public void setSolo(ComposerStandard currentComposer) {
		System.out.println("setSolo("+currentComposer+")");
		for (ComposerPanel composer: composers) {
			System.out.println("comparing to " +composer.getComposer() );
			if ((currentComposer) != composer.getComposer()) {
				composer.informToBeMuted();
				//System.out.println(composer + ".inform to be muted!");
			} else {
				composer.informToBeSolo();
			}
		}
	}
	
}

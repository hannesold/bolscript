package gui.playlist;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import algorithm.composers.kaida.Individual;
import bols.tals.Tal;

public class PlaylistBoxLayout extends JPanel {
	private ArrayList<IndividualPanel> individuals;
	private IndividualPanel currentlyPlaying;
	public static Color cBackground = new Color(220,220,210);
	public PlaylistBoxLayout() {
		super(true);
		BoxLayout boxLayout = new BoxLayout(this,BoxLayout.PAGE_AXIS);
		this.setLayout(boxLayout);
		Border border = new LineBorder(cBackground,15);
		this.setBorder(border);
		setBackground(cBackground);
		

		currentlyPlaying = null;
		individuals = new ArrayList<IndividualPanel>();
		setOpaque(true);
		
	}
	
	public void addIndividual(Individual in, Tal tal) {
		IndividualPanel ip = new IndividualPanel(in, tal, true);
		ip.setVisible(false);
		individuals.add(ip);
		this.add(ip);
		this.revalidate();
		
		ip.setVisible(true);
//		EventQueue.invokeLater( new Runnable()
//				{
//				  public void run() {
					  //this.add(ip);
		this.add(Box.createRigidArea(new Dimension(0,15)));
//				  }
//				}
//		);
		
	}
	
	public void setPlayingLast() {
		if (individuals.size()>0) {
			IndividualPanel lastIndividual = individuals.get(individuals.size()-1);
			if (currentlyPlaying != lastIndividual) {
				if (currentlyPlaying!=null) {
					currentlyPlaying.setPlaying(false);
				}
				lastIndividual.setPlaying(true);
				currentlyPlaying = lastIndividual;				
			}
		}
	}

	public void addAndPlay(Individual currentCandidate, Tal tal) {
		addIndividual(currentCandidate, tal);
		setPlayingLast();
		scrollRectToVisible(individuals.get(individuals.size()-1).getBounds());
		//repaint();
		//this.invalidate();
		/*EventQueue.invokeLater( new Runnable()
				{
				  public void run() {
					
				}
		} );*/
		
		
	}

	public void highlightCell(Integer cell) {
		// TODO Auto-generated method stub
		if (currentlyPlaying != null) {
			currentlyPlaying.highlightCell(cell);
			//scrollRectToVisible(currentlyPlaying.getBounds());
			//this.revalidate();
		}
		

	}


}

package gui.playlist;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import algorithm.composers.kaida.Individual;
import bols.tals.Tal;

public class Playlist extends JPanel {
	private ArrayList<PlayablePanel> playablePanels;
	private PlayablePanel currentlyPlaying, lastAdded;
	private boolean viewFollowing;
	public static Color cBackground = new Color(220,220,210);
	private static int marginIndividuals = 15;
	private static Insets insets = new Insets(15,30,0,15);
	
	private IndividualPanel lastIndividualAdded;
	
	public Playlist() {
		super(null, true);
		setBackground(cBackground);
		
		currentlyPlaying = null;
		lastAdded = null;
		lastIndividualAdded = null;
		
		playablePanels = new ArrayList<PlayablePanel>(1000);
		setOpaque(true);
		viewFollowing = true;
		
	}
	
	public void addIndividual(Individual in, Tal tal) {
		boolean showFeatures = false;
		if (lastIndividualAdded!=null) {
			showFeatures = lastIndividualAdded.isShowingFeatures();
		}
		IndividualPanel ip = new IndividualPanel(in, tal, showFeatures);
		int x;
		int y;
		int width = ip.getPreferredSize().width;
		int height = ip.getPreferredSize().height;
		
		if (lastAdded == null) {
			x = insets.left;
			y = marginIndividuals;
		} else {
			x = insets.left;
			y = lastAdded.getLocation().y 
				+ lastAdded.getHeight() 
				+ marginIndividuals;
		}
		
		ip.setBounds(x, y, width, height);
		playablePanels.add(ip);
		lastAdded = ip;
		lastIndividualAdded = ip;
		
		Dimension newSize = new Dimension(getPreferredSize().width,
				getPreferredSize().height + height + marginIndividuals);
		
		EventQueue.invokeLater(new EventSetSize(this, newSize, 
				new EventAddIndividualPanel(this, ip)));
		
	}
	
	public void setPlayingLast() {
		if (playablePanels.size()>0) {
			PlayablePanel lastPlayable = playablePanels.get(playablePanels.size()-1);
			if (currentlyPlaying != lastPlayable) {
				if (currentlyPlaying!=null) {
					currentlyPlaying.setPlaying(false);
				}
				lastPlayable.setPlaying(true);
				currentlyPlaying = lastPlayable;				
			}
		}
		if (viewFollowing) {
			followView();
		}
	}

	private void followView() {
		if (currentlyPlaying != null) {
			EventQueue.invokeLater(new EventScrollToShowTarget(this, currentlyPlaying));
		}
	}

	public void addAndPlay(Individual currentCandidate, Tal tal) {
		addIndividual(currentCandidate, tal);
		setPlayingLast();
	}

	public void highlightCell(Integer cell) {
		// TODO Auto-generated method stub
		if (currentlyPlaying != null) {
			currentlyPlaying.highlightCell(cell);
			//scrollRectToVisible(currentlyPlaying.getBounds());
			//this.revalidate();
		}
	}

	public void setViewFollowing(boolean b) {
		viewFollowing = b;
		if (viewFollowing) {
			followView();
		}
		
	}

	public boolean isViewFollowing() {
		return viewFollowing;
	}


	/**
	 *  Saves the established playlist as an image file. 
	 *	@param filename The image filename
 	*/
	public void exportToImageFile(String filename) {
		exportToImageFile(new File(filename));		
	}

	/**
	 * Saves the established playlist as an image file.
	 * @param outputFile The file which shall contain the sequence.
	 */
	public void exportToImageFile(File outputFile) {
		try
		{
			BufferedImage bi = new BufferedImage( this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB );
			Graphics2D g2d = bi.createGraphics();
			this.paint(g2d);
			String typ = "png";
			ImageIO.write( bi, typ, outputFile );
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("file could not be saved");
			//System.exit(1);
		}
	}
	

}

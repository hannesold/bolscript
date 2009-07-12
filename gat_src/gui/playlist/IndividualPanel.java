package gui.playlist;

import gui.bols.VariationPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import algorithm.composers.kaida.Individual;
import basics.GUI;
import bols.tals.Tal;

public class IndividualPanel extends PlayablePanel{
	private Individual individual;
	private Tal tal;
	private VariationPanel variationPanel;
	private JPanel featuresControlPanel;
	private FeaturesPanel featuresPanel;
	private JToggleButton btnShowFeatures;
	
	private boolean playing;
	private boolean showFeatures;
	private boolean hasFeatures;
	private Dimension niceSize;
	
	private static Color cBorderNotPlaying = new Color(100,100,100);
	private static Color cBorderPlaying = new Color(30,255,30);
	private static int borderNotPlaying = 2;
	private static int borderPlaying = 5;
	
	
	
	public IndividualPanel(Individual in, Tal tal, boolean showFeatures) {
		super();
		this.setLayout(new BorderLayout());
		
		this.individual = in;
		this.tal = tal;
		this.hasFeatures = individual.isRated();
		this.showFeatures = showFeatures && hasFeatures;
		
		this.playing = false;
		
		
		this.add(getVariationPanel(), BorderLayout.CENTER);
		this.add(getFeaturesControlPanel(), BorderLayout.EAST);
		
		getBtnShowFeatures().addActionListener(GUI.proxyActionListener(this,"showFeatures"));
		btnShowFeatures.setSelected(showFeatures);
		btnShowFeatures.setVisible(hasFeatures);
		
		this.updateSizes();

		this.setBorder(BorderFactory.createLineBorder(cBorderNotPlaying,borderNotPlaying));
	}
	
	public VariationPanel getVariationPanel() {
		if (variationPanel == null) {
			variationPanel = new VariationPanel(individual.getVariation(), tal, new Dimension(800,100));
		}
		return variationPanel;
	}
	
	public JPanel getFeaturesControlPanel() {
		if (featuresControlPanel == null) {
			featuresControlPanel = new JPanel();
			BoxLayout layout = new BoxLayout(featuresControlPanel, BoxLayout.Y_AXIS);
			featuresControlPanel.setLayout(layout);
			featuresControlPanel.add(getBtnShowFeatures(), null);
			featuresControlPanel.add(getFeaturesPanel(), null);
			
		}
		return featuresControlPanel;
	}
	
	public FeaturesPanel getFeaturesPanel() {
		if (featuresPanel == null) {
			featuresPanel = new FeaturesPanel(this.individual, new Dimension(400,getVariationPanel().getHeight()-getBtnShowFeatures().getHeight()));
			featuresPanel.setVisible(showFeatures);
			featuresPanel.setAlignmentX(0f);
		}
		return featuresPanel;
	}


	public JToggleButton getBtnShowFeatures() {
		if (btnShowFeatures == null) {
			btnShowFeatures = new JToggleButton("...");
			GUI.setAllSizes(btnShowFeatures,new Dimension(20,20));
			//btnShowFeatures.setHorizontalAlignment(SwingConstants.LEFT);
			btnShowFeatures.setAlignmentX(0f);
			
		}
		return btnShowFeatures;
	}
	
	
	public void updateSizes() {
		featuresPanel.setVisible(showFeatures && hasFeatures);
		if (showFeatures && hasFeatures) {
			niceSize = GUI.addPreferredWidths(variationPanel, featuresPanel);//new Dimension(variationPanel.getPreferredSize().width+featuresPanel.getWidth(), variationPanel.getHeight());
		} else {
			niceSize = GUI.addPreferredWidths(variationPanel, btnShowFeatures);
		}
		GUI.setAllSizes(this, niceSize);
	}
	
	public void setPlaying(boolean playing) {
		if (playing!=this.playing) {
			this.playing = playing;
			variationPanel.setPlaying(playing);
			if (playing ){
				this.setBorder(BorderFactory.createLineBorder(cBorderPlaying, borderPlaying));
			} else {
				this.setBorder(BorderFactory.createLineBorder(cBorderNotPlaying, borderNotPlaying));
				this.unHighlightCells();
			}
		}
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void showFeatures(ActionEvent e) {
		showFeatures(btnShowFeatures.isSelected());
		
	}
	
	public void showFeatures(boolean b) {
		if (showFeatures != b) {
			showFeatures = b;
			this.updateSizes();
		}
	}
	
	
	public boolean isShowingFeatures() {
		return showFeatures;
	}
	
	public void unHighlightCells() {
		variationPanel.unHighlightCells();
	}
	
	public void highlightCell(int cell) {
		variationPanel.highlightCell(cell);
	}




}

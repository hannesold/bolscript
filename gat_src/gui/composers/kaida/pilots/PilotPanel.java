package gui.composers.kaida.pilots;

import java.awt.event.ActionEvent;

import javax.swing.event.ChangeEvent;

import algorithm.composers.kaida.KaidaComposer;
import algorithm.pilots.Pilot;
import basics.GUI;

public class PilotPanel extends PilotPanelGraphics {
	
	private Pilot pilot;
	private boolean active;
	private KaidaComposer kaidaPlayer;
	
	public PilotPanel() {
		super();
		this.pilot = pilotSelector.getSelectedPilot();
		active = true;
		init();
	}
	
	public PilotPanel(Pilot pilot, KaidaComposer kaidaPlayer) {
		super();
		this.pilot = pilot;
		this.kaidaPlayer = kaidaPlayer;
		active = true;
		init();
	}
	
	private void init() {
		pilotActive.getModel().setSelected(active);
		pilotActive.addActionListener(GUI.proxyActionListener(this,"setActive"));
		pilotSelector.addActionListener(GUI.proxyActionListener(this,"selectPilot"));
		setPilot.addActionListener(GUI.proxyActionListener(this,"setPilot"));
		pilotPositionSlider.addChangeListener(GUI.proxyChangeListener(this,"setPosition"));
		setPilot.setVisible(false);
		updateDisplay();
		
	}
	
	public void setActive(ActionEvent e) {
		active = !active;
		pilot.setActive(active);
		pilotSelector.setEnabled(active);
		description.setEnabled(active);
		setPilot.setEnabled(active);
		pilotPositionSlider.setEnabled(active);
	}
	
	public void updateDisplay() {
		description.setText(pilot.getDescription());
		pilotPositionSlider.setMinimum(0);
		pilotPositionSlider.setMaximum(pilot.getDuration());
		pilotPositionSlider.setValue(pilot.getPosition());
		pilotPositionSlider.setPaintLabels(pilot.getDuration() < 20);
		progressReport.setText(""+pilot.getPosition() + "/" + pilot.getDuration());
	}
	
	public void selectPilot(ActionEvent e) {
		if (e.getSource() == pilotSelector) {
			Pilot oldPilot = pilot;
			if (oldPilot != pilotSelector.getSelectedPilot()) {
				this.pilot = pilotSelector.getSelectedPilot();
				updateDisplay();
			}
			setPilot.setVisible(true);
		}
	}
	
	public void setPilot(ActionEvent e) {
		setPosition(0);
		kaidaPlayer.setPilot(pilot);	
		setPilot.setVisible(false);
	}
	
	public void setPosition(int pos) {
		if (pilotPositionSlider.getValue() != pos) {
			pilotPositionSlider.setValue(pos);
		}
		pilot.setPosition(pilotPositionSlider.getValue());
	}
	
	public void setPosition(ChangeEvent e) {
		if (e.getSource()==pilotPositionSlider) {
			pilot.setPosition(pilotPositionSlider.getValue());
		} 
	}

	public Pilot getPilot() {
		return pilot;
	}
	
	public void updateVisuals(Pilot pilot) {
		if (pilot != this.pilot) {
			this.pilot = pilot;
			updateDisplay();
		}
		updateSlider();
	}
	
	public void updateSlider() {
		pilotPositionSlider.setValue(pilot.getPosition());
		progressReport.setText(""+pilot.getPosition() + "/" + pilot.getDuration());
	}
	
}

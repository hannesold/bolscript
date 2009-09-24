package gui.bolscript;

import gui.bolscript.tables.BolBasePanel;

import java.awt.EventQueue;

import javax.swing.JTextPane;

import basics.Debug;
import bols.BolBase;
import bols.BolName;
import bolscript.Reader;
import bolscript.compositions.Composition;
import bolscript.packets.Packet;
import bolscript.packets.types.PacketTypeFactory;

public class BolBaseSearcher implements TaskFactory {

	JTextPane textPane;
	BolBasePanel bolBasePanel;
	Composition composition;
	
	public BolBaseSearcher(Composition composition, JTextPane textPane, BolBasePanel bolBasePanel) {
		this.textPane = textPane;
		this.bolBasePanel = bolBasePanel;
		this.composition = composition;
	}

	public Runnable getNewTask() {
		String selection;

		try {
			selection = textPane.getSelectedText();	
		} catch (IllegalArgumentException ex) {
			selection = null;
		}
		if (selection != null) {
			//Debug.temporary(this, "selection is not null");
			return new SearchSelectionTask(selection);
		} else {
			int caretPosition = textPane.getCaretPosition();
			Packet p = composition.getPackets().getPacketAtCaretPosition(caretPosition);
			
			if (p != null) {
				if (p.getType() == PacketTypeFactory.BOLS) {
					Debug.temporary(this, "caret is in a bol packet");
					if (p.getTextRefValue().contains(caretPosition)) {
						Debug.temporary(this, "new caret task");
						return new CaretGuessTask(textPane.getText(),textPane.getCaretPosition());	
					}
				}
			}
		}
		return null;
	}

	public String getTaskName() {
		return "BolBase Searcher";
	}

	public class CaretGuessTask implements Runnable {
		String text;
		static final int RANGE = 20;

		int caretPosition;
		//Packets packets;
		
		public CaretGuessTask (String text, int caretPosition) {
			//Debug.temporary(this, "caret task constructor");
			this.caretPosition = caretPosition;
			this.text = text;
		}

		public void run() {
			Debug.temporary(this, "running caretguesstask");
			if (text != null) {
				//Debug.temporary(this, this.caretPosition +" in " + text.length()  +" ");
				if (caretPosition <= text.length()) {
					//Debug.temporary(this, "caret is IN the text");
					String input = Reader.determineBolStringAroundCaret(text, caretPosition);
					if (input != null) {
						//Debug.temporary(this, "bolstring around caret " + input);
						BolName bolName = BolBase.getStandard().getResemblingBol(input);
						if (bolName != null) {
							//Debug.temporary(this, "found bol: " + bolName);
							EventQueue.invokeLater(new SelectTask(bolName));
						}
					}
				}
			} else {
				//Debug.temporary(this, "caretPosition is " + caretPosition +", but text length " + text.length());
			}
		}

	}


	private class SearchSelectionTask implements Runnable{

		String selection;

		public SearchSelectionTask(String selection) {
			this.selection = selection;
		}

		public void run() {

			Debug.temporary(this, "selected text "+ selection);
			if (selection != null) {

				BolName bolName = BolBase.getStandard().getBolName(selection);
				if (bolName != null) {
					Debug.temporary(this, "found bol: " + bolName);
					EventQueue.invokeLater(new SelectTask(bolName));
				}

			}

		}	


	}

	/**
	 * This is the actual runnable that tells the bolBasePanel to select a bol
	 * it should only be run in the eventqueue, because it accesses gui elements.
	 * @author hannes
	 */
	private class SelectTask implements Runnable {
		private BolName bolName;

		SelectTask(BolName bolName) {
			this.bolName = bolName;
		}

		public void run() {
			bolBasePanel.selectBol(bolName);
		}
	}




}

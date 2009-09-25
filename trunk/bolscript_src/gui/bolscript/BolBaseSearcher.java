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
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeFactory;

public class BolBaseSearcher implements TaskFactory {

	JTextPane textPane;
	BolBasePanel bolBasePanel;
	Composition composition;
	BolscriptDocument document;
	static Packet previouslyHighlightedPacket;

	public BolBaseSearcher(Composition composition, JTextPane textPane, BolBasePanel bolBasePanel, BolscriptDocument document) {
		this.textPane = textPane;
		this.bolBasePanel = bolBasePanel;
		this.composition = composition;
		this.document = document;
	}

	public Runnable getNewTask() {
		int caretPosition = textPane.getCaretPosition();
		Packet p = composition.getPackets().getPacketAtCaretPosition(caretPosition);

		if (p != null) {
			if (p.getType() == PacketTypeFactory.BOLS) {
				Debug.temporary(this, "caret is in a bol packet");
				if (p.getTextRefValue().contains(caretPosition)) {
					Debug.temporary(this, "new caret task");
					return new CaretGuessTask(textPane.getText(),textPane.getCaretPosition(), p);	
				}
			}
		}
		//update document to clear away past selections
		if (previouslyHighlightedPacket != null) {
			if (previouslyHighlightedPacket !=null)	previouslyHighlightedPacket.setHighlighted(false);
			previouslyHighlightedPacket = null;
			document.updateStylesLater(composition.getPackets());
		}
		

		return null;
	}

	public String getTaskName() {
		return "BolBase Searcher";
	}

	public class CaretGuessTask implements Runnable {

		String text;
		static final int RANGE = 20;
		Packet p;

		int caretPosition;
		//Packets packets;

		public CaretGuessTask (String text, int caretPosition, Packet packet) {
			//Debug.temporary(this, "caret task constructor");
			this.caretPosition = caretPosition;
			this.text = text;
			this.p = packet;
		}

		public void run() {
			Debug.temporary(this, "running caretguesstask");

			if (previouslyHighlightedPacket !=null) {
				previouslyHighlightedPacket.setHighlighted(false);
			}
			boolean highlightedAPacket = false;

			if (text != null) {
				//Debug.temporary(this, this.caretPosition +" in " + text.length()  +" ");
				if (caretPosition <= text.length()) {
					//Debug.temporary(this, "caret is IN the text");
					String input = Reader.determineBolStringAroundCaret(text, caretPosition);


					if (input != null) {
						Packets packets = composition.getPackets();
						int packetIndex = packets.indexOf(p);

						for (int i= packetIndex-1; i>0; i--) {
							if (packets.get(i).getKey().equalsIgnoreCase(input)) {
								packets.get(i).setHighlighted(true);
								highlightedAPacket = true;
								previouslyHighlightedPacket = packets.get(i);
								break;
							}
						}

						//no packet was matched, try to find a fitting bol
						if (highlightedAPacket == false) {

							//Debug.temporary(this, "bolstring around caret " + input);
							BolName bolName = BolBase.getStandard().getResemblingBol(input);
							if (bolName != null) {
								//Debug.temporary(this, "found bol: " + bolName);
								EventQueue.invokeLater(new SelectBolInBolBaseTableTask(bolName));
							}
						} //highlightedAPacket
					} //input != null 
				} //caretPos <= text.length
			} //text == null
			
			if (!highlightedAPacket) {
				if (previouslyHighlightedPacket !=null)	previouslyHighlightedPacket.setHighlighted(false);
			}
			document.updateStylesLater(composition.getPackets());
		}
	}

	/**
	 * This is the actual runnable that tells the bolBasePanel to select a bol
	 * it should only be run in the eventqueue, because it accesses gui elements.
	 * @author hannes
	 */
	private class SelectBolInBolBaseTableTask implements Runnable {
		private BolName bolName;

		SelectBolInBolBaseTableTask(BolName bolName) {
			this.bolName = bolName;
		}

		public void run() {
			bolBasePanel.selectBol(bolName);
		}
	}




}

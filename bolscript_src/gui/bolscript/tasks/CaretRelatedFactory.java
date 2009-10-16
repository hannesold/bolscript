package gui.bolscript.tasks;

import gui.bolscript.BolscriptDocument;
import gui.bolscript.composition.CompositionPanel;
import gui.bolscript.tables.BolBasePanel;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextPane;

import basics.Debug;
import bols.BolBase;
import bols.BolName;
import bolscript.compositions.Composition;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.scanner.Parser;

public class CaretRelatedFactory implements TaskFactory {

	JTextPane textPane;
	BolBasePanel bolBasePanel;
	Composition composition;
	CompositionPanel compPanel;
	
	BolscriptDocument document;
	
	int caretPosition;
	
	static Packet previouslyHighlightedReferencedPacket;

	public CaretRelatedFactory(Composition composition, JTextPane textPane, BolBasePanel bolBasePanel, BolscriptDocument document, CompositionPanel compPanel) {
		this.textPane = textPane;
		this.bolBasePanel = bolBasePanel;
		this.composition = composition;
		this.document = document;
		this.compPanel = compPanel;
	}

	public Runnable getNewTask() {

		caretPosition = textPane.getCaretPosition();
		
		/*try {
			EventQueue.invokeAndWait(new Runnable() {public void run() {
				caretPosition = textPane.getCaretPosition();
			}});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		document.setCaretPosition(caretPosition); //is this the right place?
		
		Packet p = composition.getPackets().getPacketAtCaretPosition(caretPosition);

		if (p != null) {
			if (p.getType() == PacketTypeFactory.BOLS) {
				Debug.temporary(this, "caret is in a bol packet");
				if (p.getTextRefValue().contains(caretPosition)) {
					Debug.temporary(this, "new caret task");
					return new ProcessBolCandidateAtCarretTask(textPane, textPane.getText(),textPane.getCaretPosition(), p);	
				}
			}
		}
		
		//update document to clear away past highlighting of referenced packets
		if (previouslyHighlightedReferencedPacket != null) {
			previouslyHighlightedReferencedPacket.setHighlighted(false);
			previouslyHighlightedReferencedPacket = null;
			document.updateStylesLater(composition.getPackets());
		}
		

		return new OnlyUpdateStyles();
	}

	public String getTaskName() {
		return "BolBase Searcher";
	}
	
	public void setCompositionPanel(CompositionPanel compositionPanel) {
		this.compPanel = compositionPanel;		
	}
	
	
	private final class OnlyUpdateStyles implements Runnable {

		@Override
		public void run() {
			document.updateStylesNow(composition.getPackets());
		}
		
	}
	private final class ProcessBolCandidateAtCarretTask implements Runnable {

		String text;
		static final int RANGE = 20;
		Packet packet;
		JTextPane textPane;
		
		int caretPosition;
		//Packets packets;

		public ProcessBolCandidateAtCarretTask (JTextPane textPane, String text, int caretPosition, Packet packet) {
			this.textPane = textPane;
			//Debug.temporary(this, "caret task constructor");
			this.caretPosition = caretPosition;
			this.text = text;
			this.packet = packet;
		}

		
		public void run() {
			Debug.temporary(this, "running caretguesstask");
			
			
			if (packet != null && compPanel!=null)  {
				if (compPanel.finishedRendering()) {
					EventQueue.invokeLater(new HighlightInCompositionPanel(packet));
				}
			}
			
			if (previouslyHighlightedReferencedPacket !=null) {
				previouslyHighlightedReferencedPacket.setHighlighted(false);
			}
			boolean matchedPacketReference = false;

			if (text != null) {
				//Debug.temporary(this, this.caretPosition +" in " + text.length()  +" ");
				if (caretPosition <= text.length()) {
					//Debug.temporary(this, "caret is IN the text");
					String input = Parser.determineBolStringAroundCaret(text, caretPosition);

					if (input != null) {
						Packets packets = composition.getPackets();
						int packetIndex = packets.indexOf(packet);

						for (int i= packetIndex-1; i>0; i--) {
							// attempt to match text around caret with referenced packet keys
							if (packets.get(i).getKey().equalsIgnoreCase(input)) {
								packets.get(i).setHighlighted(true);
								matchedPacketReference = true;
								previouslyHighlightedReferencedPacket = packets.get(i);
								break;
							}
						}

						//no packet was matched, try to find a fitting bol
						if (matchedPacketReference == false) {

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
			
			if (!matchedPacketReference) {
				if (previouslyHighlightedReferencedPacket !=null)	previouslyHighlightedReferencedPacket.setHighlighted(false);
			}
			
			document.updateStylesLater(composition.getPackets());
		
		}
		
		private class HighlightInCompositionPanel implements Runnable {
			Packet p;
			public HighlightInCompositionPanel(Packet p) {
				this.p = p;
			}
			public void run() {
				Debug.temporary(this,"attempting to highlight packet " + p.getKey());
				if (compPanel != null) {
					compPanel.highlightPacketNow(p);
				} else Debug.temporary(this,"compPanel was null");
			}
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

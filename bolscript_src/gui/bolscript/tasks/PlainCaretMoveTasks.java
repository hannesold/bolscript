package gui.bolscript.tasks;

import gui.bolscript.BolscriptDocument;
import gui.bolscript.composition.CompositionPanel;
import gui.bolscript.tables.BolBasePanel;
import gui.bolscript.tasks.Task.TaskException;

import java.util.ArrayList;

import basics.Debug;
import bols.Bol;
import bols.BolBase;
import bols.BolName;
import bolscript.compositions.Composition;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.sequences.ReferencedBolPacketUnit;
import bolscript.sequences.Representable;

public class PlainCaretMoveTasks extends TaskList {

	private Composition comp;
	private CompositionPanel compPanel;
	private BolBasePanel bolBasePanel;
	private BolscriptDocument document;
	private String text;
	private int caretPosition;
	protected Packet packetAtCaretPosition;
	protected Representable unitAtCaretPosition;
	protected Packet highlightedReferencedPacket;
	
	public PlainCaretMoveTasks(Composition comp, CompositionPanel compPanel,
			BolBasePanel bolBasePanel, BolscriptDocument document, int caretPosition) {
		super();
		this.comp = comp;
		this.compPanel = compPanel;
		this.bolBasePanel = bolBasePanel;
		this.document = document;
		this.caretPosition = caretPosition;
		tasks = new ArrayList<Task>();
		init();
	}
	
	private void init () {
		tasks.add(
				new Task("DeterminePacketAndUnitAtCaret", Task.ExecutionThread.AnyThread) {
					@Override
					public void doTask() throws TaskException {
						highlightedReferencedPacket = null;
						
						Packets packets = comp.getEditorPackets();
						packetAtCaretPosition = packets.getPacketAtCaretPosition(caretPosition);
						
						if (packetAtCaretPosition != null) {
							unitAtCaretPosition = packetAtCaretPosition.getUnitAtCaretPosition(caretPosition);
							Debug.temporary(this, "unit at caret position determined: "+ unitAtCaretPosition);
							if (unitAtCaretPosition !=null) {
								if (unitAtCaretPosition.getType() == Representable.REFERENCED_BOL_PACKET) {
									highlightedReferencedPacket = ((ReferencedBolPacketUnit) unitAtCaretPosition).getReferencedPacket();
								}
							}
						} else {
							unitAtCaretPosition = null;
						}
					}
				});
		tasks.add(
				new Task("UpdateDocumentStyles", Task.ExecutionThread.EventQueue) {
					@Override
					public void doTask() throws TaskException {
						document.setCaretPosition(caretPosition);
						document.setHighlightedReferencedPacket(highlightedReferencedPacket);
						document.updateStylesNow(comp.getEditorPackets());
					}
				});
		tasks.add(
				new Task("UpdateBolBasePanel", Task.ExecutionThread.EventQueue) {
					@Override
					public void doTask() throws TaskException {
						if (unitAtCaretPosition != null) {
							if (unitAtCaretPosition.getType() == Representable.BOL) {
								Bol bol = (Bol) unitAtCaretPosition;
								BolName bolName = BolBase.getStandard().getResemblingBol(bol.getBolName().getName(BolName.EXACT));
								if (bolName != null) bolBasePanel.selectBol(bolName);								
							}
						}
					}
				});
		
		tasks.add(
				new Task("UpdateCompPanelByHighlightingAndScrolling", Task.ExecutionThread.EventQueue) {

					@Override
					public void doTask() throws TaskException {
						compPanel.highlightPacketNow(packetAtCaretPosition);
					}
					
				});


	}

	@Override
	public boolean overridesPending(TaskList taskList) {
		return (taskList.getClass() == this.getClass());
	}


	
	
}

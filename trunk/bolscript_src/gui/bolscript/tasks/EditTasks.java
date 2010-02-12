package gui.bolscript.tasks;

import gui.bolscript.BolscriptDocument;
import gui.bolscript.composition.CompositionPanel;
import gui.bolscript.tables.BolBasePanel;
import gui.bolscript.tasks.Task.TaskException;

import java.util.ArrayList;

import bols.Bol;
import bols.BolBase;
import bols.BolName;
import bolscript.compositions.Composition;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.sequences.ReferencedBolPacketUnit;
import bolscript.sequences.Representable;

public class EditTasks extends TaskList {

	private Composition comp;
	private CompositionPanel compPanel;
	private BolBasePanel bolBasePanel;
	private BolscriptDocument document;
	private String text;
	private int caretPosition;
	protected Packet packetAtCaretPosition;
	protected Representable unitAtCaretPosition;
	protected Packet highlightedReferencedPacket;
	
	
	public EditTasks(Composition comp, CompositionPanel compPanel,
			BolBasePanel bolBasePanel, BolscriptDocument document, String text,
			int caretPosition) {
		super();
		this.comp = comp;
		this.compPanel = compPanel;
		this.bolBasePanel = bolBasePanel;
		this.document = document;
		this.text = text;
		this.caretPosition = caretPosition;
		tasks = new ArrayList<Task>();
		init();
	}


	private void init() {
		tasks.add(
		new Task("UpdateComposition", Task.ExecutionThread.AnyThread) {
			@Override
			public void doTask() throws TaskException {
				comp.setEditableRawData(text);
				comp.extractInfoFromEditableRawData();		
			}
		});
		tasks.add(
				new Task("DeterminePacketAndUnitAtCaret", Task.ExecutionThread.AnyThread) {
					@Override
					public void doTask() throws TaskException {
						highlightedReferencedPacket = null;
						
						Packets packets = comp.getEditorPackets();
						packetAtCaretPosition = packets.getPacketAtCaretPosition(caretPosition);
						
						if (packetAtCaretPosition != null) {
							unitAtCaretPosition = packetAtCaretPosition.getUnitAtCaretPosition(caretPosition);
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
				new Task("PrepareRendering", Task.ExecutionThread.AnyThread) {
					@Override
					public void doTask() throws TaskException {
						compPanel.setHighlightedPaket(packetAtCaretPosition);
						compPanel.renderComposition(comp, true);
					}
				});
		tasks.add(
				new Task("FinishRendering", Task.ExecutionThread.EventQueue) {
					@Override
					public void doTask() throws TaskException {
						compPanel.renderNowAfterPreperationg();
					}
				});
	}


	@Override
	public boolean overridesPending(TaskList taskList) {
		return true;
	}
	
	
}

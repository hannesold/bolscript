package gui.bolscript.tasks;

import java.awt.EventQueue;

import gui.bolscript.EditorFrame;
import gui.bolscript.composition.CompositionPanel;
import bolscript.compositions.Composition;

public class RendererFactory implements TaskFactory {

	private EditorFrame editor;
	private TaskFactory appendedTaskFactory;
	
	
	public RendererFactory (EditorFrame editor, TaskFactory appendedTaskFactory) {
		this.editor = editor;
		this.appendedTaskFactory = appendedTaskFactory;
	}
	
	public String getTaskName() {
		return "Composition Renderer";
	}

	public Runnable getNewTask() {
		return new UpdateRawData(editor.getComposition(), editor.getCompositionPanel(), editor.getText(), editor.getCaretPosition());
	}
	
	private class UpdateRawData implements Runnable {
		private String text;
		private Composition comp;
		private CompositionPanel compPanel;
		private int caretPosition;
		
		public UpdateRawData (Composition comp, CompositionPanel compPanel, String s, int caretPosition) {
			this.text = s;
			this.comp = comp;
			this.compPanel = compPanel;
			this.caretPosition = caretPosition;
		}
		
		public void run() {
			comp.setRawData(text);
			comp.extractInfoFromRawData();
			
			Runnable appendedTask = appendedTaskFactory.getNewTask();
			if (appendedTask != null) {
				EventQueue.invokeLater(appendedTask);
			} 
			
			compPanel.setHighlightedPaket(comp.getPackets().getPacketAtCaretPosition(caretPosition));
			compPanel.renderComposition(comp);
			
			
			//if (appendedCaretRelatedWorker != null) appendedCaretRelatedWorker.addUpdate();
			
		}
	}
}

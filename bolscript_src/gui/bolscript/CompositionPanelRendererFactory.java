package gui.bolscript;

import bolscript.compositions.Composition;

public class CompositionPanelRendererFactory implements TaskFactory {

	private EditorFrame editor;
	
	private SkippingWorker alsoAddUpdateTo;
	
	public CompositionPanelRendererFactory (EditorFrame editor, SkippingWorker alsoAddUpdateTo) {
		this.editor = editor;
		this.alsoAddUpdateTo = alsoAddUpdateTo;
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
			compPanel.setHighlightedPaket(comp.getPackets().getPacketAtCaretPosition(caretPosition));
			compPanel.renderComposition(comp);
			
			if (alsoAddUpdateTo != null) alsoAddUpdateTo.addUpdate();	
			//alsoAddUpdateTo.
			//editor.getDocument().updateStylesLater(comp.getPackets());
		}
	}
}

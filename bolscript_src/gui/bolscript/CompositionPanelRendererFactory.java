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
		return new Task(editor.getComposition(), editor.getCompositionPanel(), editor.getText());
	}
	
	private class Task implements Runnable {
		private String text;
		private Composition comp;
		private CompositionPanel compPanel;
		
		public Task (Composition comp, CompositionPanel compPanel, String s) {
			this.text = s;
			this.comp = comp;
			this.compPanel = compPanel;
		}
		
		public void run() {
			comp.setRawData(text);
			comp.extractInfoFromRawData();
			compPanel.renderComposition(comp);
			
			if (alsoAddUpdateTo != null) alsoAddUpdateTo.addUpdate();		
			//editor.getDocument().updateStylesLater(comp.getPackets());
		}
	}
}

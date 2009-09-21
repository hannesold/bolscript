package gui.bolscript;

import java.awt.EventQueue;

import gui.bolscript.tables.BolBasePanel;

import javax.swing.JTextPane;

import basics.Debug;
import bols.BolBase;
import bols.BolName;

public class BolBaseSearcher implements TaskFactory {

	JTextPane textPane;
	BolBasePanel bolBasePanel;
	
	public BolBaseSearcher(JTextPane textPane, BolBasePanel bolBasePanel) {
		this.textPane = textPane;
		this.bolBasePanel = bolBasePanel;
	}
	
	public Runnable getNewTask() {
		return new Task(textPane.getSelectedText());
	}

	public String getTaskName() {
		return "BolBase Searcher";
	}
	
	private class Task implements Runnable{

		String selection;
		public Task(String selection) {
			this.selection = selection;
		}
		public void run() {
			Debug.temporary(this, "selected text "+ selection);
			BolName bolName = BolBase.getStandard().getBolName(selection);
			if (bolName != null) {
				Debug.temporary(this, "found bol: " + bolName);
				EventQueue.invokeLater(new SelectTask(bolName));
			}
			
		}	
		
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
	

	

}

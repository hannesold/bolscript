package gui.bolscript;

import gui.bolscript.actions.CloseEditor;
import gui.bolscript.composition.CompositionPanel;
import gui.bolscript.dialogs.SaveChangesDialog;
import gui.bolscript.tables.BolBasePanel;
import gui.bolscript.tasks.CaretRelatedFactory;
import gui.bolscript.tasks.EditTasks;
import gui.bolscript.tasks.ListWorker;
import gui.bolscript.tasks.PlainCaretMoveTasks;
import gui.bolscript.tasks.RendererFactory;
import gui.bolscript.tasks.SkippingWorker;
import gui.menus.EditMenu;
import gui.menus.FileMenu;
import gui.menus.LanguageMenu;
import gui.menus.ViewMenu;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

import basics.Debug;
import basics.GUI;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;

public class EditorFrame extends JFrame implements WindowListener, CompositionChangedListener, DocumentListener, CaretListener {

	CompositionPanel compositionPanel;
	CompositionFrame compositionFrame;
	private Composition composition;

	public SaveChangesDialog saveChangesDialog;

	public JMenuBar menuBar;

	String lastVersion = null;

	public JTextPane textPane;
	public JButton buttonCompile;

	SkippingWorker renderWorker;
	//SkippingWorker bolBaseSearchWorker;

	final UndoManager undoManager;
	private BolscriptDocument document;

	private int lastCaretPosition = -1;
	private BolBasePanel bolBasePanel;

	private CaretRelatedFactory bolBaseSearcher;


	private ListWorker listWorker;
	
	/**
	 * Is hidden by default. Call showLater().
	 * @param comp
	 * @param size
	 */
	public EditorFrame(Composition comp, Dimension size) {
		super(comp.getName());
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(size);
		composition = comp;
		this.setVisible(false);
		
		

		//init textPane
		document = new BolscriptDocument();
		textPane = new JTextPane(document);
		textPane.setText(comp.getRawData());
		textPane.setCaretPosition(0);
		JScrollPane scrollpane = new JScrollPane(textPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		scrollpane.setMinimumSize(new Dimension(200, 150));

		//init bolbase panel
		bolBasePanel = new BolBasePanel();
		
		//init the editorPanel
		JSplitPane editorPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		editorPanel.setLeftComponent(scrollpane);
		editorPanel.setRightComponent(bolBasePanel);
		editorPanel.setDividerLocation((int)Math.floor(((double) size.height) * 0.66));
		this.setContentPane(editorPanel);

		//init the menubar
		initMenuBar();

		//this.setText(comp.getRawData());
		undoManager = new UndoManager();
		document.addUndoableEditListener(undoManager); 

		bolBaseSearcher = new CaretRelatedFactory(composition, textPane, bolBasePanel, document, null);
		renderWorker = new SkippingWorker(new RendererFactory(this, bolBaseSearcher), 10, false);
		renderWorker.begin();

		listWorker = new ListWorker(100, true);
		listWorker.begin();
		
		//add listeners
		document.addDocumentListener(this);
		textPane.addCaretListener(this);
		composition.addChangeListener(this);
		addWindowListener(this);
		
		compositionPanel = null;
		
		document.updateStylesLater(comp.getPackets());
		

	}


	public BolscriptDocument getDocument() {
		return document;
	}


	public JMenuBar initMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(new FileMenu(this));
		menuBar.add(new EditMenu(this));
		menuBar.add(new ViewMenu(this));
		menuBar.add(new LanguageMenu(this));

		this.setJMenuBar(menuBar);
		return menuBar;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public CompositionPanel getCompositionPanel() {
		return compositionPanel;
	}


	public void setCompositionPanel(CompositionPanel compositionPanel) {
		this.compositionPanel = compositionPanel;
		bolBaseSearcher.setCompositionPanel(compositionPanel);
	}

	public int getCaretPosition() {
		return lastCaretPosition;
	}

	public void setText(String contents) {
		textPane.setText(contents);
	}

	public String getText() {
		return textPane.getText();
	}

	public Composition getComposition() {
		return composition;
	}

	public void setComposition(Composition composition) {
		this.composition = composition;
		composition.addChangeListener(this);
	}

	public CompositionFrame getCompositionFrame() {
		return compositionFrame;
	}

	/**
	 * Sets the compositionFrame. Also adds it as a ChangeListener to the composition.
	 * @param compositionFrame
	 */
	public void setCompositionFrame(CompositionFrame compositionFrame) {
		this.compositionFrame = compositionFrame;
		this.setCompositionPanel(compositionFrame.getCompositionPanel());

		renderWorker.addUpdate();
		//bolBaseSearchWorker.addUpdate();
		compositionFrame.addComponentListener(GUI.proxyComponentResizedListener(renderWorker,"compFrameResized"));
		composition.addChangeListener(compositionFrame);


	}

	public void arrangeCompositionFrame() {
		Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
		int right = screenSize.width;
		compositionFrame.setLocation(right - compositionFrame.getWidth(), this.getLocation().y);
		this.setLocation(right - compositionFrame.getWidth() - this.getWidth(), this.getLocation().y);

	}


	public void compositionChanged(CompositionChangeEvent compositionChangeEvent) {
		this.setTitle(compositionChangeEvent.getComposition().getName());
	}

	public void setTitle(String name) {
		super.setTitle("Composition Editor - " + name);
	}


	public void changedUpdate(DocumentEvent e) {
		if (!textPane.getText().equals(lastVersion)){
			compile();
			lastVersion = textPane.getText();
		}

	}

	public void insertUpdate(DocumentEvent e) {
		if (!textPane.getText().equals(lastVersion)){
			compile();
			lastVersion = textPane.getText();
		}
	}

	public void removeUpdate(DocumentEvent e) {
		if (!textPane.getText().equals(lastVersion)){
			compile();
			lastVersion = textPane.getText();
		}	
	}

	/**
	 * Is called when the text is changed.
	 * 
	 * @param e
	 */
	public void compile() {
		if (compositionPanel != null) {

			lastCaretPosition = textPane.getCaretPosition();
			EditTasks editTasks = new EditTasks(composition, compositionPanel, bolBasePanel, document, textPane.getText(), lastCaretPosition);
			listWorker.addTaskList(editTasks);
			//renderWorker.addUpdate();
			//	bolBaseSearchWorker.addUpdate();
			
		}

	}

	public void caretUpdate(CaretEvent e) {
		if (textPane.getCaretPosition() != lastCaretPosition) {
			
			//if (bolBaseSearchWorker != null) bolBaseSearchWorker.addUpdate();
			/*	if (renderWorker!= null) {
				if (!renderWorker.hasWork()) {
					//the bolbasesearcher is only used if no rendertask is running,
					//else it will automatically be run after the rendertask
					if (bolBaseSearcher != null) {
						 Runnable task = bolBaseSearcher.getNewTask();
						if (task != null) task.run();
					}	
				}
			}*/
			lastCaretPosition = textPane.getCaretPosition();
			PlainCaretMoveTasks caretTasks = new PlainCaretMoveTasks(composition, compositionPanel, bolBasePanel, document,lastCaretPosition);
			listWorker.addTaskList(caretTasks);
		}
	}


	public void windowClosing(WindowEvent e) {
		Debug.debug(this, "window closed: " + e);
		new CloseEditor(this).closeEditor();
	}

	public void dispose() {
		composition.removeChangeListener(this);
		if (renderWorker != null) renderWorker.stop();
		super.dispose();
	}

	public void windowActivated(WindowEvent e) {}

	public void windowClosed(WindowEvent e) {

	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}


	public void showLater() {
		EventQueue.invokeLater(new Runnable() {
			public void run(){
				setVisible(true);
			}
		});
		
	}






}

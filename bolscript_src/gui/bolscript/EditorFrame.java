package gui.bolscript;

import gui.bolscript.actions.CloseEditor;
import gui.bolscript.composition.CompositionPanel;
import gui.bolscript.dialogs.SaveChangesDialog;
import gui.bolscript.sequences.UnitPanelListener;
import gui.bolscript.tables.BolBasePanel;
import gui.bolscript.tasks.EditTasks;
import gui.bolscript.tasks.ListWorker;
import gui.bolscript.tasks.PlainCaretMoveTasks;
import gui.menus.EditMenu;
import gui.menus.FileMenu;
import gui.menus.ViewMenu;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;
import bolscript.packets.Packet;
import bolscript.sequences.Representable;

public class EditorFrame extends JFrame implements WindowListener, CompositionChangedListener, DocumentListener, CaretListener, ComponentListener, UnitPanelListener {

	CompositionPanel compositionPanel;
	CompositionFrame compositionFrame;
	private Composition composition;

	public SaveChangesDialog saveChangesDialog;

	public JMenuBar menuBar;

	String lastVersion = null;

	public JTextPane textPane;
	public JButton buttonCompile;

	final UndoManager undoManager;
	private BolscriptDocument document;

	private int lastCaretPosition = -1;
	private BolBasePanel bolBasePanel;

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
		textPane.setText(comp.getEditableRawData());
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

		undoManager = new UndoManager();
		document.addUndoableEditListener(undoManager); 

		//init worker
		listWorker = new ListWorker(100, true);
		listWorker.begin();
		
		//add listeners
		document.addDocumentListener(this);
		textPane.addCaretListener(this);
		composition.addChangeListener(this);
		addWindowListener(this);
		
		compositionPanel = null;
		
		document.updateStylesLater(comp.getEditorPackets());
		
	}

	public BolscriptDocument getDocument() {
		return document;
	}

	public JMenuBar initMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(new FileMenu(this));
		menuBar.add(new EditMenu(this));
		menuBar.add(new ViewMenu(this));

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

		compositionFrame.addComponentListener(this);
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


	@Override
	public void changedUpdate(DocumentEvent e) {
		processEdit();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		processEdit();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		processEdit();
	}

	private void processEdit() {
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
		}
	}
	
	@Override
	public void unitClickedInSequencePanel(Representable r, Packet containingPacket) {
		Debug.temporary(this, r +" clicked in Packet " + containingPacket);
		
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (textPane.getCaretPosition() != lastCaretPosition) {
			lastCaretPosition = textPane.getCaretPosition();
			PlainCaretMoveTasks caretTasks = new PlainCaretMoveTasks(composition, compositionPanel, bolBasePanel, document,lastCaretPosition);
			listWorker.addTaskList(caretTasks);
		}
	}

	public void windowClosing(WindowEvent e) {
		Debug.debug(this, "window closing: " + e);
		new CloseEditor(this).closeEditor();
	}

	@Override
	public void dispose() {
		composition.removeChangeListener(this);
		composition.removeChangeListener(compositionFrame);
		if (listWorker != null) listWorker.stop();
		super.dispose();
	}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
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

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getComponent() == this.compositionFrame) {
			compile();
		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
	}

	






}

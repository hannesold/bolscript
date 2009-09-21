package gui.bolscript;

import gui.bolscript.actions.CloseEditor;
import gui.bolscript.dialogs.SaveChangesDialog;
import gui.bolscript.tables.BolBasePanel;
import gui.menus.EditMenu;
import gui.menus.FileMenu;
import gui.menus.LanguageMenu;
import gui.menus.ViewMenu;

import java.awt.Dimension;
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
	SkippingWorker bolBaseSearchWorker;
	
	final UndoManager undoManager;
	private BolscriptDocument document;
	
	private BolBasePanel bolBasePanel;
	
	private EditorFrame(Dimension size) {
		super("Composition editor");
		undoManager = new UndoManager();
		this.setSize(size);
		
		compositionPanel = null;
		
		//editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
		document = new BolscriptDocument();
		textPane = new JTextPane(document);
		
		JScrollPane scrollpane = new JScrollPane(textPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		//scrollpane.setPreferredSize(new Dimension(size.width, size.height-50));
		scrollpane.setMinimumSize(new Dimension(200, 150));
		
		
		bolBasePanel = new BolBasePanel();
		//bolBasePanel.setPreferredSize(new Dimension(size.width, 50));
		
		JSplitPane editorPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		editorPanel.setLeftComponent(scrollpane);
		editorPanel.setRightComponent(bolBasePanel);
		editorPanel.setDividerLocation((int)Math.floor(((double) size.height) * 0.66));
		
		this.setContentPane(editorPanel);
		
		initMenuBar();
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		document.addDocumentListener(this);
		textPane.addCaretListener(this);
		
//		document.addDocumentListener(bolBasePanel);
		addWindowListener(this);	
	}
	
	public EditorFrame(Composition comp, Dimension dimension) {
		this(dimension);
		this.setTitle(comp.getName());
		composition = comp;
		composition.addChangeListener(this);
		this.setText(comp.getRawData());
		
		document.addUndoableEditListener(undoManager); 
		renderWorker = new SkippingWorker(new CompositionPanelRendererFactory(this));
		renderWorker.begin();
		bolBaseSearchWorker = new SkippingWorker(new BolBaseSearcher(textPane, bolBasePanel));
		bolBaseSearchWorker.begin();
		document.updateStyles(composition.getPackets());
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
		bolBaseSearchWorker.addUpdate();
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
			renderWorker.addUpdate();
			bolBaseSearchWorker.addUpdate();
		}
		
	}
	
	public void caretUpdate(CaretEvent e) {
		if (bolBaseSearchWorker != null) bolBaseSearchWorker.addUpdate();
	}


	public void windowClosing(WindowEvent e) {
		Debug.debug(this, "window closed: " + e);
		new CloseEditor(this).closeEditor();
	}
	
	public void dispose() {
		composition.removeChangeListener(this);
		if (renderWorker != null) renderWorker.stop();
		if (bolBaseSearchWorker != null) bolBaseSearchWorker.stop();
		super.dispose();
	}
	
	public void windowActivated(WindowEvent e) {}
	
	public void windowClosed(WindowEvent e) {

	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	

	


}

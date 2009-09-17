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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

import basics.Debug;
import basics.GUI;
import bolscript.compositions.Composition;
import bolscript.compositions.CompositionChangeEvent;
import bolscript.compositions.CompositionChangedListener;

public class EditorFrame extends JFrame implements WindowListener, CompositionChangedListener, DocumentListener {
	
	CompositionPanel compositionPanel;
	CompositionFrame compositionFrame;
	private Composition composition;
	
	public SaveChangesDialog saveChangesDialog;
	
	public JMenuBar menuBar;
	
	String lastVersion = null;
	
	public JTextPane textPane;
	public JButton buttonCompile;
	RenderWorker renderer;
	
	final UndoManager undoManager;
	private BolscriptDocument document;
	
	private BolBasePanel bolBasePanel;
	
	public EditorFrame(Dimension size) {
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
		addWindowListener(this);	
	}

	public BolscriptDocument getDocument() {
		return document;
	}

	public EditorFrame(Composition comp, Dimension dimension) {
		this(dimension);
		this.setTitle(comp.getName());
		composition = comp;
		composition.addChangeListener(this);
		this.setText(comp.getRawData());
		
		document.addUndoableEditListener(undoManager); 
		renderer = new RenderWorker(this);
		renderer.begin();
		document.updateStyles(composition.getPackets());
		
	}
	
	public JMenuBar initMenuBar() {
		//if (menuBar == null) {
			menuBar = new JMenuBar();
			/*JMenu menuFile = new JMenu("File");
			JMenuItem saver = new JMenuItem(new SaveChanges(this,false));
			saver.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			JMenuItem saveAs = new JMenuItem(new SaveAs(this));
			saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
					(java.awt.event.InputEvent.SHIFT_MASK | 
							(Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))));
			JMenuItem closer = new JMenuItem(new CloseEditor(this));
			closer.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

			menuFile.add(new OpenNew());
			menuFile.add(saver);
			menuFile.add(saveAs);
			menuFile.add(closer);
			menuBar.add(menuFile);
			
			JMenu menuEdit = new JMenu("Edit");
			JMenuItem undo = new JMenuItem(new Undo(this.undoManager));
			undo.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			
			JMenuItem redo = new JMenuItem(new Redo(this.undoManager));
			redo.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuEdit.add(undo);
			menuEdit.add(redo);
			
			menuBar.add(menuEdit);*/
			
			menuBar.add(new FileMenu(this));
			menuBar.add(new EditMenu(this));
			menuBar.add(new ViewMenu(this));
			menuBar.add(new LanguageMenu(this));

			this.setJMenuBar(menuBar);
		//
			
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
		
		renderer.addUpdate();
		compositionFrame.addComponentListener(GUI.proxyComponentResizedListener(renderer,"compFrameResized"));
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
	
	public void windowClosing(WindowEvent e) {
		Debug.debug(this, "window closed: " + e);
		new CloseEditor(this).closeEditor();
	}
	
	public void dispose() {
		composition.removeChangeListener(this);
		if (renderer != null) renderer.stop();
		super.dispose();
	}
	
	public void windowActivated(WindowEvent e) {}
	
	public void windowClosed(WindowEvent e) {

	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

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
			renderer.addUpdate();
		}
		
	}




}
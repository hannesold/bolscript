package gui.bolscript;

import gui.bolscript.actions.OpenNew;
import gui.bolscript.tables.CompositionListPanel;
import gui.bolscript.tables.CompositionTableModel;
import gui.menus.EditMenu;
import gui.menus.FileMenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import bolscript.Master;

public class BrowserFrame extends JFrame implements WindowListener{
	private CompositionListPanel compositionListPanel;
	private FilterPanel filterPanel;
	private SearchPanel searchPanel;
	
	//private CompositionTableModel tableModel;
	public BrowserFrame(Dimension size, CompositionTableModel model, FilterPanel filterPanel) {
		super("Bolscript Browser");
	
		

		
		this.filterPanel = filterPanel;
		this.searchPanel = filterPanel.getSearchPanel();

		JPanel headPanel = new JPanel();
		headPanel.setLayout(new BorderLayout());
		headPanel.add(searchPanel, BorderLayout.NORTH);
		headPanel.add(filterPanel, BorderLayout.CENTER);
		filterPanel.setMinimumSize(new Dimension(200,130));
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		compositionListPanel = new CompositionListPanel(model);
		lowerPanel.add(compositionListPanel,BorderLayout.CENTER);
		JButton addButton = new JButton(new OpenNew());
		lowerPanel.add(addButton,BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(headPanel);
		splitPane.setRightComponent(lowerPanel);
		this.setContentPane(splitPane);
		
		initMenuBar();
		this.pack();
		this.setSize(size);
		this.addWindowListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		
		
	}
	
	
	public CompositionListPanel getCompositionListPanel() {
		return compositionListPanel;
	}


	public void initMenuBar() {
/*
		JMenu filemenu = new JMenu("File");
		filemenu.add(new OpenNew());
		filemenu.addSeparator();
		filemenu.add(new RefreshFromTablafolder());
		
		if (!Master.master.isRunningAsMacApplication()) {
			filemenu.add(new OpenPreferences());		
			filemenu.addSeparator();
			filemenu.add(new ExitProgram());
		}
		
		JMenu editmenu = new JMenu("Edit");
		JMenuItem remover = new JMenuItem(new RemoveSelected(this));
		
		int remKey = (Config.OS == Config.MAC) ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;
		remover.setAccelerator(KeyStroke.getKeyStroke(
		        remKey, Config.MENU_SHORTKEY_MASK));
		
		editmenu.add(remover);
		*/
		JMenuBar menubar = new JMenuBar();		
		menubar.add(new FileMenu(this));
		menubar.add(new EditMenu(this));
		
		this.setJMenuBar(menubar);
	}
	public void windowClosing(WindowEvent e) {
		Master.master.exit();
	}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}

package gui.bolscript;

import gui.bolscript.actions.OpenPreferences;
import gui.bolscript.actions.ExitProgram;
import gui.bolscript.actions.OpenNew;
import gui.bolscript.actions.RefreshFromTablafolder;
import gui.bolscript.actions.RemoveSelected;
import gui.bolscript.tables.CompositionTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import bolscript.Master;
import config.Config;

public class BrowserFrame extends JFrame implements WindowListener{
	private CompositionListPanel compositionListPanel;
	private FilterPanel filterPanel;
	private SearchPanel searchPanel;
	
	//private CompositionTableModel tableModel;
	public BrowserFrame(Dimension size, CompositionTableModel model, FilterPanel filterPanel, SearchPanel searchPanel) {
		super("Bolscript Browser");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		//this.tableModel = model;
		compositionListPanel = new CompositionListPanel(model);
		
		this.filterPanel = filterPanel;
		this.searchPanel = searchPanel;
		
		panel.add(searchPanel,BorderLayout.NORTH);
		JPanel headPanel = new JPanel();
		headPanel.setLayout(new BorderLayout());
		
		headPanel.add(searchPanel, BorderLayout.NORTH);
		headPanel.add(filterPanel, BorderLayout.CENTER);
		
		panel.add(headPanel,BorderLayout.NORTH);
		panel.add(compositionListPanel,BorderLayout.CENTER);
		JButton addButton = new JButton(new OpenNew());
		panel.add(addButton,BorderLayout.SOUTH);
		
		
		this.setContentPane(panel);
		//this.pack();
		initMenu();
		this.pack();
		this.addWindowListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		
		
	}
	public void initMenu() {

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
		JMenuItem remover = new JMenuItem(new RemoveSelected(this, compositionListPanel.getCompositionTable()));
		
		int remKey = (Config.OS == Config.MAC) ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;
		remover.setAccelerator(KeyStroke.getKeyStroke(
		        remKey, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		editmenu.add(remover);
		
		JMenuBar menubar = new JMenuBar();		
		menubar.add(filemenu);
		menubar.add(editmenu);
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

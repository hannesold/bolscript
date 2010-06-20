package gui.bolscript;

import gui.bolscript.actions.OpenNew;
import gui.bolscript.tables.CompositionListPanel;
import gui.bolscript.tables.CompositionTableModel;
import gui.menus.EditMenu;
import gui.menus.FileMenu;
import gui.menus.MenuUpdater;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import bolscript.Master;
import bolscript.config.Config;
import bolscript.config.GuiConfig;

public class BrowserFrame extends JFrame implements WindowListener, DropTargetListener{
	private CompositionListPanel compositionListPanel;
	private FilterPanel filterPanel;
	private SearchPanel searchPanel;
	private MenuUpdater menuUpdater;
	
	private DropTarget dropTarget;
	
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
		
		if (dropTarget == null) {
            dropTarget = new DropTarget(this, this);
        }
		
		initMenuBar();
		this.pack();
		this.setSize(size);
		this.addWindowListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		
		
	}
	
	
	public CompositionListPanel getCompositionListPanel() {
		return compositionListPanel;
	}


	private void initMenuBar() {

		JMenuBar menubar = new JMenuBar();		
		menubar.add(new FileMenu(this));
		menubar.add(new EditMenu(this));
		
		
		this.setJMenuBar(menubar);
		
		initMenuUpdater();
	}
	
	private void initMenuUpdater() {
		menuUpdater = new MenuUpdater(this.getJMenuBar());
		compositionListPanel.getCompositionTable().getSelectionModel().addListSelectionListener(new ListSelectionListener()  {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				menuUpdater.updateEnabling();
			}
		});
		
		compositionListPanel.getTableModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				menuUpdater.updateEnabling();	
			}
		});
		
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


	@Override
	public void dragEnter(DropTargetDragEvent evt) {
		this.compositionListPanel.setBorder(new LineBorder(GuiConfig.sequencePanelHighlightColor, 5));
		
		
	}


	@Override
	public void dragExit(DropTargetEvent dte) {
		this.compositionListPanel.setBorder(null);
		
	}


	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		
	}


	@Override
	public void drop(DropTargetDropEvent evt) {
		 List<File> potentialBolscriptFiles = new ArrayList<File>();
		 int action = evt.getDropAction(); 
		 evt.acceptDrop(action);
		 
        try {
            Transferable data = evt.getTransferable();
            DataFlavor flavors[] = data.getTransferDataFlavors();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> list = (List<File>) data.getTransferData(
                    DataFlavor.javaFileListFlavor);
               
                for (File file : list) {
                	if (file.getName().toLowerCase().endsWith(Config.bolscriptSuffix)) {
                		potentialBolscriptFiles.add(file);
                	}
                }
                if (potentialBolscriptFiles.size()>0) {                	
                	Master.master.openSomeExistingFiles(potentialBolscriptFiles);
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if (potentialBolscriptFiles.size()>0) {
        		evt.dropComplete(true);
        	} else {
        		evt.dropComplete(false);
        	}        	
            //repaint();
        }
        this.compositionListPanel.setBorder(null);
		
	}


	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}
}

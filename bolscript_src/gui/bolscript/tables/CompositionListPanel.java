package gui.bolscript.tables;


import gui.bolscript.actions.OpenComposition;
import gui.bolscript.actions.RemoveSelected;
import gui.bolscript.actions.RevealCompositionInOSFileManager;
import gui.bolscript.composition.CompositionDataFlavor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import javax.swing.table.TableRowSorter;

import basics.GUI;
import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.config.Config;
import bolscript.config.GuiConfig;

public class CompositionListPanel extends JScrollPane  {

	private class CompositionListTransferHandler extends TransferHandler {

		
		CompositionListPanel panel;
		CompositionListTransferHandler(CompositionListPanel panel) {
			this.panel = panel;
		}
		
		/**
		 * To bad, this is not working...
		 */
        @Override
		public Icon getVisualRepresentation(Transferable t) {
			List<Composition> comps = panel.getSelectedCompositions();
			if (comps.size()==1) {
				JLabel label = new JLabel(comps.get(0).getName());
				JPanel p = new JPanel();
				p.add(label);

				ImageIcon icon = new ImageIcon();				
				BufferedImage bi = new BufferedImage( p.getPreferredSize().width, p.getPreferredSize().height, BufferedImage.TYPE_INT_RGB );
				icon.setImage(bi);
				Graphics2D g2d = bi.createGraphics();
				p.paint(g2d);				
				return icon;
			}
			return super.getVisualRepresentation(t);
		}

		@Override
        protected Transferable createTransferable(JComponent c) {
            if (JTable.class.isInstance(c)) {
            	JTable table = (JTable) c;
            	List<Composition> selectedComps = panel.getSelectedCompositions();
            	
                return new CompositionListTransferable(selectedComps);            	
            } else return null;            
        }

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY;
        }
    }
	/*boolean isDragging = false;
	public void setIsDragging(boolean yesno) {
		isDragging = yesno;
	}
	public boolean IsDragging() {
		return isDragging;
	}*/

    private class CompositionListTransferable implements Transferable {

        private List<Composition> comps;
        private ArrayList<DataFlavor> availableFlavors;
        private DataFlavor plainTextFlavor;
        
        public CompositionListTransferable(List<Composition> comps) {
            this.comps = comps;
            this.availableFlavors = new ArrayList<DataFlavor>();
            plainTextFlavor = DataFlavor.getTextPlainUnicodeFlavor();
            
            String plainTextMime = plainTextFlavor.getMimeType();
            
            if (comps.size()==0) {            	
            } else if (comps.size()==1) {
            	//availableFlavors.add(DataFlavor.getTextPlainUnicodeFlavor());
            	availableFlavors.add(DataFlavor.stringFlavor);
            	availableFlavors.add(plainTextFlavor);
            	availableFlavors.add(DataFlavor.javaFileListFlavor);
            } else {
            	availableFlavors.add(DataFlavor.javaFileListFlavor);
            }
            availableFlavors.add(CompositionDataFlavor.getFlavor());
            
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
        	return availableFlavors.toArray(new DataFlavor[availableFlavors.size()]);
        }        
        
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
        	return availableFlavors.contains(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
        	
        	if (!isDataFlavorSupported(flavor)) {
        		throw new UnsupportedFlavorException(flavor);                
            }
        	if (flavor.equals(DataFlavor.javaFileListFlavor)) {
        		ArrayList<File> files = new ArrayList<File>();
                for (Composition comp:comps) {
                	try {
                		files.add(new File(comp.getLinkLocal()));
                	} catch (Exception ex){
                		
                	}
                }
                return files;
        	}  else if (flavor.equals(DataFlavor.stringFlavor)) {
        		return comps.get(0).getCompleteDataForStoring();  
        	}        		
        	 else {
        		throw new UnsupportedFlavorException(flavor);
        	}
        	
        }
    }
	JTable compositionTable = null;
	CompositionTableModel tableModel = null;

	
	public CompositionListPanel(CompositionTableModel model) {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		//this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.tableModel = model;
		compositionTable = new JTable(model);
		compositionTable.setDefaultRenderer(Integer.class, new StateRenderer(false));
		compositionTable.setDefaultRenderer(Object.class, new CellRenderer(true));
		compositionTable.setBackground(Color.white);
		compositionTable.getColumnModel().getColumn(0).setMaxWidth(20);
		compositionTable.getColumnModel().getColumn(1).setMinWidth(140);
		compositionTable.getColumnModel().getColumn(2).setMinWidth(90);
		compositionTable.getColumnModel().getColumn(2).setMaxWidth(140);
		compositionTable.getColumnModel().getColumn(3).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(3).setMaxWidth(145);
		compositionTable.getColumnModel().getColumn(4).setMinWidth(80);
		compositionTable.getColumnModel().getColumn(5).setMinWidth(140);
		compositionTable.getColumnModel().getColumn(6).setMinWidth(95);
		compositionTable.getColumnModel().getColumn(6).setMaxWidth(95);
		compositionTable.getColumnModel().getColumn(7).setMinWidth(95);
		compositionTable.getColumnModel().getColumn(7).setMaxWidth(95);
		compositionTable.addMouseListener(GUI.proxyClickListener(Master.master, "clickOnCompositionList"));
		//compositionTable.setShowGrid(false);
		compositionTable.setGridColor(GuiConfig.tableBG);
		compositionTable.setShowGrid(false);
		compositionTable.setShowHorizontalLines(false);
		compositionTable.setShowVerticalLines(true);
		//compositionTable.set
		//compositionTable.setPreferredScrollableViewportSize(new Dimension(1000,500));
		//compositionTable.setPreferredSize(new Dimension(1000,500));
		compositionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		compositionTable.addMouseListener(new MouseAdapter()
		{

			private Composition determineComposition (MouseEvent e, boolean ensureItIsSelected) {
				JTable source = (JTable)e.getSource();
				int row = source.rowAtPoint( e.getPoint() );
				int column = source.columnAtPoint( e.getPoint() );
				
				if (ensureItIsSelected) {
					if (! source.isRowSelected(row))
						source.changeSelection(row, column, false, false);
				}
				
				int index = source.getRowSorter().convertRowIndexToModel(row);
				Composition compAtRow = ((CompositionTableModel) source.getModel()).getComposition(index);
				return compAtRow;
			}

			private void showPopup(MouseEvent e) {

				Composition comp = determineComposition(e, true);
				getPopupMenu(comp).show(e.getComponent(), e.getX(), e.getY());

			}
			
			private void processClick(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopup(e);
				} else if (e.getClickCount() == 2){
					Composition comp = determineComposition(e, true);
					Master.master.openEditor(comp);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if (Config.operatingSystem == Config.OperatingSystems.Mac) {
					processClick(e);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if (Config.operatingSystem != Config.OperatingSystems.Mac) {
					processClick(e);
				}
			}

		});



		//set Rowsorters
		RowSorter<CompositionTableModel> sorter = new TableRowSorter<CompositionTableModel>(model);
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		for (int i = 1; i < model.getColumnCount(); i++) {
			sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
		}
		sorter.setSortKeys(sortKeys); 

		//= new Def
		compositionTable.setRowSorter(sorter);

		this.setViewportView(compositionTable);
		this.getViewport().setBackground(Color.white);
		
		compositionTable.setDragEnabled(true);
		compositionTable.setTransferHandler(new CompositionListTransferHandler(this));


		this.setOpaque(false);

		//this.addMouseListener(this);

	}

	public JPopupMenu getPopupMenu(Composition comp) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem open = new JMenuItem(new OpenComposition(comp));
		JMenuItem reveal = new JMenuItem(new RevealCompositionInOSFileManager(null, null, comp));
		JMenuItem remove = new JMenuItem(new RemoveSelected(Master.master.getBrowser()));
		
		popupMenu.add(open);
		popupMenu.add(reveal);
		popupMenu.addSeparator();
		popupMenu.add(remove);
		
		return popupMenu;
	}

	public ArrayList<Composition> getSelectedCompositions() {
		ArrayList<Composition> selectedComps = new ArrayList<Composition> ();
		int[] selectedRows = compositionTable.getSelectedRows();
		for (int i=0; i < selectedRows.length; i++) {
			
			int index = compositionTable.getRowSorter().convertRowIndexToModel(selectedRows[i]);
			selectedComps.add(tableModel.getComposition(index));
		}
		return selectedComps;
	}
	
	public JTable getCompositionTable() {
		return compositionTable;
	}

	public CompositionTableModel getTableModel() {
		return tableModel;
	}


}

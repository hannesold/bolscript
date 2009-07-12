package gui.bols;

import gui.playlist.PlayablePanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import basics.Debug;
import basics.GUI;
import basics.Rational;
import bols.Bol;
import bols.BolBundle;
import bols.BolName;
import bols.HasPlayingStyle;
import bols.NamedInLanguages;
import bols.tals.LayoutCycle;
import bols.tals.Tal;
import bols.tals.TalBase;
import bols.tals.Vibhag;
import bolscript.sequences.FootnoteUnit;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;
import config.Config;

public class SequencePanel extends PlayablePanel  {

	public static Color cBackground = Color.WHITE;
	public static Color cDistinctBackground = new Color(200,220,200);
	public static Color cBorder = (new JPanel()).getBackground().darker();
	protected static int cellLayer = 2;
	protected static int bolLayer = 3;
	protected static int vibhagLayer = 4;
	protected static int cellMargin = 16; //vormals 16
	protected static int rowMargin = 16;
	
	protected static Insets insets = new Insets(10,20,10,20);
	
	protected RepresentableSequence sequence;
	
	//private Variation variation; 
	protected Tal tal;
	protected int language = BolName.SIMPLE;
	protected float fontSize = Config.bolFontSizeStd[BolName.SIMPLE];
	
	protected Dimension vibhagSize;
	protected boolean playing;
	protected ArrayList<CellPanel> cells;
	protected CellPanel cellHighlightedlastTime;
	protected int fixedMaxSpeed;
	protected String fixedLargestWidthBol;
	protected Dimension wantedSize;

	protected int nrOfCells;
	protected int minRows, rowHeight;
	protected int cellWidthPlusMargin;

	protected Dimension cellSize;
	protected LayoutCycle layoutCycle;
	
	protected ArrayList<Float> lineBreaks;
	
	
	public SequencePanel(RepresentableSequence sequence, Tal tal, Dimension size, int minRows, String fixedLargestWidthBol, int fixedMaxSpeed, int language, float fontSize) {
		super();
		this.setLayout(null);
		this.setBorder(BorderFactory.createLineBorder(cBorder,2));
		
		lineBreaks = new ArrayList<Float>();
		cells = new ArrayList<CellPanel>();
		playing = false;
		cellHighlightedlastTime = null;
		
		this.sequence = sequence;
		//Debug.temporary(this, sequence.toString(true, false, true, false, true, false, BolName.EXACT, 10000));
		
		if (tal == null) {
			this.tal = TalBase.standard().getTalFromName("Teental");
		} else {
			this.tal = tal;
		}
		
		this.fixedLargestWidthBol = fixedLargestWidthBol;
		this.minRows = minRows;
		this.fixedMaxSpeed = fixedMaxSpeed;
		this.wantedSize = size;
		this.language = language;
		this.fontSize = fontSize;
		//Debug.debug(this, "new vp, language is " + language);
		
		render();
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		if (playing!=this.playing) {
			this.playing = playing;
			if (playing ){
				//this.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			} else {
				//this.setBorder(BorderFactory.createLineBorder(cBackground, 1));
			}
		}
		
	}
	
	public void highlightCell(int i) {
		//System.out.println("variationpanel shall highlight cell " + i);
		int theIndex = i % cells.size();
		if ( theIndex < cells.size()) {
			if (cells.get(theIndex) != cellHighlightedlastTime) {
				if (cellHighlightedlastTime != null) {
					cellHighlightedlastTime.setHighlight(false);
					cellHighlightedlastTime.revalidate();
				}
				cellHighlightedlastTime = cells.get(theIndex);
				cellHighlightedlastTime.setHighlight(true);
				cellHighlightedlastTime.revalidate();
			}
		}
	}

	public void unHighlightCells() {
		if (cellHighlightedlastTime != null) {
			cellHighlightedlastTime.setHighlight(false);
			cellHighlightedlastTime.revalidate();
		}
	}
	
	protected void render() {
		nrOfCells = (int) Math.ceil(sequence.getDuration());
		
		cellSize = determinCellSize();
		cellWidthPlusMargin = cellSize.width + cellMargin;
		rowHeight = cellSize.height + rowMargin;
		
		// establish layoutCycle
		// which gives us the row lengths and cell coordinates etc.
		int maxDisplayableCellsPerRow = (wantedSize.width - insets.left - insets.right) 
		/ cellWidthPlusMargin;
		layoutCycle = tal.getLayoutChooser().getLayoutCycle(maxDisplayableCellsPerRow,100);
		if (layoutCycle == null) Debug.critical(this, "no layoutCycle found ! in chooser \n" + tal.getLayoutChooser());
		
		// remove everything
		this.removeAll();
		
		// add cells
		renderCells();
		
		// add vibhags
		renderVibhags();
		
		// add bols
		renderBols();
		
		
		// adjust size
		int widthWithoutInsets = (cellWidthPlusMargin) * (layoutCycle.getExactDimensions(nrOfCells).width) - cellMargin;
		int heightWithoutInsets = (layoutCycle.getExactDimensions(nrOfCells).height)*rowHeight;
		Dimension newSize = new Dimension(widthWithoutInsets+insets.right+insets.left, heightWithoutInsets+insets.top+insets.bottom);
		this.setSize(newSize);
		this.setPreferredSize(newSize);	
		this.setMaximumSize(newSize);
		this.setMinimumSize(newSize);
		
		//adjust color
		if (GUI.showLayoutStructure) {
			this.setBackground(cDistinctBackground);
		}else {
			this.setBackground(cBackground);
		}
		this.setOpaque(true);

		
		this.revalidate();
	}

	/**
	 * Determines the Size of the cells according to the maximally sized
	 * occuring labels that have to be fitted in on cell.
	 * @return
	 */
	protected Dimension determinCellSize() {
		JLabel label = new JLabel("");
		label.setFont(Config.getBolFont(language, fontSize, false));
		int maxBolLabelHeight=0;
		int maxCellWidth = 0;
		Dimension tempSize = new Dimension(0,0);
		
		// Establish maximum dimensions
		if ((fixedLargestWidthBol == "")&&(fixedMaxSpeed==0)) {
			
			//get largest bol and highest speed
			
			for (int i = 0; i < sequence.size(); i++) {
				Representable rep = sequence.get(i);
				int type = sequence.get(i).getType();
				
				if (type == Representable.BOL || type == Representable.BUNDLE) {
					NamedInLanguages nameGiver;
					double speed;
					
					if (type == Representable.BOL) {
						nameGiver = ((Bol) rep).getBolName();
						speed = ((Bol) rep).getSpeed();
					} else { //it is of type bolbundle
						nameGiver = ((BolBundle)rep).getBolNameBundle();
						speed = ((BolBundle) rep).getPlayingStyle().getSpeedValue();
					}
					
					label.setText(nameGiver.getName(language));
					tempSize = GUI.getPrefferedSize(label, 100);
					maxBolLabelHeight 	= Math.max(tempSize.height, maxBolLabelHeight);
					int width = (int) (speed*( (double) (BolPanel.marginLeft + BolPanel.marginRight + tempSize.width)));
					maxCellWidth = (int) Math.max(width, maxCellWidth);
				} 
			}
			
		} else {
			label.setText(fixedLargestWidthBol);
			tempSize = GUI.getPrefferedSize(label, 100);
			maxBolLabelHeight 	= Math.max(tempSize.height, maxBolLabelHeight);
			int width = (int) (fixedMaxSpeed*( (double) (BolPanel.marginLeft + BolPanel.marginRight + tempSize.width)));
			maxCellWidth = width;
			
		}
		Debug.temporary(this, "determined maxCellwidth: " + maxCellWidth);
		Debug.temporary(this, "determined maxBolLabelHeight: " + maxBolLabelHeight);
		
		// cell sizes and resulting dimensions of cells and vibhags
		int cellWidth = maxCellWidth;//(int) (maxBolDimension.width * maxSpeed);
		int cellHeight = maxBolLabelHeight + BolPanel.marginBottom 	+ BolPanel.marginTop;
		
		return new Dimension(cellWidth, cellHeight);
	}

	/**
	 * Adds the bol, bundles and footnotes.
	 * Expects cellSize and layoutCylcle to be established.
	 */
	protected void renderBols() {
		int cellWidth = cellSize.width;
		int cellHeight = cellSize.height;
		int y = 0;
		int x = 0;
		int cellNr;
		
		Rational currentPos = new Rational(0);
		Rational lastPos = new Rational(0);
		
		BolPanelGeneral lastBolPanel = null;
		//ArrayList<JPanel> bolPanels = new ArrayList<JPanel>();
		//ArrayList<Bol> bols = new ArrayList<Bol> ();
		for (int i = 0; i < sequence.size(); i++) {
			Representable rep = sequence.get(i);
			int type = rep.getType();
			if ( type == Representable.BOL || type == Representable.BUNDLE) {
				HasPlayingStyle b = (HasPlayingStyle) rep;
				
				// the number of the cell in which the bol will be put
				cellNr = currentPos.integerPortion();
				
				// the relative position in the cell
				double posOffset = (currentPos.toDouble() % 1.0);
				int xOffSet = (int) ((double)cellWidth * posOffset);
				x = getCellCoords(cellNr).x + xOffSet;
				y = getCellCoords(cellNr).y;
				
				int width = (int)((double)cellWidth / (double) b.getPlayingStyle().getSpeedValue());
				int height = cellHeight;
				
				BolPanelGeneral bp;
				
				if (type == Representable.BOL) {
					bp = new BolPanel((Bol) b,new Dimension(width,height),((Bol) b).isEmphasized(), language, fontSize);
				} else {
					//is of type bundle!
					bp = new BolBundlePanel((BolBundle) b, new Dimension(width,height), false, language, fontSize);
				}
					
				lastBolPanel = bp;
				
				bp.setBounds(x,y,width,height);
				this.add(bp);
				//bolPanels.add(bp);
				this.setLayer(bp, bolLayer);
				
				lastPos = currentPos;
				currentPos = currentPos.plus(b.getPlayingStyle().getSpeed().reciprocal());
			} else switch (type) {
			
			case Representable.FOOTNOTE:
				FootnotePanel cp = new FootnotePanel((FootnoteUnit) rep, FootnotePanel.maxSize);
				if (lastBolPanel != null) {
					x = lastBolPanel.getBounds().x + lastBolPanel.getBounds().width-FootnotePanel.maxSize.width;
				} else {
					x = 0;
				}
				cellNr = lastPos.integerPortion();
				y = getCellCoords(cellNr).y;
				cp.setBounds(x,y,FootnotePanel.maxSize.width,FootnotePanel.maxSize.height);
				this.add(cp);
				this.setLayer(cp, vibhagLayer);
				break;
			case Representable.COMMA:
				/* if (lastBolPanel != null) {
					x = lastBolPanel.getBounds().x + lastBolPanel.getBounds().width-ColonPanel.maxSize.width;	
				} else x =0;
				
				cellNr = currentPos.integerPortion();
				y = getCellCoords(cellNr).y;
				
				
				ColonPanel c = new ColonPanel((Unit) rep, new Dimension(ColonPanel.maxSize.width, cellHeight));
				c.setBounds(x,y,CommentPanel.maxSize.width,cellHeight);
				this.add(c);
				this.setLayer(c, vibhagLayer);*/
				break;
			case Representable.BRACKET_OPEN:
				break;
			}
		}	
	}
	
	/**
	 * Renders the cells and adds linebreaks
	 * 
	 */
	protected void renderCells() {
		lineBreaks.clear();
		int y = 0;
		for (int i = 0; i < nrOfCells; i++) {
			if (i!=0 && getCellCoords(i).y != y) {
				y = getCellCoords(i).y;
				lineBreaks.add(new Float(y));
			}
			// add Cell
			CellPanel newCell = new CellPanel(getCellCoords(i).x, getCellCoords(i).y, cellSize);
			
			cells.add(newCell);
			this.add(newCell);
			this.setLayer(newCell, cellLayer);
		}
	}
	
	/**
	 * Get the pixel coordinates of the upper left corner of a cell.
	 * Expects layoutCyle, cellWidthPlusMargin and rowHeight to be set
	 * @param cellNr the index of the cell.
	 * @return
	 */
	protected Point getCellCoords(int cellNr) {
		Point c = layoutCycle.getCoordinates(cellNr);
		
		return new Point(
				insets.left + (cellWidthPlusMargin * c.x),
				insets.top + c.y * rowHeight);
	}

	/**
	 * Adds the VibhagPanels
	 * Expects nrOfCells and cellMargin to be set.
	 * Uses getCellCoords.
	 */	
	protected void renderVibhags() {

		vibhagSize = new Dimension(20, cellSize.height);
		
		int vibhagIndex = 0;
		Vibhag[] vibhags = tal.getVibhags();
		int i = 0;
		
		while (i < nrOfCells) {
			
			Vibhag vibhag = vibhags[vibhagIndex % vibhags.length] ;
			
			int vX = getCellCoords(i).x - cellMargin/2;
			int vY = getCellCoords(i).y;
			
			// Add vibhagPanel
			VibhagPanel vp = new VibhagPanel(vX,vY, vibhagSize, vibhag);
			this.add(vp);
			this.setLayer(vp,vibhagLayer);
			
			i += vibhag.getLength();
			vibhagIndex++;
		}
		
	}

	public void setContents(RepresentableSequence seq, Tal tal) {
		this.sequence = seq;
		this.tal = tal;
		render();
	}

	public ArrayList<Float> getLineBreaks() {
		return lineBreaks;
	}

}

package gui.bolscript;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JTextPane;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import basics.Debug;
import bols.Bol;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.TextReference;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

public class BolscriptDocument extends DefaultStyledDocument{

	private static final String HIGHLIGHTED_MARKER = "___HIGHLIGHTED";
	static Color editorMetaColor = new Color(20,200,20);
	static Color footnoteColor = new Color(20,20,200);
	static Color bolKeyColor = Color.BLACK;
	static Color unitColor = Color.BLACK;

	static Color failedKeyColor = bolKeyColor;
	static Color failedValueColor = Color.red;
	static Color highlightedBolBackground;

	//final Matcher matcher =
	//    Pattern.compile("sneach").matcher("");

	// int keywordPosition = 0;
	//int keywordLength = 0;

	Style styleMetaKey;
	Style styleMetaValue;

	Style rootStyle;
	Style styleBolKey;
	Style styleBol;

	Style styleEmphasizedBol;
	Style styleBracket;
	Style styleKardinality;
	Style styleSpeed;

	Style styleUnit;

	Style styleHighlightedBolKey;
	Style styleHighlightedBolVal;

	Style styleFootnote;

	Style parseError;

	HashMap<Integer, Style> keyStyleMaps, valueStyleMaps;
	private Style styleFailedKey;
	private Style styleFailedValue;
	//  static Matcher metaKeyMatcher;

	DocumentListener[] documentListeners;
	UndoableEditListener[] undoListeners;

	private int caretPosition=0;

	private Packet highlightedReferencedPacket = null;

	public Packet getHighlightedReferencedPacket() {
		return highlightedReferencedPacket;
	}

	public void setHighlightedReferencedPacket(Packet highlightedReferencedPacket) {
		this.highlightedReferencedPacket = highlightedReferencedPacket;
	}

	public void initStylesAndMaps(){
		rootStyle = addStyle("root", null);
		StyleConstants.setForeground(rootStyle, Color.BLACK);
		StyleConstants.setBackground(rootStyle, Color.WHITE);

		styleMetaKey = addStyle("key", rootStyle);
		StyleConstants.setBold(styleMetaKey, true);
		StyleConstants.setForeground(styleMetaKey, editorMetaColor);

		styleBolKey = addStyle("meta", styleMetaKey);
		StyleConstants.setForeground(styleBolKey, bolKeyColor);

		styleFailedKey = addStyle("failedkey", styleMetaKey);
		styleFailedValue = addStyle("failedkey", styleMetaValue);
		StyleConstants.setForeground(styleFailedKey, failedKeyColor);
		StyleConstants.setForeground(styleFailedValue, failedValueColor);
		StyleConstants.setItalic(styleFailedValue, true);
		//StyleConstants.setBackground(styleFailedValue, new Color(255,230,230));

		styleMetaValue = addStyle("metavalue", rootStyle);

		styleUnit = addStyle("styleUnit", rootStyle);
		StyleConstants.setForeground(styleUnit, unitColor);
		StyleConstants.setItalic(styleUnit, false);

		styleFootnote = addStyle("footnote", styleUnit);
		StyleConstants.setForeground(styleFootnote, footnoteColor);
		StyleConstants.setItalic(styleFootnote, true);

		styleBol = addStyle("bolvalue", styleUnit);
		StyleConstants.setForeground(styleBol, new Color(0,0,0));

		styleEmphasizedBol = addStyle("emphasized", styleBol);
		StyleConstants.setForeground(styleEmphasizedBol, new Color(30,30,30));
		StyleConstants.setBold(styleEmphasizedBol, true);

		Color metaUnitColor = new Color(157, 21, 119);
		
		
		styleBracket = addStyle("bracket", styleUnit);
		StyleConstants.setBold(styleBracket, true);

		styleKardinality = addStyle("kardinality", styleUnit);
		StyleConstants.setForeground(styleKardinality, metaUnitColor);
		StyleConstants.setBold(styleKardinality, true);
		
		styleSpeed = addStyle("speed", styleUnit);
		StyleConstants.setForeground(styleSpeed, metaUnitColor);
		StyleConstants.setBold(styleSpeed, true);

		highlightedBolBackground = (new JTextPane()).getSelectionColor();
		highlightedBolBackground = new Color(highlightedBolBackground.getRed(), highlightedBolBackground.getGreen(), highlightedBolBackground.getBlue(), highlightedBolBackground.getAlpha() /2);
		styleHighlightedBolKey = addStyle("highlightedBolKey", styleBolKey);
		styleHighlightedBolVal = addStyle("highlightedBolKey", styleBol);
		StyleConstants.setForeground(styleHighlightedBolKey, Color.BLACK);
		StyleConstants.setBackground(styleHighlightedBolKey, highlightedBolBackground);
		StyleConstants.setForeground(styleHighlightedBolVal, Color.BLACK);
		StyleConstants.setBackground(styleHighlightedBolVal, highlightedBolBackground);	          
		//StyleConstants.setLeftIndent(styleMetaValue, 20f);

		keyStyleMaps = new HashMap<Integer, Style>();
		valueStyleMaps = new HashMap<Integer, Style>();
		PacketType[] metaTypes = PacketTypeFactory.getMetaTypes();
		for (int i = 0; i < metaTypes.length; i++) {
			keyStyleMaps.put(metaTypes[i].getId(), styleMetaKey);
			valueStyleMaps.put(metaTypes[i].getId(), styleMetaValue);

		}
		keyStyleMaps.put(PacketTypeFactory.FAILED, styleFailedKey);
		valueStyleMaps.put(PacketTypeFactory.FAILED, styleFailedValue);

		Enumeration<?> names = getStyleNames();

		ArrayList<String> existing = new ArrayList<String>();

		while (names.hasMoreElements()) {
			existing.add((String) names.nextElement());
		}

		for (String n: existing) {
			Style h = addStyle(n + HIGHLIGHTED_MARKER, getStyle(n));
			StyleConstants.setBackground(h, highlightedBolBackground);
		}
		//packetTypeStyleMap.put(Packet., value)
	}

	public BolscriptDocument() {
		super();
		initStylesAndMaps();
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a)
	throws BadLocationException {
		super.insertString(offs, str, a);
	}

	@Override
	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);
	}

	/**
	 * Removes and backs up undo listeners
	 */
	public void removeAndBackupListeners(){
		undoListeners = getListeners(UndoableEditListener.class);
		for (UndoableEditListener listener:undoListeners) {
			removeUndoableEditListener(listener);
		}
	}

	/**
	 * Rebuilds undo listeners from backup
	 * @param packets
	 */
	public void rebuildListeners() {
		for (UndoableEditListener listener:undoListeners) {
			addUndoableEditListener(listener);
		}
	}

	public void updateStylesNow(Packets packets) {

		removeAndBackupListeners();

		// Debug.temporary(getClass(), "update styles!");
		setCharacterAttributes(0, getLength(), rootStyle, true);

		try {
			for (Packet p: packets) {
				//Debug.temporary(getClass(), "checking packet " + p);
				//Debug.temporary(getClass(), "ref: " + p.getTextReference());
				if (p.hasTextReferences()) {
					//  Debug.temporary(getClass(), "replacing style");
					Style keyStyle = keyStyleMaps.get(p.getType());
					Style valStyle = valueStyleMaps.get(p.getType());
					if (keyStyle == null) keyStyle = styleBolKey;
					if (valStyle == null) valStyle = styleBol;

					if (highlightedReferencedPacket != null) {
						if (highlightedReferencedPacket==p) {
							keyStyle = styleHighlightedBolKey;
							valStyle = styleHighlightedBolVal;
						}
					}
					/*if (p.isHighlighted()) {
						 keyStyle = styleHighlightedBolKey;
						 valStyle = styleHighlightedBolVal;
					 }*/

					//Debug.temporary(getClass(), "key: " + p.getTextRefKey());
					//Debug.temporary(getClass(), "val: " + p.getTextRefValue());
					setCharacterAttributes(p.getTextRefKey().start(), p.getTextReference().length(), keyStyle, true);
					setCharacterAttributes(p.getTextRefValue().start(), p.getTextRefValue().length(), valStyle, true);


					if (p.getType() == PacketTypeFactory.BOLS) {

						RepresentableSequence seq = ((RepresentableSequence) p.getObject());
						renderSequence(seq, p);


					}

				}

			}

		} catch (Exception ex) {
			Debug.critical(BolscriptDocument.class, "error in updating styles");
			ex.printStackTrace();
		}

		rebuildListeners();
	}

	private void renderSequence(RepresentableSequence seq, Packet p) {
		for (Representable r: seq) {
			int type = r.getType();
			if (type == Representable.SEQUENCE) {
				renderSequence((RepresentableSequence) r,p);
			} else {
				TextReference absoluteReference = r.getTextReference().translateBy(p.getTextRefValue());

				boolean isAtCursor = absoluteReference.contains(caretPosition);
				Style currentUnitStyle = getUnitStyle(r, isAtCursor);
				if (currentUnitStyle != null) {
					if (p == highlightedReferencedPacket) {
						currentUnitStyle = getStyle(currentUnitStyle.getName() + HIGHLIGHTED_MARKER);	
					}

				}
				if (currentUnitStyle != null) {
					setCharacterAttributes(
							absoluteReference.start(), 
							absoluteReference.length(), currentUnitStyle, true);
				}

			}

		}
	}
	private Style getUnitStyle(Representable r, boolean isAtCursor) {


		int type = r.getType();
		switch (type) {
		case Representable.BOL:
			Bol b = (Bol) r;
			if (!isAtCursor && !b.getBolName().isWellDefinedInBolBase())
				return styleFailedValue;
			else if (b.isEmphasized()){
				return styleEmphasizedBol; 
			} else {
				return null;
			}
		case Representable.FAILED:
			if (!isAtCursor) {
				return styleFailedValue;
			}
			break;

		case Representable.FOOTNOTE:
			return styleFootnote;

		case Representable.BRACKET_OPEN:
			return styleBracket;
		case Representable.BRACKET_CLOSED:
			return styleBracket;
		case Representable.KARDINALITY_MODIFIER:
			return styleKardinality;
		case Representable.SPEED:
			return styleSpeed;
		}

		return styleUnit;
	}

	private class StyleUpdater implements Runnable {
		Packets packets;

		StyleUpdater(Packets packets) {
			this.packets = packets;
		}
		public void run() {
			updateStylesNow(packets);  
		}
	}

	public void updateStylesLater(Packets packets) {
		EventQueue.invokeLater(new StyleUpdater(packets));
	}

	public void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}

}

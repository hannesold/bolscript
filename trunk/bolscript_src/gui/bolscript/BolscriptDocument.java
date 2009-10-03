package gui.bolscript;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
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
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.sequences.Representable;
import bolscript.sequences.RepresentableSequence;

public class BolscriptDocument extends DefaultStyledDocument{

	static Color editorMetaColor = new Color(20,200,20);
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
	Style styleBolValue;


	Style styleUnit;


	Style styleHighlightedBolKey;
	Style styleHighlightedBolVal;

	Style parseError;


	HashMap<Integer, Style> keyStyleMaps, valueStyleMaps;
	private Style styleFailedKey;
	private Style styleFailedValue;
	//  static Matcher metaKeyMatcher;

	DocumentListener[] documentListeners;
	UndoableEditListener[] undoListeners;

	public  void initStylesAndMaps(){
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


		styleMetaValue = addStyle("metavalue", rootStyle);
		styleBolValue = addStyle("bolvalue", styleMetaValue);

		styleUnit = addStyle("styleUnit", styleBolValue);
		StyleConstants.setForeground(styleUnit, unitColor);
		StyleConstants.setItalic(styleUnit, false);

		highlightedBolBackground = (new JTextPane()).getSelectionColor();
		highlightedBolBackground = new Color(highlightedBolBackground.getRed(), highlightedBolBackground.getGreen(), highlightedBolBackground.getBlue(), highlightedBolBackground.getAlpha() /2);
		styleHighlightedBolKey = addStyle("highlightedBolKey", styleBolKey);
		styleHighlightedBolVal = addStyle("highlightedBolKey", styleBolValue);
		StyleConstants.setForeground(styleHighlightedBolKey, Color.BLACK);
		StyleConstants.setBackground(styleHighlightedBolKey, highlightedBolBackground);
		StyleConstants.setForeground(styleHighlightedBolVal, Color.BLACK);
		StyleConstants.setBackground(styleHighlightedBolVal, highlightedBolBackground);	          

		keyStyleMaps = new HashMap<Integer, Style>();
		valueStyleMaps = new HashMap<Integer, Style>();
		PacketType[] metaTypes = PacketTypeFactory.getMetaTypes();
		for (int i = 0; i < metaTypes.length; i++) {
			keyStyleMaps.put(metaTypes[i].getId(), styleMetaKey);
			valueStyleMaps.put(metaTypes[i].getId(), styleMetaValue);

		}
		keyStyleMaps.put(PacketTypeFactory.FAILED, styleFailedKey);
		valueStyleMaps.put(PacketTypeFactory.FAILED, styleFailedValue);

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
					 if (valStyle == null) valStyle = styleBolValue;
					 if (p.isHighlighted()) {
						 keyStyle = styleHighlightedBolKey;
						 valStyle = styleHighlightedBolVal;
					 }
					 //Debug.temporary(getClass(), "key: " + p.getTextRefKey());
					 //Debug.temporary(getClass(), "val: " + p.getTextRefValue());
					 setCharacterAttributes(p.getTextRefKey().start(), p.getTextReference().length(), keyStyle, true);
					 setCharacterAttributes(p.getTextRefValue().start(), p.getTextRefValue().length(), valStyle, true);


					 if (p.getType() == PacketTypeFactory.BOLS) {
						 ArrayList<Representable> failedUnits = ((RepresentableSequence) p.getObject()).getFailedUnits();

						 for (Representable r : failedUnits) {
							 setCharacterAttributes(
									 r.getTextReference().start()+p.getTextRefValue().start(), 
									 r.getTextReference().length(), styleFailedValue, true);
						 }
					 }

				 }

			 }

		 } catch (Exception ex) {
			 Debug.critical(BolscriptDocument.class, "error in updating styles");
			 ex.printStackTrace();
		 }

		 rebuildListeners();
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

}

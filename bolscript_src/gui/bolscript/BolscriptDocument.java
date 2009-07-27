package gui.bolscript;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.HashMap;

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

public class BolscriptDocument extends DefaultStyledDocument{
		 
		static Color metaColor = new Color(20,200,20);
		static Color bolKeyColor = Color.BLACK;
		static Color failedKeyColor = bolKeyColor;
		static Color failedValueColor = Color.red;
	      //final Matcher matcher =
	        //    Pattern.compile("sneach").matcher("");
	 
	     // int keywordPosition = 0;
	      //int keywordLength = 0;
		
	       Style styleMetaKey;
	       Style styleMetaValue;
	       Style rootStyle;
	       Style styleBolKey;
	       Style styleBolValue;
	       Style parseError;
	    
	      
	       HashMap<Integer, Style> keyStyleMaps, valueStyleMaps;
		private Style styleFailedKey;
		private Style styleFailedValue;
	    //  static Matcher metaKeyMatcher;
		
		DocumentListener[] documentListeners;
	    UndoableEditListener[] undoListeners;
	      
	      public  void initStylesAndMaps(){
	    	  rootStyle = addStyle("root", null);
	          
	    	  styleMetaKey = addStyle("key", rootStyle);
	          StyleConstants.setBold(styleMetaKey, true);
	          StyleConstants.setForeground(styleMetaKey, metaColor);
	          
	          styleBolKey = addStyle("meta", styleMetaKey);
	          StyleConstants.setForeground(styleBolKey, bolKeyColor);
	          
	          styleFailedKey = addStyle("failedkey", styleMetaKey);
	          styleFailedValue = addStyle("failedkey", styleMetaValue);
	          StyleConstants.setForeground(styleFailedKey, failedKeyColor);
	          StyleConstants.setForeground(styleFailedValue, failedValueColor);
	          StyleConstants.setItalic(styleFailedValue, true);
	          
	          styleMetaValue = addStyle("metavalue", rootStyle);
	          styleBolValue = addStyle("bolvalue", styleMetaValue);
	          
	    	 
	          keyStyleMaps = new HashMap<Integer, Style>();
	          valueStyleMaps = new HashMap<Integer, Style>();
	          for (int i = 0; i < Packet.METATYPES.length; i++) {
	        	  keyStyleMaps.put(Packet.METATYPES[i], styleMetaKey);
	        	  valueStyleMaps.put(Packet.METATYPES[i], styleMetaValue);
	        	  
	          }
	          keyStyleMaps.put(Packet.FAILED, styleFailedKey);
			valueStyleMaps.put(Packet.FAILED, styleFailedValue);
	          
	          //packetTypeStyleMap.put(Packet., value)
	      }
	      
	      public BolscriptDocument() {
	    	  super();
	    	  
	    	  
	          
	          initStylesAndMaps();
	          //final String kw = "THE_KEYWORD";
	          //keywordLength = kw.length();
	      }
	 
	      @Override
	      public void insertString(int offs, String str, AttributeSet a)
	            throws BadLocationException {
	    	 //if (!getText(offs, str.length()).equals(str)) {
	    		 super.insertString(offs, str, a);
	    		 checkDocument();
	    	 //}
	      }
	 
	      @Override
	      public void remove(int offs, int len) throws BadLocationException {
	        super.remove(offs, len);
	        checkDocument();
	      }
	 
	      private void checkDocument() {
	 
	        /*setCharacterAttributes(0, getLength(), rootStyle, true);
	        try {
	          matcher.reset(getText(0, getLength()));
	          while (matcher.find()) {
	            setCharacterAttributes(matcher.start(), matcher.end()
	                  - matcher.start(), styleMetaKey, true);
	          }
	        } catch (BadLocationException ex) {
	          ex.printStackTrace();
	        }*/
	      }
	      
	      /**
	       * Removes and backs up undo listeners
	       */
	      public void removeAndBackupListeners(){
	    	//documentListeners = getListeners(DocumentListener.class);
		    undoListeners = getListeners(UndoableEditListener.class);
//		    for (DocumentListener listener:documentListeners) {
//		    	//removeDocumentListener(listener);
//		    }
		    for (UndoableEditListener listener:undoListeners) {
		    	removeUndoableEditListener(listener);
		    }
	      }
	      
	      /**
	       * Rebuilds undo listeners from backup
	       * @param packets
	       */
	      public void rebuildListeners() {
	  		    //for (DocumentListener listener:documentListeners) {
	  		    	//addDocumentListener(listener);
	  		    //}
	  		    for (UndoableEditListener listener:undoListeners) {
	  		    	addUndoableEditListener(listener);
	  		    }
	      }
	      
	      public void updateStylesNow(Packets packets) {
	    	
	    	removeAndBackupListeners();
	    	
	    	  Debug.temporary(getClass(), "update styles!");
	    	  setCharacterAttributes(0, getLength(), rootStyle, true);
	    	  try {
	    	  for (Packet p: packets) {
	    		  Debug.temporary(getClass(), "checking packet " + p);
	    		  Debug.temporary(getClass(), "ref: " + p.getTextReference());
	    		  if (p.hasTextReferences()) {
	    			  Debug.temporary(getClass(), "replacing style");
	    			  Style keyStyle = keyStyleMaps.get(p.getType());
	    			  Style valStyle = valueStyleMaps.get(p.getType());
	    			  if (keyStyle == null) keyStyle = styleBolKey;
	    			  if (valStyle == null) valStyle = styleBolValue;
	    			  Debug.temporary(getClass(), "key: " + p.getTextRefKey());
	    			  Debug.temporary(getClass(), "val: " + p.getTextRefValue());
	    			  setCharacterAttributes(p.getTextRefKey().start(), p.getTextReference().length(), keyStyle, true);
	    			  setCharacterAttributes(p.getTextRefValue().start(), p.getTextRefValue().length(), valStyle, true);
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
	      
	      public void updateStyles(Packets packets) {
	    	  EventQueue.invokeLater(new StyleUpdater(packets));
	      }

}

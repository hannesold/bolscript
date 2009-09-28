package bolscript.scanner;

import bolscript.packets.TextReference;

public class SequenceToken {
	
	  //public int charBegin;
	  public String text;
	  public int type;
	  //public int line;
	 // public int charEnd;
	  public TextReference textReference;
	  
	  public SequenceToken()
	  {
	  }

	  public SequenceToken(int type, String text, int charBegin, int line)
	  {
		  this.type = type;
		  this.text = text;
		  this.textReference = new TextReference(charBegin, charBegin+text.length(), line);
	  }

	  public String toString()
	  { 
	    return text;
	  }

	}

package bolscript.sequences;

import bolscript.scanner.SequenceToken;

public class FailedUnit extends Unit {

	//String text;
	private int intendedType;
	private String message;
	
	public FailedUnit(SequenceToken input, String message) {
		this.textReference = input.textReference;
		this.obj = input.text;
		this.type = Representable.FAILED;
		this.intendedType = input.type;
		this.message = message;
	}

	public FailedUnit(Representable r, String message) {
		this.textReference = r.getTextReference();
		this.obj = r;
		this.type = Representable.FAILED;
		this.intendedType = r.getType();
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public int getIntendedType() {
		return intendedType;
	}
	
	
	
}

package bolscript.sequences;

import bolscript.scanner.SequenceToken;

public class FailedUnit extends Unit {

	//String text;
	private int intendedType;
	private String message;
	
	public FailedUnit(SequenceToken input, String message) {
		super(Representable.FAILED, input.text, input.textReference);
		this.intendedType = input.type;
		this.message = message;
	}

	public FailedUnit(Representable r, String message) {
		super(Representable.FAILED, r, r.getTextReference());
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

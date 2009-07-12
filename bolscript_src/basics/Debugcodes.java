package basics;

public enum Debugcodes {

	CRITICAL ("critical"),
	USER ("user"),
	TEMPORARY ("temporary"),
	DEBUG ("debug");
		
	private final String label;
	
	Debugcodes(String name) {
	    this.label = name;
	}
	 
	public String toString() {
		return label;
	}

}

package algorithm.composers.kaida;

import algorithm.raters.Rater;

public class Feature {
	protected String message;
	public double value;
	protected Rater generator;
	protected boolean hasGenerator;
	
	public Feature(String message, double value) {
		super();
		// TODO Auto-generated constructor stub
		this.message = message;
		this.value = value;
		this.generator = null;
		hasGenerator = false;
	}
	
	public Feature(String message,  Rater generator, double value) {
		super();
		// TODO Auto-generated constructor stub
		this.message = message;
		this.value = value;
		this.generator = generator;
		hasGenerator = true;
	}	
	
	public Feature(Rater generator, double value) {
		super();
		// TODO Auto-generated constructor stub
		this.message = "";
		this.value = value;
		this.generator = generator;
		hasGenerator = true;
	}		

	public String toString() {
		if (hasGenerator) {
			return ("(" + generator.getLabel() + ": " + value + ")");
		} else {
			return ("(" + message + ": " + value + ")"); 
		}
	}

	public String getMessage() {
		return message;
	}
	
	
	public boolean equals(Feature f2) {
		return ( f2.getMessage().equals(message) &&
				 f2.value == value &&
				 f2.getGenerator().equals(generator));
	}

	public Rater getGenerator() {
		return generator;
	}

	public Feature getCopy() {
		// TODO Auto-generated method stub
		return new Feature(new String(message), generator, value);
	}

	public boolean hasGenerator() {
		// TODO Auto-generated method stub
		return hasGenerator;
	}

	public String getLabel() {
		
		if (hasGenerator) {
			return (generator.getLabel());
		} else {
			return (message); 
		}
	}
	
}

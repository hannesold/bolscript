package algorithm.composers.kaida;

import algorithm.raters.Rater;

public class Goal extends Feature {
	private double importance;
	private boolean wantsToBePiloted;
	private ValueRange valueRange;

	public ValueRange getValueRange() {
		return valueRange;
	}

	public void setValueRange(ValueRange valueRange) {
		this.valueRange = valueRange;
		valueRange.setValue((double)value);
	}

	public Goal(Rater rater, double value, double importance, ValueRange valueRange) {
		super(rater, value);
		this.importance = importance;
		this.valueRange = valueRange;
		valueRange.setValue((double)value);
		wantsToBePiloted = true;
	}
	
	public Goal(Feature f, double importance, ValueRange valueRange) {
		super(f.getGenerator(),f.value);
		this.importance = importance;
		this.valueRange = valueRange;
		valueRange.setValue((double)value);
		wantsToBePiloted = true;
	}
	
	

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}
	
	public String toString() {
		if (generator != null) {
			return ("(" + generator.getLabel() + ": " + value + ", imp:"+importance+")");
		} else {
			return ("(" + message + ": " + value +", imp:"+importance+ ")"); 
		}
	}

	public boolean wantsToBePiloted() {
		return wantsToBePiloted;
	}

	public void setWantsToBePiloted(boolean wantsToBePiloted) {
		this.wantsToBePiloted = wantsToBePiloted;
	}	
}

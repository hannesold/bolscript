package algorithm.composers.kaida;

import java.util.ArrayList;

import javax.swing.SpinnerNumberModel;

import algorithm.tools.Calc;

/**
 * A range of values in list form.
 * @author Hannes
 */
public class ValueRange extends SpinnerNumberModel{
	public ArrayList<Double> values;
	private int currentIndex;
	private double currentValue;
	
	/**
	 * decimals after comma
	 */
	public static int precision = 5; 
//	private double valMin, valMax;
	
	/**
	 * Builds a range with the given step size
	 * @param minValue
	 * @param maxValue
	 * @param stepSize
	 */
	public ValueRange(double minValue, double maxValue, double stepSize) {
		super();
		values = new ArrayList<Double>((int)Math.abs((maxValue-minValue) / stepSize));
		double val = round(minValue);	
		int i = 1;
		while(val < maxValue) {
			values.add(new Double(val));
			val = round(minValue + (double) i * stepSize);
			i++;
		}
		values.add(round(maxValue));
		currentIndex = 0;
		currentValue = round(minValue);
	}
	
	private double round (double num) {
		return Calc.round(num,precision);
	}
	
	/**
	 * Builds a range with the passed number of steps
	 * @param minValue
	 * @param maxValue
	 * @param nrOfSteps
	 */
	public ValueRange(double minValue, double maxValue, int nrOfSteps) {
		super();
		values = new ArrayList<Double>(nrOfSteps);
		double stepSize = (maxValue-minValue)/ ((double)nrOfSteps-1);
		double val = round(minValue);
		while(val < maxValue) {
			values.add(new Double(val));
			val = round(val + stepSize);
		}
		values.add(round(maxValue));
		currentIndex = 0;
		currentValue = round(minValue);
	}
	
	/**
	 * Doesnt initiate values
	 * @see ValueRange.addValue()
	 */
	public ValueRange() {
		super();
		values = new ArrayList<Double>();
		currentIndex = 0;
		currentValue = 0.0;
	}
	
	
	public void addValue(double val) {
		values.add(new Double(round(val)));
		currentValue = values.get(0);
	}
	
	/**
	 * Inits a 0/1 range 
	 */
	public void initBoolean() {
		values = new ArrayList<Double>(2);
		values.add(0.0);
		values.add(1.0);
	}

	private void setIndexAndValueAround(double val) {
		int j = 0;
		val = round(val);
		
		if (val <= values.get(0)) {
			j = 0;
			currentValue = values.get(j);
			
		} else if (val >= values.get(values.size()-1)) {
			j = values.size()-1;
			currentValue = values.get(j);
			
		} else {
			for (int i = 0; i < (values.size()-1); i++) {
				if ((values.get(i) <= val) && (values.get(i+1) > val)) {
					j = i;
					currentValue = val;
					break;
				}
			}		
		}
		currentIndex = j;
	}
	
	/////////////////////////////////////
	//implemented methods for spinnerItem
	
	public Object getValue() {
		return new Double(currentValue);
	}

	public void setValue(Object value) {
		Double val = null;
		if (Number.class.isInstance(value)) {
			val = ((Number)value).doubleValue();
		} else {		
			try {
				val = Double.parseDouble((String) value);
			} catch (NumberFormatException e) {
				// do nothing
				throw new IllegalArgumentException("Invalid Value Element");
			}
		}
		if (round(val)!=currentValue) {
			setIndexAndValueAround(val);
			fireStateChanged();
		}
	}

	public Object getNextValue() {
		if (((currentIndex+1) < values.size()) && 
			((currentIndex+1) >= 0)){
			return values.get(currentIndex+1);
			
		} else return null;
	}

	public Object getPreviousValue() {
		if (((currentIndex-1) >= 0) &&
			 (currentIndex-1) < values.size()) {
			return values.get(currentIndex-1);
			
		} else return null;
	}
	
	


	
}

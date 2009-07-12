package algorithm;

import junit.framework.TestCase;
import algorithm.composers.kaida.ImportanceRange;
import algorithm.composers.kaida.ValueRange;
import algorithm.tools.Calc;

public class ValueRangeTest extends TestCase {

	private double round(double num) {
		return Calc.round(num,ValueRange.precision);
	}
	/*
	 * Test method for 'algorithm.ValueRange.ValueRange(double, double, double)'
	 */
	public void testValueRangeDoubleDoubleDouble() {
		ValueRange vr = new ValueRange(0.0,1.0,0.1);
		assertEquals("first value should be 0",0.0,vr.getValue());
		
		for (double f=0.0; f < 1.0; f = round(f+0.1)) {
			vr.setValue(f);
			
			if (f>0.0){
				assertEquals("value should be" + round(f-0.1),round(f-0.1),vr.getPreviousValue());
			}
			assertEquals("value should be " + f,f,vr.getValue());
			
			if (f< 0.9) {
				assertEquals("nextvalue should be " + round(f+0.1),round(f+0.1),vr.getNextValue());
			}
		}
		
		vr.setValue(-1.0);
		assertEquals("setting value of -1 should lead to 0.0",0.0,vr.getValue());
		vr.setValue(2.0);
		assertEquals("setting value of 2 should lead to 1.0",1.0,vr.getValue());
		vr.setValue(0.5);
		assertEquals("setting value of 0.5 should lead to 0.5",0.5,vr.getValue());
		
		for (int i=0;i<10;i++) {
			double val = round((double)i * 0.1);
			vr.setValue(val);
			
			assertEquals("value should be " + val,val,vr.getValue());
		}
		
		
			/*
		assertEquals("next value should be .1",0.1f,vr.getNextValue());
		vr.setValue(0.1f);
		assertEquals("next value should be .2",0.2f,vr.getNextValue());
		vr.setValue(0.2f);
		assertEquals("next value should be .3",0.3f,vr.getNextValue());
		vr.setValue(0.8f);
		assertEquals("next value should be .9",0.9f,vr.getNextValue());
		vr.setValue(0.9f);
		assertEquals("next value should be 1",1f,vr.getNextValue());
		vr.setValue(1.0f);
		assertEquals("value should be 1",1f,vr.getValue());*/
		
	}

	public void testImportanceRange() {
		ValueRange vr;
		vr = new ImportanceRange();
		vr.setValue(0.3);
		assertEquals("next importance after 0.3 should be 0.4", 0.4, vr.getNextValue());

	}
	/*
	 * Test method for 'algorithm.ValueRange.ValueRange(double, double, int)'
	 */
	public void testValueRangeDoubleDoubleInt() {
		ValueRange vr = new ValueRange(0.0f,0.9f,10);

	}

	/*
	 * Test method for 'algorithm.ValueRange.ValueRange()'
	 */
	public void testValueRange() {
		ValueRange vr = new ValueRange();

	}

	/*
	 * Test method for 'algorithm.ValueRange.addValue(double)'
	 */
	public void testAddValue() {

	}

	/*
	 * Test method for 'algorithm.ValueRange.getValue()'
	 */
	public void testGetValue() {

	}

	/*
	 * Test method for 'algorithm.ValueRange.setValue(Object)'
	 */
	public void testSetValue() {

	}

	/*
	 * Test method for 'algorithm.ValueRange.getNextValue()'
	 */
	public void testGetNextValue() {

	}

	/*
	 * Test method for 'algorithm.ValueRange.getPreviousValue()'
	 */
	public void testGetPreviousValue() {

	}

}

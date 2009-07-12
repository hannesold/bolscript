package bolscript;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;

import org.junit.Test;

import bols.tals.LayoutCycle;


public class LayoutCycleTest {

	@Test
	public void fromString () {
		LayoutCycle lc = LayoutCycle.fromString("4, 3");
		assertEquals(4, lc.getRowLength(0));
		assertEquals(3, lc.getRowLength(1));
		assertEquals(4, lc.getRowLength(2));
		assertEquals(3, lc.getRowLength(3));
		
		lc = LayoutCycle.fromString("3, 4, 5");
		assertEquals(3, lc.getRowLength(0));
		assertEquals(4, lc.getRowLength(1));
		assertEquals(5, lc.getRowLength(2));		
	}
	
	@Test
	public void getCoordinates () {
		LayoutCycle lc = LayoutCycle.fromString("3, 4");
		Point c = lc.getCoordinates(0);
		assertEquals(0, c.x);
		assertEquals(0, c.y);
		
		c = lc.getCoordinates(2);
		assertEquals(2, c.x);
		assertEquals(0, c.y);
		
		c = lc.getCoordinates(3);
		assertEquals(0, c.x);
		assertEquals(1, c.y);

		c = lc.getCoordinates(7);
		assertEquals(0, c.x);
		assertEquals(2, c.y);

	}
	
	@Test
	public void getExactDimensions() {
		LayoutCycle lc = LayoutCycle.fromString("3, 4, 5");
		
		
		Dimension d = lc.getExactDimensions(1);
		Point c = lc.getCoordinates(0);
		System.out.println(c);
		assertEquals(1, d.width);
		assertEquals(1, d.height);

		d = lc.getExactDimensions(3);
		assertEquals(3, d.width);
		assertEquals(1, d.height);
		
		d = lc.getExactDimensions(4);
		assertEquals(3, d.width);
		assertEquals(2, d.height);
		
		d = lc.getExactDimensions(7);
		assertEquals(4, d.width);
		assertEquals(2, d.height);
		
		d = lc.getExactDimensions(8);
		assertEquals(4, d.width);
		assertEquals(3, d.height);

		d = lc.getExactDimensions(12);
		assertEquals(5, d.width);
		assertEquals(3, d.height);
		
		lc = new LayoutCycle(16);
		d = lc.getExactDimensions(16);
		assertEquals(16, d.width);
		assertEquals(1, d.height);
	}
	
}

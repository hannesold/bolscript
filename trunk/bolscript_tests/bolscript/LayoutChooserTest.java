package bolscript;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.junit.Test;

import bols.tals.LayoutChooser;
import bols.tals.LayoutCycle;
import bols.tals.Tal;
import bols.tals.Teental;


public class LayoutChooserTest {

	@Test
	public void getLayoutCyclesFromTeental () throws Exception {
		Tal teental = new Teental();
		
		LayoutChooser lc = teental.getLayoutChooser();
		LayoutCycle c = lc.getLayoutCycle(10, 100);
		
		System.out.println(lc);
		System.out.println(c);
		
		int nrOfCells = 16;
		int displayable = 16;
		c = lc.getLayoutCycle(displayable, 1000);
		System.out.println("chosen: " + c);
		Dimension d = c.getExactDimensions(nrOfCells);
		assertEquals(d.width, 16);
		assertEquals(d.height, 1);
		
		
		
	}
}

package basics;

import static org.junit.Assert.*;

import org.junit.Test;


public class ToolsTest {

	@Test
	public void testFormatName() {
		String s = "--TA tA, taTa-Dha";
		assertEquals("--Ta Ta, Tata-Dha", Tools.formatName(s));
	}
	
}

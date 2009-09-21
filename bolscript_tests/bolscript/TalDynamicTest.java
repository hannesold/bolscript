package bolscript;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import basics.FileReadException;
import bols.BolBase;
import bols.tals.TalBaseDefault;
import bols.tals.TalDynamic;

public class TalDynamicTest {
	BolBase bolBase;
	
	@Before
	public void setUp() throws Exception {
	   bolBase = new BolBase();
	}
	
	
	@Ignore
	public void testFromFile() throws FileReadException{
		TalDynamic tal = new TalDynamic("/Users/hannes/Desktop/teental.tal.txt", TalBaseDefault.getStandard());
		System.out.println(tal.getName());
		System.out.println(tal.getLength());
		//System.out.println(tal.getLayoutChooser());
		System.out.println(tal.getTheka());
		System.out.println(tal.getVibhagsAsString());
	}
	
	@Test
	public void testFromString() {
		String input = "Gharana: Punjab\n"+
		"Type:Tal\n"+
		"Name: Dadra\n"+
		"\n"+
		"Tal: Dadra\n"+
		"\n"+
		"Theka:\n"+
		"Dha Tin Na Na Dhin Na\n"+
		"\n"+
		"Vibhags:\n"+
		"3 + 3K\n"+
		"\n"+
		"Length:\n"+
		"6\n"+
		"\n"+
		"Layout:\n"+
		
		"3 3\n"+
		"6 6\n";
		TalDynamic tal = new TalDynamic(input, TalBaseDefault.getStandard());
		System.out.println(tal.toString());
		System.out.println(tal.getVibhagsAsString());
		System.out.println(tal.getLayoutChooser());
		
	}

}

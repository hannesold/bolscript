package bolscript.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import basics.Debug;
import bols.BolBase;
import bolscript.packets.Packets;


public class ParserTest {

	@BeforeClass
	public static void setup(){
		Debug.init();
		BolBase.getStandard();
	}
	@Ignore
	public void publicTestPacketTextRefs() {
		//doesnt work!
		String input =  "Bla:\n" +
						"dha ge ti te\n"+
						"Blabla:\n" +
						"ta ge ti te\n";
		Packets packets = Parser.splitIntoPackets(input);
		assertEquals("B", input.substring(
				packets.get(0).getTextReference().start(),
				packets.get(0).getTextReference().start()+1));
		//assertEquals("")
		assertEquals(0, packets.get(0).getTextReference().start());
		assertEquals(0, packets.get(0).getTextRefKey().start());
		
		
		assertEquals("d", input.substring(packets.get(0).getTextRefValue().start(),packets.get(0).getTextRefValue().start()+1));
		assertEquals("t", input.substring(packets.get(1).getTextRefValue().start(),packets.get(1).getTextRefValue().start()+1));
		//assertEquals(5, packets.get(0).getTextRefValue().start());

	}
	
	@Test
	public void testUpdatePackets() {
		String input =  "Bla:\n" +
		"dha ge ti te\n"+
		"Blabla:\n" +
		"ta ge ti te\n";
		Packets packets = Parser.compilePacketsFromString(input);
		Packets newPackets = Parser.updatePacketsFromString(packets, input);
		assertEquals(packets.size(), newPackets.size());
		for (int i=0; i < newPackets.size();i++) {
			assertSame(packets.get(i), newPackets.get(i));
		}
		
		String input2 = "Bla:\n"+
		"dha ge ti te\n"+
		"Speed: 2\n"+
		"Blabla:\n" +
		"ta ge ti te\n";
		packets = Parser.compilePacketsFromString(input);
		newPackets = Parser.updatePacketsFromString(packets, input2);
		assertEquals(packets.size()+1, newPackets.size());
		//assertEquals(packets.get(packets.size()-1),newPackets.get(newPackets.size()-1));
		assertEquals(packets.get(packets.size()-1),newPackets.get(newPackets.size()-1));
		
	}
}

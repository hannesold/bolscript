package bolscript.scanner;

import static org.junit.Assert.*;

import org.junit.Test;

import bolscript.packets.Packets;


public class ParserTest {

	@Test
	public void publicTestPacketTextRefs() {
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
}

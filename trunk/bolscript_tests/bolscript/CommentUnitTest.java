package bolscript;

import static org.junit.Assert.*;

import org.junit.Test;

import bolscript.Reader;
import bolscript.sequences.FootnoteUnit;

public class CommentUnitTest {

	@Test
	public void testCommentUnitString() throws Exception {
		
		String s = Reader.getFootnoteCode(0, 1, 2);
		FootnoteUnit c = new FootnoteUnit(s, null, "");
		assertEquals(c.packetNr, 0);
		assertEquals(c.footnoteNrInPacket, 1);
		assertEquals(c.footnoteNrGlobal, 2);
	}

}

package bolscript.scanner;

import static bolscript.sequences.Representable.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import basics.Debug;
import bolscript.sequences.Representable;

public class SequenceScannerTest {

	
	public void testSeveralInputsForSameTokenAssignment (String[] inputs, int[] expectedTokens) {
		for (String input: inputs) {
			Debug.temporary(this, "scanning: " + input);
			SequenceScanner scanner = new SequenceScanner(input);
		for (int i=0; i < expectedTokens.length; i++) {
			
			SequenceToken token;
			try {
				token = scanner.nextToken();
			} catch (IOException e) {
				token = null;
				e.printStackTrace();
			}
			assertNotNull(token);
			Debug.temporary(this, "token nr." + i + ": '" + token.text +"'");
			assertEquals(expectedTokens[i], token.type);
			
		}
		}
	}
	
	@Test
	public void testTokens() throws Exception{
		String[] inputs = new String[]{
				"Dha Ge ,dhin  ( 4! Dha )x3<3",
				"Dha Ge, dhin  ( 4\n! Dha )x 3 < 3",
				"Dha Ge ,dhin ( 4! Dha ) x   \n3<3",
				"Dha  Ge,, 	dhin  ( 4 ! Dha )x3<3",
				"           Dha	 Ge ,, ,,dhin  ( 4! Dha )x3<3"
		};
		int[] expectedTokens = new int[]{BOL_CANDIDATE,
										BOL_CANDIDATE,
										COMMA,
		                               BOL_CANDIDATE, 
		                               BRACKET_OPEN, 
		                               SPEED, 
		                               BOL_CANDIDATE, 
		                               BRACKET_CLOSED, 
		                               KARDINALITY_MODIFIER};
		
		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens);
		
		inputs = new String[]{
				"Dha ( Ge dhin \n ( 4! Dha )x3<3 4 )",
				"Dha (Ge dhin \n ( 4! Dha )x 3 < 3 4)",
				"Dha( Ge dhin \n ( 4! Dha ) x   3<3 4   )",
				"Dha ( Ge 	dhin \n ( 4 ! Dha )x3<3 4  )  ",
				"           Dha		(Ge dhin \n( 4! Dha )x3<3   4)"
		};
		expectedTokens = new int[]{	   BOL_CANDIDATE,
									   BRACKET_OPEN,
									   BOL_CANDIDATE,
		                               BOL_CANDIDATE, 
		                               LINE_BREAK,		                               
		                               BRACKET_OPEN, 
		                               SPEED, 
		                               BOL_CANDIDATE, 
		                               BRACKET_CLOSED, 
		                               KARDINALITY_MODIFIER,
		                               SPEED, 
		                               BRACKET_CLOSED};
		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens);
		
		
		inputs = new String[]{
		" Dha Ge x2\nKi Te\n Ge Na x2"};
		expectedTokens = new int[]{	BOL_CANDIDATE,
				BOL_CANDIDATE, KARDINALITY_MODIFIER,LINE_BREAK,BOL_CANDIDATE, BOL_CANDIDATE, LINE_BREAK, BOL_CANDIDATE, BOL_CANDIDATE, KARDINALITY_MODIFIER};
		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens);
		
		String input = "3 3! 3  !";
		SequenceScanner scanner = new SequenceScanner(input);
		
		assertEquals("3", scanner.nextToken().text);
		assertEquals("3!", scanner.nextToken().text);
		assertEquals("3  !", scanner.nextToken().text);
		
		
	}
}

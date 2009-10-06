package bolscript.scanner;

import static bolscript.sequences.Representable.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import basics.Debug;
import bolscript.sequences.Representable;

public class SequenceScannerTest {


	public void testSeveralInputsForSameTokenAssignment (String[] inputs, int[] expectedTokens, boolean ignoreWhiteSpaces) {
		for (String input: inputs) {
			Debug.temporary(this, "scanning: " + input);
			SequenceScanner scanner = new SequenceScanner(input);
			for (int i=0; i < expectedTokens.length; i++) {

				SequenceToken token;
				try {

					token = scanner.nextToken();
					if (ignoreWhiteSpaces) {
						while (token.type == Representable.WHITESPACES) {
							token = scanner.nextToken();
						}
					}
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

		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens, true);

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
		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens, true);


		inputs = new String[]{
				" Dha Ge x2\nKi Te\n Ge Na x2"};
		expectedTokens = new int[]{	BOL_CANDIDATE,
				BOL_CANDIDATE, KARDINALITY_MODIFIER,LINE_BREAK,BOL_CANDIDATE, BOL_CANDIDATE, LINE_BREAK, BOL_CANDIDATE, BOL_CANDIDATE, KARDINALITY_MODIFIER};
		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens, true);

		String input = "3 3! 3  !";
		
		SequenceScanner scanner = new SequenceScanner(input);
		
		assertEquals("3", scanner.nextToken().text);
		scanner.nextToken();
		assertEquals("3!", scanner.nextToken().text);
		scanner.nextToken();
		assertEquals("3  !", scanner.nextToken().text);

		input = "dha ge sihn sun dus";
		
		scanner = new SequenceScanner(input);
		assertEquals("dha", scanner.nextToken().text);
		Debug.temporary(this, scanner.nextToken().text);
		assertEquals("ge", scanner.nextToken().text);
		Debug.temporary(this, scanner.nextToken().text);
		
		assertEquals("sihn", scanner.nextToken().text);
		Debug.temporary(this, scanner.nextToken().text);
		assertEquals("sun", scanner.nextToken().text);
		Debug.temporary(this, scanner.nextToken().text);
		assertEquals("dus", scanner.nextToken().text);

	}

	@Test
	public void testWhiteSpaces() {
		String[] inputs = new String[]{
				"D D D"
		};
		int[] expectedTokens = new int[]{
				BOL_CANDIDATE,
				WHITESPACES,
				BOL_CANDIDATE,
				WHITESPACES,
				BOL_CANDIDATE};

		testSeveralInputsForSameTokenAssignment(inputs, expectedTokens, false);
		
	}
	
}

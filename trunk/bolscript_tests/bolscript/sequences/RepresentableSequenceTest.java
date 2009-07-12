package bolscript.sequences;

import org.junit.Before;
import org.junit.Test;

import basics.Debug;
import basics.Rational;
import bols.BolBase;
import bols.BundlingDepthToSpeedMap;
import bolscript.Reader;
import bolscript.sequences.RepresentableSequence;
import config.Config;

public class RepresentableSequenceTest {

	@Before
	public void init() {
		Debug.init();
		Config.init();
		BolBase.init(this.getClass());
	}
	@Test
	public void testGetBundled() {
		BolBase bb = BolBase.getStandard();
		Debug.setExclusivelyMapped(false);
		RepresentableSequence seq1 = Reader.getRepresentableSequence(" 1! Dhin Dhin 2! Dha Ge 4! Ti Re Ke Te 3! Dha Ti Te Dhe Ti Te 6! Ka Ta Ga Di Ge Na ", new Rational(1), bb);
		Debug.out("sequence1: " + seq1.toString());
		
		Debug.out("\nbundled: " + seq1.getBundled(new Rational(2), true) + "\n\n");
		Debug.out("\nbundled: " + seq1.getBundled(new Rational(1), true) + "\n\n");
		
		RepresentableSequence seq2 = Reader.getRepresentableSequence(" 4! Dha Ge Ti Te Dha Ti ", new Rational(1), bb);
		BundlingDepthToSpeedMap map = Config.getBundlingDepthToSpeedMap(seq2.getMaxSpeed());
		Debug.out("\nbundled directly to targsp 1: " + seq2.getBundled(new Rational(1), true));
		Debug.out("\nbundled to maxDepth: " + seq2.getBundled(map, map.getMaxDepth(), true));
		
	}

}

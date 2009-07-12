package algorithm.composers.tihai;

import bols.SubSequence;

public class TihaiStructure {
	public int trunc, tail;
	public double speed;
	public SubSequence subSequence;
	
	public TihaiStructure(int trunc, int tail, double d, SubSequence subSequence) {
		super();
		this.trunc = trunc;
		this.tail = tail;
		this.speed = d;
		this.subSequence = subSequence;
	}

	@Override
	public String toString() {
		return ("(trunc:" + trunc + ", tail: " + tail +", speed: " + speed +") -> \n" +
				trunc + "+"+tail+","+trunc+"+"+tail+","+trunc+"+"+1 + " = " + (3*trunc+2*tail+1)
				+ "\nseq: " + subSequence);
	}
	
	
	
}

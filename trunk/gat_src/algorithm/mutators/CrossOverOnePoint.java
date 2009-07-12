package algorithm.mutators;

import java.util.ArrayList;

import algorithm.composers.kaida.Individual;
import algorithm.tools.RouletteWheel;
import bols.SubSequence;
import bols.Variation;

public class CrossOverOnePoint extends CrossOverStandard implements CrossOver {

	private double probability;

	
	public CrossOverOnePoint(double probability) {
		super();
		
		label = "Cross-Over";
		description = "A one-point cross-over.";
		
		this.probability = probability;
	}

	public void crossOver(Individual individual1, Individual individual2) throws Exception {

		Variation var1 = individual1.getVariation();
		Variation var2 = individual2.getVariation();
		ArrayList<SubSequence> subSeqs1 = var1.getSubSequences();
		ArrayList<Double> positions1 = var1.getPositions();
		ArrayList<SubSequence> subSeqs2 = var2.getSubSequences();
		RouletteWheel splitPoints = new RouletteWheel();
		//double nr = var2.getSubSequencesPositioned().size();
		
		int nrOfSplitPointsFound = 0;
		int splitPointIndex1 = 0;
		int i = 0;
		for (SubSequence subSeq : subSeqs1) {
			double splitPoint = positions1.get(i);
			//System.out.println("splitpoint Nr." + nrOfSplitPointsFound + " in var1("+splitPointIndex1+")is at position:" + splitPoint);
			//the index of the subSequence in individual2, which starts at the splitpoint
			int splitPointIndex2 = var2.getIndexOfSplitPoint(splitPoint);
			//System.out.println("in other one searchresult:" + splitPointIndex2);
			
			if (splitPointIndex2 > 0) {
				//splitPointIndex2 has to be found (> -1)
				//, not at the beginning (that doesnt change anything)
				//add to list
				try {
					splitPoints.put(1.0f,new SplitPointDesciber(splitPoint, splitPointIndex1, splitPointIndex2));
					nrOfSplitPointsFound++;
				} catch (Exception e) {
					//System.out.println("couldn't add splitPoint to wheel ");
					throw (e);
				}
			}
			splitPointIndex1++;
			i++;
		}
		
		if (nrOfSplitPointsFound>0) {
		// do CrossOver
		try {
			SplitPointDesciber splitPoint = (SplitPointDesciber) splitPoints.getRandom();
			ArrayList<SubSequence> newSubSeqs1;
			ArrayList<SubSequence> newSubSeqs2;
			
			//get first range from var 1
			newSubSeqs1 = new ArrayList<SubSequence>(subSeqs1.subList(0,splitPoint.getIndex1()));
			//get second range from var 2
			newSubSeqs1.addAll(subSeqs2.subList(splitPoint.getIndex2(),subSeqs2.size()));
			
			//get first range from var 2
			newSubSeqs2 = new ArrayList<SubSequence>(subSeqs2.subList(0, splitPoint.getIndex2()));			
			//get second range from var 1
			newSubSeqs2.addAll(subSeqs1.subList(splitPoint.getIndex1(),subSeqs1.size()));
			
			//update variations
			var1.setSubSequences(newSubSeqs1);
			var2.setSubSequences(newSubSeqs2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (e);
		}
		} else {
			//System.out.println("no splitPoints found");
		}		
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public boolean usesAtomicProb() {
		return false;
	}
}

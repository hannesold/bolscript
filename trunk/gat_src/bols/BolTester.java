package bols;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class BolTester extends basics.Basic{
	private BolSequence bolSeq1;
	private ArrayList<BolName> allBolNames;
	
	public BolTester () {
		
	}
	
	public void run () {
		BolBase bolBase;
		try {
			bolBase = new BolBase();

		bolBase.printAll();
		
		Pattern p = Pattern.compile( "," );
		String[] sequenceAsArray = p.split("Dha,Ge,Ti,Ri,Ke,Te,Dha,-,Dha,-,Dha,Ge,Ti,Ri,Ke,Te");
				
		//go through stringarray to build up bol-sequence
		//use BolBase to get real "BolName"s
		bolSeq1 = new BolSequence();
		
		for (int i=0; i < sequenceAsArray.length; i++) {
					
			Bol bol = null;
			BolName bolName = bolBase.getBolName(sequenceAsArray[i]);
			//out("looked for " + bolName);
			if (bolName != null) {
				bol = new Bol(bolName, new PlayingStyle(1,1), null, false); 
				bolSeq1.addBol(bol);
				System.out.println(bol.toString() + " added");
			} else {
				System.out.println(sequenceAsArray[i] + " not found in bolNames");
			}
		}
		
		System.out.println("sequence is now:");
		System.out.println(bolSeq1.toString());
		
		runVariationTest(bolSeq1);
		} catch (Exception e) {
			System.exit(1);
		}
	}
	
	public void run2 () {
		Pattern p = Pattern.compile( " " );
		String[] someBolNames = p.split("Dha Na Dhin Ge Ne Ti Ri Ki Te -");
		
		allBolNames = new ArrayList<BolName>();
		for (int i=0; i < someBolNames.length; i++) {
			allBolNames.add(new BolName(someBolNames[i]));
		}
		
		//make an initial sequence
		//bolSeq1 = new BolSequence("Dha,Ge,Ti,Ri,Ki,Te,Dha,-,Dha,-,Dha,Ge,Ti,Ri,Ki,Te", allBolNames);
		bolSeq1 = new BolSequence();
		
		p = Pattern.compile( "," );
		String[] sequenceAsArray = p.split("Dha,Ge,Ti,Ri,Ki,Te,Dha,-,Dha,-,Dha,Ge,Ti,Ri,Ki,Te");
				
		//go through stringarray to build up bol-sequence
		for (int i=0; i < sequenceAsArray.length; i++) {
			
			boolean found = false;
			Bol currentBol = null;
	
			for (int j=0; j < allBolNames.size(); j++) {
				//find current BolName in allBolNames
				
				//System.out.print("compare: "+sequenceAsArray[i]+ " with " + ((BolName)(allBolNames.get(j))).toString()+" ");
				if (allBolNames.get(j).toString().equals((String)sequenceAsArray[i])) {
					
					//make Bol with speed = 1
					currentBol = new Bol((BolName)allBolNames.get(j), new PlayingStyle(1,1), null, false);
					found = true;
					//break;
				}
			}
			if (found) {
				bolSeq1.addBol(currentBol);
				System.out.println(currentBol.toString() + " added");
			} else {
				System.out.println(sequenceAsArray[i] + " not found in bolNames");
			}
		}
		System.out.println("sequence is now:");
		System.out.println(bolSeq1.toString());
		
		runVariationTest(bolSeq1);
	}
	
	public void runVariationTest (BolSequence bolSeq) {
		
		SubSequenceAdvanced s1 = new SubSequenceAdvanced(bolSeq, 0, 2, new PlayingStyle(1,1));
		SubSequenceAdvanced s2 = new SubSequenceAdvanced(bolSeq, 2, 4, new PlayingStyle(4,1));
		SubSequenceAdvanced s3 = new SubSequenceAdvanced(bolSeq, 2, 4, new PlayingStyle(1,1));
		
		Variation var1 = new Variation(bolSeq);
		Variation var2 = new Variation(bolSeq);
		
		var1.addSubSequence(s1);
		var1.addSubSequence(s2);
		var1.addSubSequence(s3);
		var2.addSubSequence(s1);
		
		System.out.println("variation1:");
		System.out.println(var1.toString());
		System.out.println("variation2:");
		System.out.println(var2.toString());
	}
	
	public static void main (String[] args) {
		System.out.println("hello world");
		BolTester bt = new BolTester();
		bt.run();
	}
}

package bols.tals;

import org.junit.Test;

import bols.BolBase;

public class TalBaseTest {

	@Test
	public void newTalBase() throws Exception{
		BolBase bolBase = new BolBase();
		
		TalBase talBase = new TalBase(bolBase, bolscript.config.Config.pathToTals);
		System.out.println(talBase.getNrOfTals());
		System.out.println(talBase);
	}

}

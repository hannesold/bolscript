package bols.tals;

public class TalBaseDefault implements TalBase {

	static TalBaseDefault standard = new TalBaseDefault();
	
	public static TalBase getStandard() {
		return standard;
	}
	
	public Tal getTalFromName(String name) {
		return null;
	}

}

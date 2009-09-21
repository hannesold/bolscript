package bols.tals;


public class Teental extends bols.tals.TalDynamic implements Tal {

	public static TalDynamic defaultTeental = null;
	
	public Teental() {
		super(TEENTAL, TalBaseDefault.getStandard());
	}
	
	public static Tal getDefaultTeental() {
		if (defaultTeental == null) defaultTeental = new Teental();
		return defaultTeental;
	}
	
	public static String TEENTAL =
		"Type: \n"+
		"Tal \n"+
		""+
		"Gharanas:\n"+
		"Punjab\n"+
		""+
		"Name: \n"+
		"Teental\n"+
		""+
		"Length:\n"+
		"16\n"+
		""+
		"Tal:\n"+
		"Teental\n"+
		""+
		"Theka:\n"+
		"Dha! Dhin Dhin Dha,\n"+
		"Dha! Dhin Dhin Dha, \n"+
		"Na Tin Tin Na, \n"+
		"Na Dhin! Dhin Dha, \n"+
		""+
		"Vibhags:\n"+
		"4 + 4 + 4K + 4\n"+
		""+
		"Layout:\n"+
		"1 1\n"+
		"2 2\n"+
		"4 4\n"+
		"8 8\n"+
		"16 16\n"+
		"32 16\n";


}

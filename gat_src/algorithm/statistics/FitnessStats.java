package algorithm.statistics;

public class FitnessStats {
	public static int SUMMEDFITNESS = 0;
	public static int AVERAGEFITNESS = 1; 
	public static int MINIMUMFITNESS = 2; 
	public static int MAXIMUMFITNESS = 3; 
	public static int STDDEVIATION = 4;
	public static int NrOfKeys = 5;
	
	public static String[] statNames;
	
	static {
		statNames = new String[NrOfKeys];
		statNames[SUMMEDFITNESS] = "Summed Fitness";
		statNames[AVERAGEFITNESS] = "Average Fitness";
		statNames[MINIMUMFITNESS] = "Minimum Fitness";
		statNames[MAXIMUMFITNESS] = "Maximum Fitness";
		statNames[STDDEVIATION] = "Standard Deviation";
	}
	
	private double[] values;

	public FitnessStats() {
		super();
		values = new double[NrOfKeys];
		for (int i=0; i < NrOfKeys; i++) {
			values[i] = 0f;
		}
		// TODO Auto-generated constructor stub
	}
	
	public void set(int k, double f) {
		values[k] = f;
	}
	
	public void add(int k, double f) {
		values[k] = values[k] + f;
	}
	
	public double get(int k) {
		return values[k];
	}
	
	public static String getLabel(int i) {
		return statNames[i];
	}

	public String toString() {
		String s = "(";
		for (int k=0; k<NrOfKeys;k++) {
			s += statNames[k] + ": " + values[k] + ((k==(NrOfKeys-1))?")":",");
		}
		return s;
	}
}

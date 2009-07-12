package algorithm.composers.kaida;


public class ImportanceRange extends ValueRange {
	private static final long serialVersionUID = 8824487996833932144L;

	public ImportanceRange() {
		super();
		
		for (int i=0; i < 10;i++) {
			this.addValue(0.01*(double)i);
		}
		for (int i=1; i < 10;i++) {
			this.addValue(0.1*(double)i);
		}
		for (int i=1; i < 11;i++) {
			this.addValue(1*(double)i);
		}
		
	}
}

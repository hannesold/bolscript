package bols;

public class BolPositionedAndWeighted extends Bol {

	public double position = 0f;
	public double weight = 0f;
	
	public BolPositionedAndWeighted(BolName bolName, PlayingStyle style) {
		super(bolName, style);
	}
	
	public BolPositionedAndWeighted(BolName bolName, PlayingStyle style, double position) {
		super(bolName, style);
		this.position = position;
	}
	
	public BolPositionedAndWeighted(BolName bolName, PlayingStyle style, double position, double weight) {
		super(bolName, style);
		this.position = position;
		this.weight = weight;
	}

	public BolPositionedAndWeighted getCopy() {
		return new BolPositionedAndWeighted(bolName, style.getCopy(), position, weight);
		
	}
	
	public boolean equals(Bol bol) {
		return (bol.getBolName() == bolName)&&(bol.getPlayingStyle().equals(style));
	}
	
	public boolean equals(BolPositionedAndWeighted bol) {
		return (bol.getBolName() == bolName)&&(bol.getPlayingStyle().equals(style) &&
				(bol.position == position) && (bol.weight == weight));
	}


	
	
	
}

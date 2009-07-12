package algorithm.mutators;

public class SplitPointDesciber {
	private double splitPoint;
	private int index1;
	private int index2;
	
	public SplitPointDesciber(double point, int index1, int index2) {
		super();
		// TODO Auto-generated constructor stub
		splitPoint = point;
		this.index1 = index1;
		this.index2 = index2;
	}

	public int getIndex1() {
		return index1;
	}

	public void setIndex1(int index1) {
		this.index1 = index1;
	}

	public int getIndex2() {
		return index2;
	}

	public void setIndex2(int index2) {
		this.index2 = index2;
	}

	public double getSplitPoint() {
		return splitPoint;
	}

	public void setSplitPoint(double splitPoint) {
		this.splitPoint = splitPoint;
	}
}

package bolscript.sequences;

public class Unit implements Representable {

	private int type;
	private Object contents;
	
	public Unit(int type, Object contents) {
		super();
		this.type = type;
		this.contents = contents;
	}
	
	public Unit() {
		this.type = Representable.OTHER;
		this.contents = "undetermined";
	}

	public Object getContents() {
		return contents;
	}

	public void setContents(String Object) {
		this.contents = contents;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}
	
	public String toString() {
		return contents.toString();
	}

}

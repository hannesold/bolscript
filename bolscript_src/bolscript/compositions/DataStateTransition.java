package bolscript.compositions;

public interface DataStateTransition {
	public void connect(DataStatePosessor d);
	public void connectfailed(DataStatePosessor d);
	public void save(DataStatePosessor d);
	public void close(DataStatePosessor d);
	public void open(DataStatePosessor d);
	public void remove(DataStatePosessor d);
	public void delete(DataStatePosessor d);
	
}

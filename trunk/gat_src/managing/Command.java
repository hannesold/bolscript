package managing;

public class Command {
	public static final int FillNextCycle = 0;
	public static final int AddCurrentIndividualToPlaylist = 1;
	public static final int HighLightLastIndividual = 2;
	public static final int HighLightCell = 3;
	public static final int SetComposer = 4;
	public static final int MuteComposer = 5;
	public static final int PausePlayback = 6;
	public static final int ContinuePlayback = 7;
	public static final int RestartComposer = 8;
	
	/**
	 * When a cycle is completed. No further arguments needed.
	 */
	public static final int CycleCompleted = 9;
	
	/**
	 * Pass on to the next Composer (for example after the theka after a tihai) 
	 */
	public static final int PassOnToComposer = 10;
	

	/**
	 * Is called, whenever one evolutionstep is done.
	 * One Argument is needed: the composer.
	 * progressbar is updated
	 */
	public static final int OneEvolutionStepDone = 11;
	
	/**
	 * Is called when the composer shall be notified to start preparing the next cycle
	 */
	public static final int PrepareNextCycle = 12; 
	
	
	private boolean valid;
	private int type;
	
	private Object[] args;
	
	public Command (int type, Object ... args) {
		this.type = type;
		this.args = args;
		this.valid = true;
	}
	
	public synchronized boolean isValid() {
		return valid;
	}

	public synchronized void setValid(boolean valid) {
		this.valid = valid;
	}

	public synchronized Object[] getArgs() {
		return args;
	}

	public synchronized int getType() {
		return type;
	}
	
	public String toString() {
		return "Command("+type+")";
	}
	
}

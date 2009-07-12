package bolscript.compositions;

import java.util.EventObject;

import basics.Debug;

public class CompositionBaseChangeEvent extends EventObject implements Runnable{

	CompositionBaseListener listener;
	CompositionBase compBase;
	public final static int DATA = 0;
	public final static int VIEW = 1;
	private int type;
	
	public CompositionBaseChangeEvent(Object source) {
		super(source);
	}
	
	public CompositionBaseChangeEvent(Object source, CompositionBase compBase, CompositionBaseListener listener, int type) {
		super(source);
		this.compBase = compBase;
		this.listener = listener;
		this.type = type;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public CompositionBase getCompositionBase () {
		return compBase;
	}
	private static final long serialVersionUID = -2782158964570855469L;


	public void run() {
		Debug.debug(this, " sending to " + listener);
		listener.compositionBaseChanged(this);
	}

}

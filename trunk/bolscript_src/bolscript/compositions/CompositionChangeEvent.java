package bolscript.compositions;

import java.util.EventObject;

import basics.Debug;

public class CompositionChangeEvent extends EventObject implements Runnable{

	CompositionChangedListener listener;
	Composition comp;
	
	public CompositionChangeEvent(Object source) {
		super(source);
	}
	
	public CompositionChangeEvent(Composition comp, CompositionChangedListener listener) {
		super(comp);
		this.comp = comp;
		this.listener = listener;
	}


	public Composition getComposition () {
		return comp;
	}
	private static final long serialVersionUID = -2782158964570855469L;


	public void run() {
		//Debug.debug(this, " sending to " + listener);
		listener.compositionChanged(this);
	}

}

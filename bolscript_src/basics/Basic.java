package basics;

/**
 * This class is extended in some old gat classes,
 * it should however be replaced by statically using the Debug class,
 * or creating local Debug objects.
 * @author hannes
 * @see Debug
 *
 */
@Deprecated
public class Basic {

	/**
	 * @param args
	 */
	public static boolean DEBUG_GLOBAL = true;
	protected boolean DEBUG;
	
	public Basic() {
		// TODO Auto-generated method stub
		DEBUG=true;
	}
	
	protected void out(Object message)
	{
		if ((DEBUG)&&(DEBUG_GLOBAL)) {
			Debug.debug(this, message);
		}
	}

}

package bolscript.compositions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;

public class MetaValues extends HashMap<Integer, Object> {
	private HashMap<Integer, Object> values;
	
	public MetaValues() {
		PacketType[] metaTypes = PacketTypeFactory.getMetaTypes();
		values = new HashMap<Integer,Object>(metaTypes.length);
		clear();
	}
	
	
	public void clear() {
		values.clear();
	}
	
	public void addString(Integer key, String string) {
		if (get(key) == null) {
			put(key, new ArrayList<String>());
		} 
		ArrayList<String> meta = (ArrayList<String>) get(key);
		if (!meta.contains(string)) {
			meta.add(string);
			Collections.sort(meta);
		}
	}
}

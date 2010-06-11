package bolscript.compositions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import basics.Debug;
import basics.Tools;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.packets.types.PacketType.StorageType;

public class MetaValues {
	
	private HashMap<Integer, Object> values;
	
	public MetaValues() { 
		PacketType[] metaTypes = PacketTypeDefinitions.getMetaTypes();
		values = new HashMap<Integer,Object>(metaTypes.length);
		clear();
	}

	public void clear() {
		values.clear();
	}
	
	public void addString(Integer key, String string) {
		if (values.get(key) == null) {
			values.put(key, new ArrayList<String>());
		} 
		ArrayList<String> meta = (ArrayList<String>) values.get(key);
		if (!meta.contains(string)) {
			meta.add(string);
			Collections.sort(meta);
		}
	}
	
	public void setString(Integer key, String string) {
		if (PacketTypeDefinitions.getType(key).getStorageType() == StorageType.STRING) {
			values.put(key,string);
		} else {
			Debug.critical(this, PacketTypeDefinitions.getType(key).getDisplayNameSingular() +" is not supposed to be stored as string");
		}
	}
	
	public void setList(Integer key, ArrayList<String> list) {
		if (PacketTypeDefinitions.getType(key).getStorageType() == StorageType.STRINGLIST) {
			values.put(key,list);
		} else {
			Debug.critical(this, PacketTypeDefinitions.getType(key).getDisplayNameSingular() + " is not supposed to be stored as list");
		}
	}
	public String getString(Integer key) {
		return (String) values.get(key);
	}
	
	public String makeString(Integer key) {
		if (PacketTypeDefinitions.getType(key).getStorageType() == StorageType.STRINGLIST) {
			return Tools.toString(getList(key));
		} else if (PacketTypeDefinitions.getType(key).getStorageType() == StorageType.STRING){
			return getString(key);
		} else return "";
	}
	
	public ArrayList<String> getList(int key) {
		return (ArrayList<String>) values.get(key);
	}


	public void setDefault() {
		for (int i = 0; i< PacketTypeDefinitions.nrOfTypes; i++) {
			if (PacketTypeDefinitions.getType(i).getStorageType() == StorageType.STRINGLIST) {
				setList(i, new ArrayList<String>());
			} else if (PacketTypeDefinitions.getType(i).getStorageType() == StorageType.STRING){
				setString(i, "");
			}
		}
	}
	
}

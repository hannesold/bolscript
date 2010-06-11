package bolscript.filters;

import java.util.ArrayList;

import bolscript.compositions.Composition;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.packets.types.PacketType.StorageType;

public class MetaValueFilter extends StringArrayFilter implements Filter {

	protected int packetTypeId;
	protected PacketType packetType;
	
	/**
	 * Generates a metaValueFilter to a given PacketType id
	 * @param packetType
	 */
	public MetaValueFilter (int packetTypeId) {
		super();
		this.packetTypeId = packetTypeId;
		this.packetType = PacketTypeDefinitions.getType(packetTypeId);
	}
	
	
	@Override
	protected ArrayList<String> getSamples(Composition comp) {
		switch (packetType.getStorageType()) {
		case STRING: 
			ArrayList<String> samples = new ArrayList<String>(1);
			samples.add(comp.getMetaValues().getString(packetTypeId));
			return samples;
		case STRINGLIST:
			return new ArrayList<String>(comp.getMetaValues().getList(packetTypeId));
		default:
			return new ArrayList<String>();
		}
	}

	public String getFilterHeader() {
		return packetType.getDisplayNameFilter();
	}
}

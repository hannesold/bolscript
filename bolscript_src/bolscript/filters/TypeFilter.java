package bolscript.filters;

import java.util.ArrayList;

import bolscript.compositions.Composition;

public class TypeFilter extends StringArrayFilterGeneral implements Filter {

	
	public TypeFilter () {
		super();
	}
	public TypeFilter (String type){
		super(type);
	}
	public TypeFilter (String[] types){
		super(types);
	}
	
	@Override
	public ArrayList<String> getSamples(Composition comp) {
		ArrayList<String> types = new ArrayList<String>();
		types.addAll(comp.getTypes());
		return types;
	}


}

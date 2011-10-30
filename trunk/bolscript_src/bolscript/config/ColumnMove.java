package bolscript.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ColumnMove {
	
	@XmlAttribute
	public int fromIndex;
	
	@XmlAttribute
	public int toIndex;
}

package bolscript.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TableDisplaySetting {
	@XmlElement
	public String columnname;
	@XmlElement
	public int columnindex;
	@XmlElement
	public int preferredWidth;
	
	public TableDisplaySetting(){}
	public TableDisplaySetting(String columnkey, int columnindex, 
			int preferredWidth) {
		super();
		this.columnname= columnkey;
		this.columnindex = columnindex;
		this.preferredWidth = preferredWidth;			
	}		
}

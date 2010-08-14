package bolscript.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionInfo {
	
	private int buildNumber;
	private String versionNumber = null;
	private String completeVersionString = null;
	private String builtForOperatingSystem = null;
	private Date buildDate = null;
	
	public VersionInfo(Manifest manifest) {
		
		try {
			Attributes attributes = manifest.getMainAttributes();	
			HashMap<String, String> keyValMap = new HashMap<String,String>();
			for (Object keyObject: attributes.keySet()) {
				String key = keyObject.toString();
				String val = attributes.getValue(keyObject.toString());
				keyValMap.put(key, val);
			}
			
			if (keyValMap.containsKey(ManifestKeys.versionNumber)) {
				versionNumber = keyValMap.get(ManifestKeys.versionNumber);
			}
			if (keyValMap.containsKey(ManifestKeys.buildNumber)) {
				try {
					buildNumber = Integer.parseInt(keyValMap.get(ManifestKeys.buildNumber));	
				} catch (Exception ex){}
			}
			if (keyValMap.containsKey(ManifestKeys.completeVersionString)) {
				completeVersionString = keyValMap.get(ManifestKeys.completeVersionString);
			}
			if (keyValMap.containsKey(ManifestKeys.builtForOperatingSystem)) {
				builtForOperatingSystem = keyValMap.get(ManifestKeys.builtForOperatingSystem);
			}
			if (keyValMap.containsKey(ManifestKeys.buildDate)) {
				try {
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					buildDate = format.parse(keyValMap.get(ManifestKeys.buildDate));
				} catch (Exception ex) {
					
				}
			}
		} catch (Exception ex) {}
		
	}
	public int getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}
	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	public String getCompleteVersionString() {
		return completeVersionString;
	}
	public void setCompleteVersionString(String completeVersionString) {
		this.completeVersionString = completeVersionString;
	}
	public String getBuiltForOperatingSystem() {
		return builtForOperatingSystem;
	}
	public void setBuiltForOperatingSystem(String builtForOperatingSystem) {
		this.builtForOperatingSystem = builtForOperatingSystem;
	}
	public Date getBuildDate() {
		return buildDate;
	}
	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}
	
}

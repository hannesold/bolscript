package bolscript.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionInfo {
	
	private int buildNumber;
	private String versionNumber = null;
	private String completeVersionString = null;
	//private String builtForOperatingSystem = null;
	private int operatingSystem = Config.WINDOWS;
	private Date buildDate = null;
	
	public VersionInfo() {
	
	}
	
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
				String opSysAsString = keyValMap.get(ManifestKeys.builtForOperatingSystem);
				operatingSystem = parseOperatingSystem(opSysAsString);
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
	public static int parseOperatingSystem(String opSysAsString) {
		String regexForWindows = "(W|w)(i|I)(n|N).*";
		String regexForMac = "OSX.*|OS X.*|Mac.*|mac.*";
		if (opSysAsString.matches(regexForWindows)) {
			return Config.WINDOWS;
		} else if (opSysAsString.matches(regexForMac)) {
			return Config.MAC;
		} else return Config.WINDOWS;
	}
	
	public String getOperatingSystemAsString() {		
		if (operatingSystem == Config.MAC) return "OSX";
		return "WINDOWS";		
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
	public int getBuiltForOperatingSystem() {
		return operatingSystem;
	}
	public void setBuiltForOperatingSystem(int operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public Date getBuildDate() {
		return buildDate;
	}
	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}
	
}

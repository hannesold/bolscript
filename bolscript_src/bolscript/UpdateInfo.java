package bolscript;

import basics.Debug;
import bolscript.config.VersionInfo;

public class UpdateInfo {

	public UpdateInfo() {
		Debug.temporary(this, "UpdateInfo constructed");
	}
	private String changelog;
	private String downloadLink;
	
	public enum VersionState {
		HasUpdates,
		OK,
		CouldNotCheck
	}
	
	private VersionState result;
	
	private Download download;
	
	private VersionInfo currentVersion;
	
	public VersionInfo getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(VersionInfo currentVersion) {
		this.currentVersion = currentVersion;
	}
	public VersionState getResult() {
		return result;
	}
	
	private Exception error;
	
	public Exception getError() {
		return error;
	}
	public void setError(Exception errorMessage) {
		this.error = errorMessage;
		setResult(VersionState.CouldNotCheck);
	}
	
	public void setResult(VersionState result) {
		this.result = result;
	}
	public String getChangelog() {
		return changelog;
	}
	public boolean hasChangeLog() {
		if (changelog != null) {
			return !changelog.isEmpty();	
		} 
		return false;
	}
	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}
	public String getDownloadUrl() {
		return downloadLink;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadLink = downloadUrl;
	}
	
	public Download getDownload() {
		return download;
	}
	public void setDownload(Download download) {
		this.download = download;
	}
}

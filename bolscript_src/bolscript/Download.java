package bolscript;

import java.util.Date;

public class Download {
	private String downloadLink;
	private String detailsLink;
	private String title;
	private String version;
	private String buildNr;
	private String operatingSystem;
	private Date date;
	
	public String getDownloadLink() {
		return downloadLink;
	}
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	public String getDetailsLink() {
		return detailsLink;
	}
	public void setDetailsLink(String detailsLink) {
		this.detailsLink = detailsLink;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBuildNr() {
		return buildNr;
	}
	public void setBuildNr(String buildNr) {
		this.buildNr = buildNr;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}

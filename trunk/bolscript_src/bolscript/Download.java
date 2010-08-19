package bolscript;

import java.util.Date;

import bolscript.config.VersionInfo;

public class Download {
	private String downloadLink;
	private String detailsLink;
	private String title;
	private Date date;
	private VersionInfo versionInfo;
	
	public VersionInfo getVersionInfo() {
		return versionInfo;
	}

	public Download() {
		this.versionInfo = new VersionInfo();
	}
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}

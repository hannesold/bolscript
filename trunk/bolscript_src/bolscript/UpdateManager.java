package bolscript;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Tools;
import bolscript.Download.DownloadTypes;
import bolscript.config.Config;
import bolscript.config.VersionInfo;
import bolscript.scanner.Parser;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


public class UpdateManager {

	private static final int connectionTimeOut = 3000;
	private static final int readTimeOut = 3000;
	private static final String downloadsUrl = "http://code.google.com/p/bolscript/downloads/list";
	private static final String downloadsFeed = "http://code.google.com/feeds/p/bolscript/downloads/basic";
	
	UpdateInfo updateInfo;

	public UpdateManager () {
		updateInfo = new UpdateInfo();		
	}

	/**
	 * Queries the download feed and fills the update info.
	 */
	public void CheckForUpdates() {
		VersionInfo currentVersionInfo;
		try {
			currentVersionInfo = Config.getVersionInfoFromJar(Config.getJarPath());		
			updateInfo.setCurrentVersion(currentVersionInfo);
			if (currentVersionInfo.getOperatingSystem() == Config.OperatingSystems.Unknown) {
				currentVersionInfo.setOperatingSystem(VersionInfo.getRunningOperatingSystem());
			}
			currentVersionInfo.setBuildNumber(1000);
		} catch (Exception e) {			
			updateInfo.setError(new Exception("Could not determine current Version..."));
			return;
		}

		ArrayList<Download> downloads = new ArrayList<Download>();
		URL url = null;
		URLConnection connection = null;
		try {
			
			url = new URL(downloadsFeed);
			connection = url.openConnection();			
			connection.setConnectTimeout(connectionTimeOut);
			connection.setReadTimeout(readTimeOut);
		} catch (Exception e) {
			updateInfo.setError(new MalformedURLException("Could not find download url..."));
			return;
		}

		try {					
			SyndFeedInput input = new SyndFeedInput();		
			XmlReader reader = new XmlReader(connection);
			SyndFeed feed = input.build(reader);

		//	System.out.println(feed);

			List entries = feed.getEntries();			    
			for (Object entryObj : entries) {
				SyndEntry entry = (SyndEntry) entryObj;
				try {
					downloads.add(parseDownload(entry));
				} catch (ParseException e) {						
					e.printStackTrace();
				}	
			}			    

		} catch (Exception e) {
			updateInfo.setError(new Exception("Could not contact downloads page...",e));
			return;
		}

		//determine fitting download
		Download bestCandidate = null;
		Download changelog = null;
		for (Download download : downloads) {
			if (download.getVersionInfo() != null) {
				if (download.getType() == Download.DownloadTypes.ChangelogHtml) {
					if (changelog == null) {
						changelog = download; 
						} else if (download.getVersionInfo().getBuildNumber()
								> changelog.getVersionInfo().getBuildNumber()) {
							changelog = download;
						}
											
				} else if (download.getVersionInfo().getOperatingSystem() 
						== currentVersionInfo.getBuiltForOperatingSystem()) {
					if (bestCandidate == null) {
						bestCandidate = download;
					} else if (download.getVersionInfo().getBuildNumber() 
							> bestCandidate.getVersionInfo().getBuildNumber()) {
						bestCandidate = download;
					}
				}
			}
		}

		//check for changelog
		String changelogContent = null;
		//download changelog
		if (changelog != null) {
			try {
				URL changelogUrl = Tools.URIFromDangerousPlainTextUrl(changelog.getDownloadLink()).toURL();							
				URLConnection connect = changelogUrl.openConnection();
				connect.setConnectTimeout(connectionTimeOut);
				connect.setReadTimeout(readTimeOut);
				
				InputStream contentStream = connect.getInputStream();
				changelogContent = Tools.inputStreamToString(contentStream);
				updateInfo.setChangelog(stripToRelevantContent(changelogContent));

			} catch (Exception e) {
				//could not download changelog.
			}
		}
		
		if (bestCandidate == null) {
			//fehler
			updateInfo.setResult(UpdateInfo.VersionState.CouldNotCheck);
			updateInfo.setError(new Exception("Could not find an apropriate download."));
			updateInfo.setDownloadUrl(downloadsUrl);
			return;
			
		} else if (bestCandidate.getVersionInfo().getBuildNumber()
					<=	currentVersionInfo.getBuildNumber()) {
			//everything is up to date
			updateInfo.setResult(UpdateInfo.VersionState.OK);
			updateInfo.setDownloadUrl(downloadsUrl);
			return;
			
		} else {				
				
			
			updateInfo.setResult(UpdateInfo.VersionState.HasUpdates);
			updateInfo.setDownloadUrl(bestCandidate.getDetailsLink());
			updateInfo.setDownload(bestCandidate);
			return;
		}


	}

	private Download parseDownload(SyndEntry entry) throws ParseException{
		try {
			String link = entry.getLink();
			String title = entry.getTitle();

			Download download = new Download();			        
			download.setDetailsLink(link);
			download.setTitle(title);
			download.setDate(entry.getUpdatedDate());

			//read main contents of the entry
			String contents = ((SyndContent) entry.getContents().get(0)).getValue();

			//extract the download link
			String downloadLinkRegexString = "(?<=" + Pattern.quote("href=\"") +")"+ "([^\"]+)";
			Pattern downloadLinkPattern = Pattern.compile(downloadLinkRegexString);
			Matcher matcher = downloadLinkPattern.matcher(contents);
			while (matcher.find()){        
				download.setDownloadLink(matcher.group(1));
			}

			//extract the version info
			try {
				VersionInfo versionInfo = parseVersionInfo(contents);
				download.setVersionInfo(versionInfo);
			} catch (ParseException e) {

			}
			//set the download type
			download.setType(parseDownloadType(download));

			return download;

		} catch (Exception ex) {
			throw new ParseException(entry.toString(), -1);
		}		                    
	}

	private DownloadTypes parseDownloadType(Download download) {
		Pattern changelogRegex = Pattern.compile("(?i)change");
		Pattern dmgPattern = Pattern.compile(".*(?i)dmg$");
		Pattern zipPattern = Pattern.compile(".*(?i)zip$");

		if (changelogRegex.matcher(download.getTitle()).find()) {
			return Download.DownloadTypes.ChangelogHtml;
		}
		if (dmgPattern.matcher(download.getDownloadLink()).find()) 
			return Download.DownloadTypes.Dmg;
		if (zipPattern.matcher(download.getDownloadLink()).find()) 
			return Download.DownloadTypes.Zip;

		return Download.DownloadTypes.Unknown;
	}

	private VersionInfo parseVersionInfo(String contents) throws ParseException {
		String versionNrAndBuildRegexString = "(\\d\\.\\d+)(?:\\s|build|Build|b|\\.)+(\\d+)";
		int versionNrGroupIndex=1;
		int buildNrGroupIndex=2;
		Pattern versionNrAndBuildRegex = Pattern.compile(versionNrAndBuildRegexString);
		Matcher versionMatcher = versionNrAndBuildRegex.matcher(contents);
		boolean versionFound = versionMatcher.find();

		if (versionFound) {
			VersionInfo versionInfo = new VersionInfo();
			versionInfo.setVersionNumber(versionMatcher.group(versionNrGroupIndex));
			versionInfo.setBuildNumber(Integer.parseInt(versionMatcher.group(buildNrGroupIndex)));

			Pattern opSysPattern = Pattern.compile("OpSys-(Windows|OSX)");
			Matcher opSysMatcher = opSysPattern.matcher(contents);
			if (opSysMatcher.find()){
				versionInfo.setOperatingSystem(VersionInfo.parseOperatingSystem(opSysMatcher.group(1)));
			}

			return versionInfo;
		} else {
			throw new ParseException(contents, 0);
		}
	}

	public String stripToRelevantContent(String changeLog) {
		String styleRemover = Pattern.quote("<style")+ "(.|"+Parser.N+")*"+Pattern.quote("/style>");
		String stripped = changeLog.replaceAll(styleRemover, "");		
		stripped = stripped.replaceAll(Pattern.quote("body>"), "body style=\"font-size: 9px;\">");
		return stripped;
	}
	public UpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(UpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
	}
}
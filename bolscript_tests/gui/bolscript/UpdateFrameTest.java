package gui.bolscript;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;

import org.junit.Test;

import basics.FileManager;
import basics.FileReadException;
import bolscript.UpdateInfo;
import bolscript.config.Config;
import bolscript.config.GuiConfig;


public class UpdateFrameTest {
	UpdateFrame updateFrame;
	UpdateInfo updateInfo;
	
	@Test
	public void testUpdateFrame() {
		File file = new File("/Users/hannes/Projekte/Workspace/bolscript googlecode/Changelog.html");
		String changelog = "";
		try {
			changelog = FileManager.getContents(file, Config.compositionEncoding);
		} catch (FileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateInfo = new UpdateInfo();
		updateInfo.setChangelog(changelog);
		updateInfo.setDownloadUrl("blub");
		
		updateFrame = new UpdateFrame();
		
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				updateFrame.displayInfo(updateInfo);
				updateFrame.setPreferredSize(new Dimension(500,300));
				GuiConfig.setVisibleAndAdaptFrameLocation(updateFrame);
			}
		});
		
		
	}

}

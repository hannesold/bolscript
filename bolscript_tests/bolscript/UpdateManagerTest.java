package bolscript;

import static org.junit.Assert.*;

import org.junit.Test;

public class UpdateManagerTest {

	@Test
	public void testCheckForUpdates() {
		UpdateManager updateManager = new UpdateManager();
		try {
			updateManager.CheckForUpdates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

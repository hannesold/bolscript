package bolscript.packets.types;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import basics.Debug;

public class HistoryEntryTest {

	@Test
	public void testFromString() {
		try {
			String regexp = "(dddd\\.dd\\.dd\\s*dd:dd)\\s+".replaceAll("d", "\\d");
			regexp = "(\\d\\d\\d\\d\\.\\d\\d\\.\\d\\d\\s*\\d\\d \\d\\d)\\s+";
			Pattern pattern = Pattern.compile(regexp);
			Matcher m = pattern.matcher("2009.12.01 23 33 ");
			assertTrue(m.find());
			
			HistoryEntry entry = HistoryEntry.fromString("2009.12.01 23 33 modified by Hannes");
			Debug.out(entry);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("parse error");
		}
		
	}


	@Test
	public void testParseDateFromBolscript() {
		Debug.init();
		try {
			Date date = HistoryEntry.parseDateFromBolscript("2009.12.01 23 33");
			HistoryEntry entry = new HistoryEntry(date, HistoryOperationType.CREATED, "Hannes");
			
			Debug.out(entry.formatDateForBolscript());
		} catch (ParseException e) {			
			e.printStackTrace();
			fail("parse error");
		}
	}

}

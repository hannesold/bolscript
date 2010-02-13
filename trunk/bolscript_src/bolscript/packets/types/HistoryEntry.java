package bolscript.packets.types;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.config.UserConfig;


public class HistoryEntry {

	

	private int operationTypeId;	
	private String user;
	private Date date;

	public static Comparator getDateComparator() {
		
		return new HistoryEntryDateComparator();
	}
	private static Pattern parserPattern = null;
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH mm");

	static {
		init();
	}
	//needs HistoryOperationType to be initialised
	public static void init() {
		String regexp = "(\\d\\d\\d\\d\\.\\d\\d\\.\\d\\d\\s*\\d\\d \\d\\d)\\s+";
		regexp += "("+HistoryOperationType.getRegexp()+")";
		regexp += "(?=\\s+by\\s+";
		regexp += "("+UserConfig.USER_ID_REGEX+")";
		regexp += ")?";
		parserPattern = Pattern.compile(regexp);

	}

	public HistoryEntry(Date date, int operationTypeId, String user) {
		super();
		this.date = date;
		this.operationTypeId = operationTypeId;
		this.user = user;
	}

	public HistoryOperationType getOperationType() {
		return HistoryOperationType.getHistoryOperationType(operationTypeId);
	}

	public String toString() {
		return formatForBolscript();
	}

	public int compareDateTo (HistoryEntry otherEntry) {
		if (this.date == null) {
			if (otherEntry.date == null) { 
				return 0; 
			} else return -1;
		} else if (otherEntry.date == null) { 
					return 1; 
				} else {
					return this.date.compareTo(otherEntry.date);
		}
	}
	public int getOperationTypeId() {
		return operationTypeId;
	}

	public String getUser() {
		return user;
	}

	public Date getDate() {
		return date;
	}

	/**
	 * Returns a history entry parsed from the given String of the form suggested in init().
	 * @param input
	 */
	public static HistoryEntry fromString(String input) throws ParseException{
		Matcher m = parserPattern.matcher(input);
		if (m.find()) {
			//HistoryEntry entry = new HistoryEntry(m.group(1),m.group(2),null);

			if (m.group(1) != null) {
				//get date
				Date date;
				try {
					date = parseDateFromBolscript(m.group(1));
				} catch (ParseException e) {					
					e.printStackTrace();
					throw new ParseException ("date "+m.group(1)+" could not be parsed!", m.start(1));
				}

				//get operation type
				HistoryOperationType opType = HistoryOperationType.getType(m.group(2));
				if (opType==null) {								
					throw new ParseException ("date "+m.group(1)+" could not be parsed!", m.start(2));
				}

				//get user
				String user = m.group(3);

				return new HistoryEntry(date, opType.getId(), user);


			}

		} 
		throw new ParseException ("HistoryEntry could not be parsed: (" + input + ")", 0);

	}

	public String formatForBolscript() {
		return formatDateForBolscript() + " " + getOperationType().getParsableVersion() + ((user!=null)?" by " + user:"");
	}

	public String formatDateForBolscript() {
		return dateFormat.format(date);		
	}

	public static Date parseDateFromBolscript(String input) throws ParseException {
		return dateFormat.parse(input);
	}

}

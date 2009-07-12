package bolscript.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;

public class ConnectTest {

	public static void main (String[] args) {
		Debug.init();
		URL url;
		try {
			String SID = "";
			url = new URI("http://www.oudbrothers.de/bolscript/composition.php").toURL();
			String input = doRequest(url, SID);
			Pattern p = Pattern.compile("PHPSESSID=[a-z0-9]+");
			Matcher m = p.matcher(input);
			
			if (m.find()) {
				SID = m.group();
			}
			
			Debug.debug(ConnectTest.class, "received session id '" + SID+"'");
			input = doRequest(url, SID);
			Debug.debug(ConnectTest.class, "2. input:\n"+input);
			
		}		
		 catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		 catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		
		}
	}
	
	public static String doRequest (URL url, String SID) throws Exception{
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
		writer.write(SID + "&user=hannes&password=bla");
		writer.flush();
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuilder all = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			all.append(line);
			all.append("\n");
		}
		writer.close();
		reader.close();
		con.disconnect();
		return all.toString();
		
	}
}

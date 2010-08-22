package basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Tools {


	/**
	 * Sets the first character to uppercase and the rest to lower case
	 * @param s
	 * @return
	 */
	public static String formatFirstCapital(String s) {
		if (s.length() > 1) {
			return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
		} else return s.toUpperCase();
	}

	/**
	 * Upper case at beginning, and after each punctuation or whitespace.
	 * @param s
	 * @return
	 */
	public static String formatName(String s) {

		StringBuilder n = new StringBuilder();

		char [] chars = s.toCharArray();
		boolean uppercase = true;

		for (int i = 0; i < chars.length; i++) {
			Character c = chars[i];

			n.append( (uppercase)?Character.toUpperCase(c):Character.toLowerCase(c) );

			uppercase = c.toString().matches("[\\s-\\p{Punct}]");
		}
		return n.toString();
	}

	public static String toString(ArrayList list) {

		if (list != null) {
			StringBuilder s = new StringBuilder();
			for (int i=0; i<list.size(); i++) {
				if (i < list.size()-1) {
					s.append(list.get(i).toString());
					s.append(", ");
				} else {
					s.append(list.get(i).toString());
				}
			}
			return s.toString();
		} else return new String("");
	}

	public static String toString(Object [] array) {
		if (array != null) {
			StringBuilder s = new StringBuilder();
			for (int i=0; i<array.length; i++) {
				if (i < array.length-1) {
					s.append(array[i]);
					s.append(", ");
				} else {
					s.append(array[i]);
				}
			}
			return s.toString();
		} else return new String("");
	}

	public static int assure(int min, int value, int max) {
		return Math.min(Math.max(min, value), max);
	}
	public static double assure(double min, double value, double max) {
		return Math.min(Math.max(min, value), max);
	}

	public static URI URIFromDangerousPlainTextUrl(String url) throws URISyntaxException {
		URI uri = new URI(url.replaceAll("\\s", "%20"));
		return uri;
	}

	public static String inputStreamToString(InputStream in) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}

		bufferedReader.close();
		return stringBuilder.toString();

	}
}

package basics;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A FilenameFilter that accepts Files when a certain suffix is met.
 * @author hannes
 */
public class SuffixFilter implements FilenameFilter {

	
	private String suffix;

	public SuffixFilter (String suffix) {
		this.suffix = suffix;
	}

	public boolean accept( File f, String s ) { 
		return new File(f, s).isFile() && 
		s.toLowerCase().endsWith( suffix ); 
	} 



}

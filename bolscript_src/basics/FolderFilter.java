package basics;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * A filechooser.FileFilter that only accepts directories.
 * @author hannes
 *
 */
public class FolderFilter extends javax.swing.filechooser.FileFilter implements FileFilter, FilenameFilter {

	public FolderFilter() {
		super();
	}
	
	public boolean accept(File f) {
		return f.isDirectory() && (!f.getName().endsWith(".svn"));
	}

	public String getDescription() {
		return "Chooses only directories";
	}

	public boolean accept(File dir, String name) {
		File f = new File(dir.getPath() + System.getProperty("file.separator") + name);
		return f.isDirectory();
		
	}

}

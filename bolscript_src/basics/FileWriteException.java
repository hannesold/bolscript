package basics;

import java.io.File;
import java.io.FileNotFoundException;

public class FileWriteException extends Exception {
	
	public FileWriteException (File f, Exception exact) {
		super(f + " could not be written." + exact);
		if (exact.getClass() == FileNotFoundException.class) {
		}
	}
}

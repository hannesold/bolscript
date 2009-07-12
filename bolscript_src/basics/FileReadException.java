package basics;

import java.io.File;
import java.io.FileNotFoundException;

public class FileReadException extends Exception {
	
	private static final long serialVersionUID = 3872999695477315773L;

	public FileReadException (File f, Exception exact) {
		super(f + " could not be read." + exact);
		if (exact.getClass() == FileNotFoundException.class) {
		}
	}
}

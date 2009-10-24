package basics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import bols.Bol;
import bols.BolBaseGeneral;
import bols.BolName;
import bols.PlayingStyle;
import bolscript.config.Config;
import bolscript.sequences.BracketClosedUnit;
import bolscript.sequences.BracketOpenUnit;
import bolscript.sequences.CommaUnit;
import bolscript.sequences.SpeedUnit;


public class FileManager {
	public static Debug debug = new Debug(FileManager.class);

	/**
	 * Returns the contents of a textfile, but only if it does not exceed a specified maximum file size. In case of exceeding or
	 * in case of some reading error a FileReadException is thrown.
	 * @param file The file.
	 * @param maxFileSize The maximum allowed file size in bytes.
	 * @param encoding TODO
	 * @return
	 * @throws FileReadException Is thrown if it is larger than maxFileSize or there is some readError.
	 */
	static public String getContents(File file, int maxFileSize, String encoding) throws FileReadException{
		if (file.length() < maxFileSize) {
			debug.debug("reading file "+file.getName()+" with file size: " + file.length() + " bytes.");
			return getContents(file, encoding);
		} else {
			throw new FileReadException(file, new Exception("File exceeds maximum allowed filesize of: " + maxFileSize + "B, it has size:" + file.length()));
		} 
	}

	/**
	 * Original version from http://www.javapractices.com/topic/TopicAction.do?Id=42
	 * Returns the contents of a textfile. In case of some reading error a FileReadException is thrown.
	 * @param file The file.
	 * @param encoding TODO
	 * @param maxFileSize The maximum allowed file size in bytes.
	 * @return
	 * @throws FileReadException Is thrown if it is larger than maxFileSize or there is some readError.
	 */
	static public String getContents(File file, String encoding) throws FileReadException {
		//...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!

			FileInputStream fin = new FileInputStream(file);
			InputStreamReader in = new InputStreamReader(fin, encoding);
			BufferedReader input = new BufferedReader(in);

			try {
				String line = null; //not declared within while loop
				/*
				 * readLine is a bit quirky :
				 * it returns the content of a line MINUS the newline.
				 * it returns null only for the END of the stream.
				 * it returns an empty String if two newlines appear in a row.
				 */
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append(Config.bolscriptStandardLineSeperator);
				}
			}
			finally {
				input.close();
			}
		}
		catch (FileNotFoundException ex) {
			throw new FileReadException (file, ex);
		}
		catch (IOException ex){
			throw new FileReadException (file, ex);
		}

		return contents.toString();
	}

	/**
	 * Write the contents to a file with the given filename.
	 * If the file already exists it is overwritten.
	 * If not, it will be created first.
	 * 
	 * @param filename
	 * @param contents
	 * @param encoding TODO
	 * @throws FileWriteException if anything goes wrong.
	 */
	static public void writeFile(String filename, String contents, String encoding) throws FileWriteException {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				throw new FileWriteException(file, ex);
			}
		} else if (!file.isFile()) {
			throw new FileWriteException( file , 
					new IllegalArgumentException("Should not be a directory: " + file));
		}
		try {
			Debug.debug(FileManager.class, "setting contents of " + file + "...");
			setContents(file, contents, encoding);
			Debug.debug(FileManager.class, "done...");
		} catch (Exception ex) {
			throw new FileWriteException(file, ex);
		}
	}


	/**
	 * Change the contents of text file in its entirety, overwriting any
	 * existing text.
	 *
	 * This style of implementation throws all exceptions to the caller.
	 *
	 * @param aFile is an existing file which can be written to.
	 * @param encoding TODO
	 * @throws IllegalArgumentException if param does not comply.
	 * @throws FileNotFoundException if the file does not exist.
	 * @throws IOException if problem encountered during write.
	 */
	static public void setContents(File aFile, String aContents, String encoding)
	throws FileNotFoundException, IOException {
		if (aFile == null) {
			throw new IllegalArgumentException("File should not be null.");
		}

		if (!aFile.exists()) {
			throw new FileNotFoundException ("File does not exist: " + aFile);
		}
		if (!aFile.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: " + aFile);
		}
		if (!aFile.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: " + aFile);
		}

		//use buffering
		FileOutputStream fos = new FileOutputStream(aFile);
		OutputStreamWriter out = new OutputStreamWriter(fos, encoding);

		try {
			//FileWriter always assumes default encoding is OK!
			out.write(aContents);
		}
		finally {
			out.close();
			fos.close();
		}
	}



}


package basics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import bolscript.config.Config;

/**

 * @author hannes
 */
public class ZipTools {

	/**
	 * Extracts all Subentries of a directory-type entry of a Zipfile, i.e. all subdirectories and subfiles of the directory.
	 * They keep their relative paths and land alltogether in destination_path. 
	 * @param destination_path The destination folder.
	 * @param zipFile The Zip-File from which to extract.
	 * @param entry The entry whos 'children' shall be extracted.
	 * @throws Exception
	 */
	public static void extractSubentries( String destination_path, ZipFile zipFile, ZipEntry entry) throws Exception{
		if (entry.isDirectory()) {
			Debug.temporary(ZipTools.class, "ExtractSubentries: the entry is no directory so nothing can be extracted");
		} else {
			Debug.temporary(ZipTools.class, "ExtractSubentries of " + entry);


			String father = entry.getName();


			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry e = entries.nextElement();

				if (!e.equals(entry)) {
					if (e.getName().startsWith(entry.getName())) {
						Debug.temporary(ZipTools.class, "extraction candidate: " + e.getName());
						String relativePath = 
							e.getName().substring(father.length()+1).
							replaceAll("/", Config.fileSeperator);
						Debug.temporary(ZipTools.class, "relative path: " + relativePath);


						String absoluteDestinationPath = destination_path + Config.fileSeperator + relativePath;
						Debug.temporary(ZipTools.class, "absoluteDestinationPath: " + absoluteDestinationPath);
						if (e.isDirectory()) {
							createDirectory(absoluteDestinationPath);
						} else {

							try {
								extractOneFile(e.getName(), absoluteDestinationPath, zipFile.getInputStream(e));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}

					}
				}
			}
		}
	}



	private static void createDirectory(String absoluteDestinationPath) throws IOException{
		File folder = new File(absoluteDestinationPath);
		if (!folder.exists()) {
			folder.mkdirs();
		} else if (!folder.isDirectory()) {
			throw new IOException("a file got confused with a folder");
		}		
	}

	/**
	 * Most parts of extractOneFile are taken from ZipExtract.java:
	 * April 27, 1997
	 * Mark Nelson
	 * markn@tiny.com
	 * http://web2.airmail.net/markn
	 * This method does all the work to extract a single
	 * file from the ZipFile.  The java.util.zip package
	 * doesn't have a handy extract() function, it simply
	 * provides a stream and lets you do the work.  This
	 * makes more work for you, but it also makes it easy
	 * to implement your own feedback method.  This routine
	 * simply prints a line of text every 100K bytes.
	 */
	public static File extractOneFile( String plainName,
			String destinationFilename,
			InputStream input ) throws IOException {

		Debug.debug(ZipTools.class, " extractOneFile to " + destinationFilename);
		//create folder, just in case
		int lastSeperator = destinationFilename.lastIndexOf(Config.fileSeperator);
		if (lastSeperator >0) {
			String directory = destinationFilename.substring(0, lastSeperator-Config.fileSeperator.length()+1);
			Debug.debug(ZipTools.class, "checking directory: " + directory);
			File dir = new File(directory);
			if (!dir.exists()) {
				Debug.debug(ZipTools.class, "directory does not exist, make it.");
				dir.mkdirs();
			}
		}

		FileOutputStream output = new FileOutputStream( destinationFilename );
		byte[] buf = new byte[ 100000 ];
		int old_pacifier = -1;
		int j;
		for ( j = 0 ; ;  ) {
			int length = input.read( buf );
			if ( length <= 0 )
				break;
			j += length;
			output.write( buf, 0, length );
			int new_pacifier = ( j / 100000 ) * 100000;
			if ( new_pacifier != old_pacifier ) {
				Debug.temporary(ZipTools.class, "Extracting: " +
						plainName +
						": " +
						new_pacifier );
				old_pacifier = new_pacifier;
			}
		}
		output.close();
		Debug.temporary(ZipTools.class, "Extracting: " +
				plainName +
				": complete, " +
				j + " bytes" );

		return new File(destinationFilename);
	}

	/**
	 * Simply lists all entries in a Zip file.
	 * @param zipFile
	 */
	public static void listEntries(ZipFile zipFile) {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry e = entries.nextElement();
			Debug.temporary(ZipTools.class, e.toString());
		}
	}
}

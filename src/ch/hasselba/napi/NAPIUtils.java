package ch.hasselba.napi;

import java.io.InputStream;

import com.ibm.designer.domino.napi.NotesAPIException;
import com.ibm.designer.domino.napi.NotesDatabase;
import com.ibm.designer.domino.napi.NotesNote;
import com.ibm.designer.domino.napi.NotesObject;
import com.ibm.designer.domino.napi.NotesSession;
import com.ibm.designer.domino.napi.design.FileAccess;

/**
 * NAPIUtils
 * (c) Sven Hasselbach
 *
 * @author
 *   Sven Hasselbach
 * @version 1.2
 */
public class NAPIUtils {

	/**
	 * loads given file from a database and returns the Inputsstream
	 * 
	 * @param serverName
	 *            the server to use
	 * @param dbPath
	 *            the database path
	 * @param fileName
	 *            the file to load
	 * @return the file data as InputStream
	 * 
	 */
	static public InputStream loadBinaryFile(final String serverName, final String dbPath,
			final String fileName) {

		NotesSession nSession = null;
		NotesDatabase nDatabase = null;
		NotesNote nNote = null;

		try {
			nSession = new NotesSession();

			// open database
			try {
				nDatabase = nSession.getDatabaseByPath(serverName + "!!" + dbPath);
			} catch (NotesAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nDatabase.open();

			// load existing data
			nNote = FileAccess.getFileByPath(nDatabase, fileName);

			// get Filedate and return String
			InputStream is = FileAccess.readFileContentAsInputStream(nNote);

			return is;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// recycle NAPI objects
			recycleNAPIObject(nNote, nDatabase, nSession);
		}

		return null;
	}
	
	
	/**
	 * loads a given WebContent file and returns the result as String
	 * 
	 * @param serverName
	 *            the server to use
	 * @param dbPath
	 *            the database path
	 * @param fileName
	 *            the file to load
	 * @return the file data as String
	 */
	static public String loadFile(final String serverName, final String dbPath,
			final String fileName) {

		try {
			// get file data and return converted string
			InputStream is = loadBinaryFile( serverName, dbPath, fileName );
			
			if( is != null )
				return convertStreamToString(is);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return fileName;
	}
	
	

	/**
	 * loads a given WebContent file and returns the result as String
	 * 
	 * @param serverName
	 *            the server to use
	 * @param dbPath
	 *            the database path
	 * @param fileName
	 *            the file to load
	 * @param fileData
	 *            the data of the file
	 */
	static public void saveFile(final String serverName, final String dbPath,
			final String fileName, final String fileData) {

		NotesSession nSession = null;
		NotesDatabase nDatabase = null;
		NotesNote nNote = null;

		try {
			nSession = new NotesSession();

			// open database
			nDatabase = nSession.getDatabaseByPath(serverName + "!!" + dbPath);
			nDatabase.open();

			// load existing data
			nNote = FileAccess.getFileByPath(nDatabase, fileName);

			// store them to note
			FileAccess.saveData(nNote, fileName, fileData.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// recycle NAPI objects
			recycleNAPIObject(nNote, nDatabase, nSession);
		}

	}

	/**
	 * converts an input stream to a string
	 * 
	 * @param is
	 *            the input stream to convert
	 * @return String
	 */
	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * recycleNAPIObject helper method for recycling NAPI objects
	 * 
	 * @param nObjects
	 *            the NAPI objects to recycle
	 */
	static void recycleNAPIObject(NotesObject... nObjects) {
		for (NotesObject nObject : nObjects) {
			if (nObject != null) {
				try {
					(nObject).recycle();
				} catch (Exception ne) {
				}
			}
		}
	}
}

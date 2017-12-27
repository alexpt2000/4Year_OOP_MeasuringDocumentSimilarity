package ie.gmit.sw;

import static java.lang.System.out;

import java.util.List;
import java.util.Map;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.ConfigScope;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;
import com.db4o.ext.IncompatibleFileFormatException;
import com.db4o.ext.OldFormatException;
import com.db4o.io.MemoryStorage;
import com.db4o.query.Predicate;
import com.db4o.ta.TransparentActivationSupport;
import com.db4o.ta.TransparentPersistenceSupport;
import com.sun.istack.internal.NotNull;

import xtea_db4o.XTEA;
import xtea_db4o.XTeaEncryptionStorage;


/**
 * The Class BooksDB.
 * 
 * All the necessary settings for DB4O to work in the system.
 * 
 * 
 * @author Alexander Souza
 * @version 1.0
 * @since Dec 2017
 */
public class DocumentsDB {
	
	private ObjectContainer db = null;
	
	private ObjectContainer containerDB = null;

	/**
	 * Instantiates a new books DB.
	 */
	public DocumentsDB() {

		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentActivationSupport()); // Real lazy. Saves all the config commented out below
		config.common().add(new TransparentPersistenceSupport()); // Lazier still. Saves all the config commented out
																	// below
		config.common().updateDepth(7); // Propagate updates

		// Use the XTea lib for encryption. The basic Db4O container only has a Caesar
		// cypher... Dicas quod non est ita!
		config.file().storage(new XTeaEncryptionStorage("password", XTEA.ITERATIONS64));

		// Open a local database. Use Db4o.openServer(config, server, port) for full
		// client / server
		try {
			//db = Db4oEmbedded.openFile(config, "C:/books/Documents.data"); // Hard code of local storage
			//db = Db4oEmbedded.openFile(config, "Documents.data"); // Hard code of local storage
			db = Db4oEmbedded.openFile(config, "webapps/jaccard/DB/Documents.data"); 	// Local storage into DB folder on 
																						//Tomcat folder
		} catch (Db4oIOException e) {
		} catch (DatabaseFileLockedException e) {
		} catch (IncompatibleFileFormatException e) {
		} catch (OldFormatException e) {
		} catch (DatabaseReadOnlyException e) {
		}

	}

	/**
	 * Adds the document to database.
	 * 
	 * Once we get a handle on an ObjectContainer, we are working in a transactional
	 * environment. Adding objects to our database merely requires calling
	 * db.set(object).
	 *
	 * @param document the Document load from the file
	 */

	public void AddDocumentToDatabase(Documents document) {
		containerDB = db.ext().openSession();
		try {
			containerDB.store(document);
			containerDB.commit(); // Commits the transaction
			// db.rollback(); //Rolls back the transaction
		} finally {
			containerDB.close();
		}
	}

	/**
	 * Load all Documents into Database.
	 *
	 * @return the list of documents
	 */
	public List<Documents> LoadAllDocuments() {
		// An ObjectSet is a specialised List for storing results
		ObjectSet<Documents> documents;
		containerDB = db.ext().openSession();
		documents = containerDB.query(Documents.class);
		return documents;
	}

}

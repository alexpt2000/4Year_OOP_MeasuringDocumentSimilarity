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

public class BooksDB {
	private ObjectContainer db = null;
	private ObjectContainer containerDB = null;

	public BooksDB() {

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
			db = Db4oEmbedded.openFile(config, "C:/books/books.data");
		} catch (Db4oIOException e) {
			// e.printStackTrace();
		} catch (DatabaseFileLockedException e) {
			// e.printStackTrace();
		} catch (IncompatibleFileFormatException e) {
			// e.printStackTrace();
		} catch (OldFormatException e) {
			// e.printStackTrace();
		} catch (DatabaseReadOnlyException e) {
			// e.printStackTrace();
		}

	}

	/*
	 * Once we get a handle on an ObjectContainer, we are working in a transactional
	 * environment. Adding objects to our database merely requires calling
	 * db.set(object).
	 */
	public void addBookssToDatabase(Books book) {
		containerDB = db.ext().openSession();
		try {
			containerDB.store(book);
			containerDB.commit(); // Commits the transaction
			// db.rollback(); //Rolls back the transaction
		} finally {
			containerDB.close();
		}
	}

	public void showAllBooks() {
		// An ObjectSet is a specialised List for storing results
		ObjectSet<Books> books = db.query(Books.class);
		for (Books book : books) {
			out.println("[Books] " + book.getBookName() + "\t ***Database ObjID: " + db.ext().getID(book));

			// Removing objects from the database is as easy as adding them
			// db.delete(customer);
			db.commit();
		}
	}

	public List<Books> loadAllBooks() {
		ObjectSet<Books> books;

		containerDB = db.ext().openSession();

		books = containerDB.query(Books.class);

		// An ObjectSet is a specialised List for storing results

		return books;
	}

	// ****************** Best ************************
	public void getBooksNative(final Books b) {
		ObjectSet<Books> result = db.query(new Predicate<Books>() {
			private static final long serialVersionUID = 777L;

			public boolean match(Books book) {
				return book.getBookName().equals(b.getBookName());
			}
		});

		if (result.hasNext()) {
			out.println("[getBooksNative] found " + b.getBookName());

			for (Books books : result) {

				for (Map.Entry<Integer, List<Integer>> me : books.getBookHash().entrySet()) {
					int key = me.getKey();
					List<Integer> valueList = me.getValue();

					System.out.println("Key: " + key + " = ");

					for (Integer s : valueList) {
						System.out.print(" " + s);
					}
				}
				// System.out.println("Value .: " + books.getBookHash().entrySet());
			}
		} else {
			out.println("[Error] " + b.getBookName() + " is not in the database");
		}
	}
}

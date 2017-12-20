package ie.gmit.sw;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class BookServiceImpl.
 */
public class BookServiceImpl extends UnicastRemoteObject implements BookService {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new book service impl.
	 *
	 * @throws RemoteException the remote exception
	 */
	public BookServiceImpl() throws RemoteException {
		super();
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.BookService#campareBooks(ie.gmit.sw.Books)
	 */
	public Validator campareBooks(Books book) throws RemoteException {

		ArrayList<BooksResults> sendResultToPage = new ArrayList<BooksResults>();
		Validator resultDefinition = new ValidatorImp();
		CompareBook compare = new CompareBook();
		BooksDB saveBook = new BooksDB();
		List<Books> loadDocumentsDB = saveBook.loadAllBooks();

		boolean existBook = false;
		int resultSililary = 0;

		for (Books books : loadDocumentsDB) {
			resultSililary = compare.similarityHashMap(book, books);
			sendResultToPage.add(new BooksResults(resultSililary, books.getBookName()));

			if (book.getBookName().equals(books.getBookName())) {
				existBook = true;
			}
		}

		if (existBook) {
			System.out.println("The document " + book.getBookName() + " already exist on database.");
			resultDefinition.setResultSave("The document " + book.getBookName() + " already exist on database.");
		} else {
			System.out.println("The document " + book.getBookName() + " will be save on database.");
			resultDefinition.setResultSave("The document " + book.getBookName() + " will be save on database.");

			saveBook.addBookssToDatabase(book);
		}

		resultDefinition.setResult(sendResultToPage);

		// Set the definition as Processed into Queue
		resultDefinition.setProcessed();

		return resultDefinition;
	}
}

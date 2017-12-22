package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import ie.gmit.sw.Compare.CompareDB;

public class BookServiceImpl extends UnicastRemoteObject implements BookService {

	private static final long serialVersionUID = 1L;

	protected BookServiceImpl() throws RemoteException {
		super();

	}

	// Find definition into ArrayList
	public Validator getBookResults(Books book) throws RemoteException {

		String sendResultToPage = "";
		boolean existBook = false;
		
		//BooksDB saveBook = new BooksDB();
		CompareDB compare = new CompareDB();
		
		BooksResults resultSililary = new BooksResults();
		
		BooksDB saveBook = new BooksDB();
		List<Books> loadDocumentsDB = saveBook.loadAllBooks();

		// Control of Queues
		Validator bookCompareResults = new ValidatorImp();

		for (Books books : loadDocumentsDB) {

			//double resultSililary = compare.similarityHashMap(book, books);
			
			resultSililary = new BooksResults(compare.similarityHashMap(book, books), books.getBookName());
			
			sendResultToPage += resultSililary + " " + books.getBookName();
			
			if (book.getBookName().equals(books.getBookName())) {
				existBook = true;
			}

		}

		if (existBook) {
			System.out.println("This book exist on database.");
		} else {
			System.out.println("The book " + book.getBookName() + " will be save on database.");
			saveBook.addBookssToDatabase(book);
		}

		// Set the String in result
		bookCompareResults.setResult(sendResultToPage);

		// Set the definition as Processed into Queue
		bookCompareResults.setProcessed();

		return bookCompareResults;
	}

}

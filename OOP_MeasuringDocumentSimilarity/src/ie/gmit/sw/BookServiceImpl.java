package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class BookServiceImpl {
	//private static final long serialVersionUID = 1L;

	BooksDB saveBook = new BooksDB();

	public BookServiceImpl() throws RemoteException {
		super();
	}


	
	public String campareBooks(Books book) throws RemoteException {
		String sendResultToPage = "";
		boolean existBook = false;
		double resultSililary = 0;

		// Control of Queues
		//Validator resultDefinition = new ValidatorImp();
		CompareBook compare = new CompareBook();

		
		
		List<Books> loadDocumentsDB = saveBook.loadAllBooks();

		for (Books books : loadDocumentsDB) {

			//double resultSililary = compare.similarityHashMap(book, books);
			
			resultSililary = compare.similarityHashMap(book, books);
			
			sendResultToPage += resultSililary + "%  = " + books.getBookName();
			
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
		//resultDefinition.setResult(sendResultToPage);

		// Set the definition as Processed into Queue
		//resultDefinition.setProcessed();

		return sendResultToPage;
	}
}

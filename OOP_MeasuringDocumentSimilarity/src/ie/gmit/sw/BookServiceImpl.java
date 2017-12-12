package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import com.sun.tools.javac.util.List;

import ie.gmit.sw.db4o.Books;

public class BookServiceImpl extends UnicastRemoteObject implements BookService {
	
	private static final long serialVersionUID = 1L;
	
	protected BookServiceImpl() throws RemoteException {
		super();
		
	}


	// Find definition into ArrayList
	public Validator getBookResults(Books book) throws RemoteException {
		
		List<BooksResults> sendResultToPage = null;

		// Control of Queues
		Validator bookCompareResults = new ValidatorImp();

		ArrayList<String> wordDetailReturn = new ArrayList<String>();


		
		//TODO Resultados
		
		
		

		// Set the String in result
		bookCompareResults.setResult(sendResultToPage);

		// Set the definition as Processed into Queue
		bookCompareResults.setProcessed();

		return bookCompareResults;
	}




}

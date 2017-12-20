package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;

// TODO: Auto-generated Javadoc
/**
 * The Interface BookService.
 */
// Find definition base on keyWord
public interface BookService extends Remote {
	
	/**
	 * Campare books.
	 *
	 * @param book the book
	 * @return the validator
	 * @throws RemoteException the remote exception
	 */
	public Validator campareBooks(Books book) throws RemoteException;
}

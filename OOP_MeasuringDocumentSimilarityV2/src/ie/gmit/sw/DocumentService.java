package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;

// TODO: Auto-generated Javadoc
/**
 * The Interface BookService.
 */
// Find definition base on keyWord
public interface DocumentService extends Remote {
	
	/**
	 * Campare books.
	 *
	 * @param document the book
	 * @return the validator
	 * @throws RemoteException the remote exception
	 */
	public Validator campareDocument(Documents document) throws RemoteException;
}

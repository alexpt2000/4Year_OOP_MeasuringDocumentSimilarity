package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Interface DocumentService.
 */
// Find definition base on keyWord
public interface DocumentService extends Remote {
	
	/**
	 * Compare Documents.
	 *
	 * @param document the Documents
	 * @return the validator
	 * @throws RemoteException the remote exception
	 */
	public Validator campareDocument(Documents document) throws RemoteException;
}

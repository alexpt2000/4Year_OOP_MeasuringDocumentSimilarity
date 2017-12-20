package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface Validator.
 */
public interface Validator extends Remote {
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 * @throws RemoteException the remote exception
	 */
	public ArrayList<BooksResults> getResult() throws RemoteException;

	/**
	 * Sets the result.
	 *
	 * @param result the new result
	 * @throws RemoteException the remote exception
	 */
	public void setResult(ArrayList<BooksResults> result) throws RemoteException;

	/**
	 * Gets the result save.
	 *
	 * @return the result save
	 * @throws RemoteException the remote exception
	 */
	public String getResultSave() throws RemoteException;

	/**
	 * Sets the result save.
	 *
	 * @param result the new result save
	 * @throws RemoteException the remote exception
	 */
	public void setResultSave(String result) throws RemoteException;

	/**
	 * Checks if is processed.
	 *
	 * @return true, if is processed
	 * @throws RemoteException the remote exception
	 */
	public boolean isProcessed() throws RemoteException;

	/**
	 * Sets the processed.
	 *
	 * @throws RemoteException the remote exception
	 */
	public void setProcessed() throws RemoteException;
}

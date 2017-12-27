package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidatorImp.
 */
// Bean Validator
public class ValidatorImp extends UnicastRemoteObject implements Validator {

	private static final long serialVersionUID = 1L;
	private boolean processed;
	private ArrayList<DocumentResults> result;
	private String resultSave;

	/**
	 * Instantiates a new validator imp.
	 *
	 * @throws RemoteException the remote exception
	 */
	ValidatorImp() throws RemoteException {
		super();
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.Validator#getResult()
	 */
	public ArrayList<DocumentResults> getResult() throws RemoteException {
		return result;
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.Validator#setResult(java.util.ArrayList)
	 */
	public void setResult(ArrayList<DocumentResults> result) throws RemoteException {
		this.result = result;
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.Validator#isProcessed()
	 */
	public boolean isProcessed() throws RemoteException {
		return processed = true;
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.Validator#setProcessed()
	 */
	public void setProcessed() throws RemoteException {
		this.processed = processed;
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.Validator#getResultSave()
	 */
	public String getResultSave() throws RemoteException {

		return resultSave;
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.Validator#setResultSave(java.lang.String)
	 */
	public void setResultSave(String resultSave) throws RemoteException {
		this.resultSave = resultSave;

	}

}
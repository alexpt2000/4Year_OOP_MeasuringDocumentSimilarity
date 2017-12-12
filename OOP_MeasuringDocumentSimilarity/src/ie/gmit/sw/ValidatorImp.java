package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;



// Bean Validator
public class ValidatorImp extends UnicastRemoteObject implements Validator {

	private static final long serialVersionUID = 1L;
	private boolean processed;
	private List<BooksResults> result;

	ValidatorImp() throws RemoteException {
		super();
	}

	public List<BooksResults> getResult() throws RemoteException {
		return result;
	}

	public void setResult(List<BooksResults> result) throws RemoteException {
		this.result = result;
	}

	public boolean isProcessed() throws RemoteException {
		return processed = true;
	}

	public void setProcessed() throws RemoteException {
		this.processed = processed;
	}


}
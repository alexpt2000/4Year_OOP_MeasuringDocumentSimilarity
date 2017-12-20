package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

// Bean Validator
public class ValidatorImp extends UnicastRemoteObject implements Validator {

	private static final long serialVersionUID = 1L;
	private boolean processed;
	private ArrayList<BooksResults> result;
	private String resultSave;

	ValidatorImp() throws RemoteException {
		super();
	}

	public ArrayList<BooksResults> getResult() throws RemoteException {
		return result;
	}

	public void setResult(ArrayList<BooksResults> result) throws RemoteException {
		this.result = result;
	}

	public boolean isProcessed() throws RemoteException {
		return processed = true;
	}

	public void setProcessed() throws RemoteException {
		this.processed = processed;
	}

	public String getResultSave() throws RemoteException {

		return resultSave;
	}

	public void setResultSave(String resultSave) throws RemoteException {
		this.resultSave = resultSave;

	}

}
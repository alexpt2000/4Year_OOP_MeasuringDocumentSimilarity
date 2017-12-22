package ie.gmit.sw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Bean Validator
public class ValidatorImp extends UnicastRemoteObject implements Validator {

	private static final long serialVersionUID = 1L;
	private boolean processed;
	private String result;

	ValidatorImp() throws RemoteException {
		super();
	}

	public String getResult() throws RemoteException {
		return result;
	}

	public void setResult(String result) throws RemoteException {
		this.result = result;
	}

	public boolean isProcessed() throws RemoteException {
		return processed = true;
	}

	public void setProcessed() throws RemoteException {
		this.processed = processed;
	}
}
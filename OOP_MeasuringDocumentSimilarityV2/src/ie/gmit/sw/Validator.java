package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface Validator extends Remote {
	public ArrayList<BooksResults> getResult() throws RemoteException;

	public void setResult(ArrayList<BooksResults> result) throws RemoteException;

	public boolean isProcessed() throws RemoteException;

	public void setProcessed() throws RemoteException;
}

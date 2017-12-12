package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sun.tools.javac.util.List;

public interface Validator extends Remote {
	public List<BooksResults> getResult() throws RemoteException;

	public void setResult(List<BooksResults> result) throws RemoteException;

	public boolean isProcessed() throws RemoteException;

	public void setProcessed() throws RemoteException;
}

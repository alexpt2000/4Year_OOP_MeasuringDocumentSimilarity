package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Find definition base on keyWord
public interface BookService extends Remote {
	public Validator campareBooks(Books book) throws RemoteException;
}

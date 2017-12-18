package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Find definition base on keyWord
public interface BookService extends Remote {
	public String campareBooks(Books book) throws RemoteException;
}

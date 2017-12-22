package ie.gmit.sw;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// Find definition base on keyWord
public interface BookService extends Remote {
	public Validator getBookResults(Books book) throws RemoteException;
}

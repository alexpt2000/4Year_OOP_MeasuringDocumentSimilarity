package ie.gmit.sw;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ServiceQueue implements Runnable {

	private BlockingQueue<Books> inQueue;
	private Map<String, Validator> outQueue;
	private Validator res;
	private BookService strSer;

	public ServiceQueue(BlockingQueue<Books> inQueue, Map<String, Validator> outQueue, BookService strSer) {
		this.inQueue = inQueue;
		this.outQueue = outQueue;
		this.strSer = strSer;
	}

	// Thread Pool
	@Override
	public void run() {
		Books req = inQueue.poll();

		try {
			System.out.println("\nChecking Status of Task No: " + req.getTaskNumber());
			Thread.sleep(500);

			res = strSer.getBookResults(req);

			//System.out.println(req.getKeyWord());
			System.out.println(" Print Tast.: " + req.getTaskNumber() + " ==== ");
			outQueue.put(req.getTaskNumber(), res);
		} catch (RemoteException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}

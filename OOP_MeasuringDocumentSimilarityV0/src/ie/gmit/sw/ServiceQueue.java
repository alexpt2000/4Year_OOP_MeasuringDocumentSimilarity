package ie.gmit.sw;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ServiceQueue implements Runnable {

	private BlockingQueue<Books> inQueue;
	private Map<String, String> outQueue;
	private Validator res;
	private BookService strSer;

	public ServiceQueue(BlockingQueue<Books> inQueue, Map<String, String> outQueue, BookService strSer) {
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

			//res = strSer.getBookResults(req);
			//res= "100% tested";
			
			//System.out.println(" Print Result.: " + res.getResult());

			//System.out.println(req.getKeyWord());
			System.out.println(" Print Tast.: " + req.getTaskNumber() + " ==== ");
			outQueue.put("Test2", "Test Thread");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

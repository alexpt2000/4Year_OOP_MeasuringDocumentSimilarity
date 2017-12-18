package ie.gmit.sw;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ServiceQueue implements Runnable {

	private BlockingQueue<Books> inQueue;
	private Map<String, String> outQueue;
	//private Validator res;
	//private BookServiceImpl bookservice = new BookServiceImpl();
	


	public ServiceQueue(BlockingQueue<Books> inQueue, Map<String, String> outQueue) {
		this.inQueue = inQueue;
		this.outQueue = outQueue;
	}

	// Thread Pool
	@Override
	public void run() {
		Books req = inQueue.poll();

		try {
			//System.out.println("\nChecking Status of Task No: " + req.getTaskNumber());
			Thread.sleep(500);

			//res.setResult("Funciona"); 
			//res = strSer.campareBooks(req);
			BookServiceImpl bookservice = new BookServiceImpl();
			
			String result = bookservice.campareBooks(req);

			System.out.println("-"+req.getTaskNumber() + " Funciona000 " );
			
			outQueue.put(req.getTaskNumber(), result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

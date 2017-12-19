package ie.gmit.sw;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ServiceQueue implements Runnable {

	private BlockingQueue<Books> inQueue;
	private Map<String, Validator> outQueue;
	private Validator res;
	private BookService strSer;
	private volatile boolean exit = false;

	
	
	public ServiceQueue() {
		super();
	}

	public ServiceQueue(BlockingQueue<Books> inQueue, Map<String, Validator> outQueue, BookService strSer) {
		this.inQueue = inQueue;
		this.outQueue = outQueue;
		this.strSer = strSer;
	}

	// Thread Pool
	@Override
	public void run() {
		while(!exit) {
			Books req = inQueue.poll();
	
			try {
				//System.out.println("\nChecking Status of Task No: " + req.getTaskNumber());
				Thread.sleep(1000);
	
				res = strSer.campareBooks(req);
	
				//System.out.println(req.getKeyWord());
				
				outQueue.put(req.getTaskNumber(), res);
			} catch (RemoteException | InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
    public void stop(){
        exit = true;
    }

}

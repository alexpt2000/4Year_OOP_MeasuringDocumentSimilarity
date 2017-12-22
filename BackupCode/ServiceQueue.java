package ie.gmit.sw;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

// TODO: Auto-generated Javadoc
/**
 * The Class ServiceQueue.
 */
public class ServiceQueue implements Runnable {

	/** The in queue. */
	private BlockingQueue<Books> inQueue;
	
	/** The out queue. */
	private Map<String, Validator> outQueue;
	
	/** The res. */
	private Validator res;
	
	/** The str ser. */
	private BookService strSer;
	
	/** The exit. */
	private volatile boolean exit = false;

	/**
	 * Instantiates a new service queue.
	 */
	public ServiceQueue() {
		super();
	}

	/**
	 * Instantiates a new service queue.
	 *
	 * @param inQueue the in queue
	 * @param outQueue the out queue
	 * @param strSer the str ser
	 */
	public ServiceQueue(BlockingQueue<Books> inQueue, Map<String, Validator> outQueue, BookService strSer) {
		this.inQueue = inQueue;
		this.outQueue = outQueue;
		this.strSer = strSer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	// Thread Pool
	@Override
	public void run() {
		while (!exit) {
			Books req = inQueue.poll();
			try {
				Thread.sleep(1000);
				res = strSer.campareBooks(req);
				outQueue.put(req.getTaskNumber(), res);
			} catch (RemoteException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stop.
	 */
	public void stop() {
		exit = true;
	}
}

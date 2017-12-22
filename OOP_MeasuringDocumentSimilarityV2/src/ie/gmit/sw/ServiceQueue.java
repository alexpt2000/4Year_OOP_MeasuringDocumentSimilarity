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
	private BlockingQueue<Documents> inQueue;
	
	/** The out queue. */
	private Map<String, Validator> outQueue;
	
	/** The res. */
	private Validator res;
	
	/** The str ser. */
	private DocumentService strSer;
	
	/** The exit. */
	private volatile boolean exit = false;
	
	private String taskNumber;

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
	public ServiceQueue(BlockingQueue<Documents> inQueue, Map<String, Validator> outQueue, DocumentService strSer, String taskNumber) {
		this.inQueue = inQueue;
		this.outQueue = outQueue;
		this.strSer = strSer;
		this.taskNumber = taskNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	// Thread Pool
	@Override
	public void run() {
		while (!exit) {
			Documents req = inQueue.poll();
			try {
				Thread.sleep(1000);
				res = strSer.campareDocument(req);
				outQueue.put(taskNumber, res);
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

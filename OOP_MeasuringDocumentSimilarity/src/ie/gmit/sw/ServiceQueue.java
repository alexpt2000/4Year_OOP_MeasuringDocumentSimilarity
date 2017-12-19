package ie.gmit.sw;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.Runner.CompareDB;

public class ServiceQueue implements Runnable {

	private BlockingQueue<Books> inQueue;
	private Map<String, String> outQueue;
	//private Validator res;
	//private BookServiceImpl bookservice = new BookServiceImpl();
	
	//BookServiceImpl bookservice = new BookServiceImpl();
	
	private BooksDB saveBook = new BooksDB();
	private CompareBook compare = new CompareBook();
	


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


			List<Books> loadDocumentsDB = saveBook.loadAllBooks();
			
			for (Books books : loadDocumentsDB) {

				System.out.println(String.format("%.2f", (compare.similarityHashMap(req, books)))
						+ " %" + "\t\t" + books.getBookName());

			}

			
			//String result = bookservice.campareBooks(req);

			System.out.println("-"+req.getTaskNumber() + " Funciona000 " );
			
			outQueue.put(req.getTaskNumber(), "Testresult");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

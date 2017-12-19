package ie.gmit.sw;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;


import javax.servlet.annotation.*;

/* NB: You will need to add the JAR file $TOMCAT_HOME/lib/servlet-api.jar to your CLASSPATH 
 *     variable in order to compile a servlet from a command line.
 */
@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB. The file size in bytes after which the file will be temporarily stored on disk. The default size is 0 bytes.
                 maxFileSize=1024*1024*10,      // 10MB. The maximum size allowed for uploaded files, in bytes
                 maxRequestSize=1024*1024*50)   // 50MB. he maximum size allowed for a multipart/form-data request, in bytes.

public class ServiceHandler extends HttpServlet {
	/* Declare any shared objects here. For example any of the following can be handled from 
	 * this context by instantiating them at a servlet level:
	 *   1) An Asynchronous Message Facade: declare the IN and OUT queues or MessageQueue
	 *   2) An Chain of Responsibility: declare the initial handler or a full chain object
	 *   1) A Proxy: Declare a shared proxy here and a request proxy inside doGet()
	 */
	private String environmentalVariable = null; //Demo purposes only. Rename this variable to something more appropriate
	private static long jobNumber = 0;
	private final int POOL_SIZE = 6;

	private static Map<String, Validator> outQueue;
	private static BlockingQueue<Books> inQueue;
	private static ExecutorService executor;

	private boolean checkProcessed;
	private String returningResult;
	private boolean firstTime = true;
	private Part part;
	private Runnable work = new ServiceQueue();


	/* This method is only called once, when the servlet is first started (like a constructor). 
	 * It's the Template Patten in action! Any application-wide variables should be initialised 
	 * here. Note that if you set the xml element <load-on-startup>1</load-on-startup>, this
	 * method will be automatically fired by Tomcat when the web server itself is started.
	 */
	public void init() throws ServletException {
		ServletContext ctx = getServletContext(); //The servlet context is the application itself.
		
		//Reads the value from the <context-param> in web.xml. Any application scope variables 
		//defined in the web.xml can be read in as follows:
		environmentalVariable = ctx.getInitParameter("SOME_GLOBAL_OR_ENVIRONMENTAL_VARIABLE"); 
		
		outQueue = new HashMap<String, Validator>();
		inQueue = new LinkedBlockingQueue<Books>();
		executor = Executors.newFixedThreadPool(POOL_SIZE);
	}


	/* The doGet() method handles a HTTP GET request. Please note the following very carefully:
	 *   1) The doGet() method is executed in a separate thread. If you instantiate any objects
	 *      inside this method and don't pass them around (ie. encapsulate them), they will be
	 *      thread safe.
	 *   2) Any instance variables like environmentalVariable or class fields like jobNumber will 
	 *      are shared by threads and must be handled carefully.
	 *   3) It is standard practice for doGet() to forward the method invocation to doPost() or
	 *      vice-versa.
	 */
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		BookService service = new BookServiceImpl();
		
		CompareBook compareBook = new CompareBook();
		
		//Step 1) Write out the MIME type
		resp.setContentType("text/html"); 
		
		//Step 2) Get a handle on the PrintWriter to write out HTML
		PrintWriter out = resp.getWriter(); 
		
		//Step 3) Get any submitted form data. These variables are local to this method and thread safe...
		String bookTitle = req.getParameter("txtTitle");
		String taskNumber = req.getParameter("frmTaskNumber");

		//Step 4) Process the input and write out the response. 
		//The following string should be extracted as a context from web.xml 
		out.print("<html><head><title>A JEE Application for Measuring Document Similarity</title>");		
		out.print("</head>");		
		out.print("<body>");
		


		// We could use the following to track asynchronous tasks. Comment it out
		// otherwise...
		if (taskNumber == null) {
			taskNumber = new String("T" + jobNumber);
			
			part = req.getPart("txtDocument");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(part.getInputStream()));
			String line = null;
			
			Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();

			Random r = new Random();
			
			while ((line = br.readLine()) != null) {

				String[] words = line.split("\\s");

				for (int i = 0; i < words.length; i++) {
					words[i] = words[i].toUpperCase();
				}

				assert words.length > 2;

				if (words.length > 2) {
					
					final String[] document = Arrays.copyOfRange(words, 1, words.length);
					int docId = r.nextInt(200);
					
					docsAsShingleSets.put(docId, new ArrayList<>(compareBook.asHashes(compareBook.asShingles(document, 3))));
				}
		

				out.print(line);
			}
			

			checkProcessed = false;

			Books requestBookResult = new Books(bookTitle, taskNumber, docsAsShingleSets);

			// Add job to in-queue
			inQueue.add(requestBookResult);

			// Start the Thread
			//Runnable work = new ServiceQueue(inQueue, outQueue, service);
			//Runnable work = new ServiceQueue(inQueue, outQueue, service);
			work = new ServiceQueue(inQueue, outQueue, service);
			executor.execute(work);
			
			jobNumber++;
		} else {

			if (outQueue.containsKey(taskNumber)) {

				// get the Resultator object from outMap based on tasknumber
				Validator outQItem = outQueue.get(taskNumber);

				// System.out.println("\nChecking Status of Task No:" + taskNumber);

				// Check out-queue for finished job with the given taskNumber
				checkProcessed = outQItem.isProcessed();

				// Check to see if the Resultator Item is Processed
				if (checkProcessed == true) {
					// Remove the processed item from Map by taskNumber
					outQueue.remove(taskNumber);

					// Get the Definitons of the Current Task
					returningResult = outQItem.getResult();

					//((ServiceQueue) work).stop();
					
					System.out.println("Result.: " + returningResult );
					// System.out.println("String " + keyWord + " - " + returningDefinitons);
				}
			}
		}
		
		//Output some headings at the top of the generated page
		out.print("<H1>Processing request for Job#: " + taskNumber + "</H1>");
		out.print("<H3>Document Title: " + bookTitle + "</H3>");
		
		
		//Output some useful information for you (yes YOU!)
		out.print("<div id=\"r\"></div>");
		out.print("<font color=\"#993333\"><b>");
		out.print("Environmental Variable Read from web.xml: " + environmentalVariable);
		out.print("<br>This servlet should only be responsible for handling client request and returning responses. Everything else should be handled by different objects.");
		out.print("Note that any variables declared inside this doGet() method are thread safe. Anything defined at a class level is shared between HTTP requests.");				
		out.print("</b></font>");
		
		out.print("<h3>Compiling and Packaging this Application</h3>");
		out.print("Place any servlets or Java classes in the WEB-INF/classes directory. Alternatively package "); 
		out.print("these resources as a JAR archive in the WEB-INF/lib directory using by executing the ");  
		out.print("following command from the WEB-INF/classes directory jar -cf my-library.jar *");
		
		out.print("<ol>");
		out.print("<li><b>Compile on Mac/Linux:</b> javac -cp .:$TOMCAT_HOME/lib/servlet-api.jar WEB-INF/classes/ie/gmit/sw/*.java");
		out.print("<li><b>Compile on Windows:</b> javac -cp .;%TOMCAT_HOME%/lib/servlet-api.jar WEB-INF/classes/ie/gmit/sw/*.java");
		out.print("<li><b>Build JAR Archive:</b> jar -cf jaccard.war *");
		out.print("</ol>");
		
		//We can also dynamically write out a form using hidden form fields. The form itself is not
		//visible in the browser, but the JavaScript below can see it.
		//out.print("<form name=\"frmRequestDetails\" action=\"poll\">");
		out.print("<form name=\"frmRequestDetails\">");
		out.print("<input name=\"txtTitle\" type=\"hidden\" value=\"" + bookTitle + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("</form>");								
		out.print("</body>");	
		out.print("</html>");	
		
		//JavaScript to periodically poll the server for updates (this is ideal for an asynchronous operation)
		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 10000);"); //Refresh every 10 seconds
		out.print("</script>");
		
		
			
		/* File Upload: The following few lines read the multipart/form-data from an instance of the
		 * interface Part that is accessed by Part part = req.getPart("txtDocument"). We can read 
		 * bytes or arrays of bytes by calling read() on the InputStream of the Part object. In this
		 * case, we are only interested in text files, so it's as easy to buffer the bytes as characters
		 * to enable the servlet to read the uploaded file line-by-line. Note that the uplaod action
		 * can be easily completed by writing the file to disk if necessary. The following lines just
		 * read the document from memory... this might not be a good idea if the file size is large!
		 */
		out.print("<h3>Uploaded Document</h3>");	
		out.print("<font color=\"0000ff\">");	

		out.print("</font>");	
	}
	

	

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
	
	
}
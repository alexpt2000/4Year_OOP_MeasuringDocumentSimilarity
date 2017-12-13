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


public class ServiceHandlerCopy extends HttpServlet {

	private String environmentalVariable = null; //Demo purposes only. Rename this variable to something more appropriate
	private static long jobNumber = 0;
	private final int POOL_SIZE = 6;

	private static Map<String, Validator> outQueue;
	private static BlockingQueue<Books> inQueue;
	private static ExecutorService executor;

	private boolean checkProcessed;
	private String returningResult;


	/*
	 * This method is only called once, when the servlet is first started (like a
	 * constructor). It's the Template Patten in action! Any application-wide
	 * variables should be initialised here. Note that if you set the xml element
	 * <load-on-startup>1</load-on-startup>, this method will be automatically fired
	 * by Tomcat when the web server itself is started.
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

	/*
	 * The doGet() method handles a HTTP GET request. Please note the following very
	 * carefully: 1) The doGet() method is executed in a separate thread. If you
	 * instantiate any objects inside this method and don't pass them around (ie.
	 * encapsulate them), they will be thread safe. 2) Any instance variables like
	 * environmentalVariable or class fields like jobNumber will are shared by
	 * threads and must be handled carefully. 3) It is standard practice for doGet()
	 * to forward the method invocation to doPost() or vice-versa.
	 */

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			
		BookService service = null;

		// Step 1) Write out the MIME type
		resp.setContentType("text/html");

		// Step 2) Get a handle on the PrintWriter to write out HTML
		PrintWriter out = resp.getWriter();



		// Step 3) Get any submitted form data. These variables are local to this method
		// and thread safe...
		String bookTitle = req.getParameter("txtTitle");
		String taskNumber = req.getParameter("frmTaskNumber");
		Part part = req.getPart("txtDocument");

		// Change "bookTitle" to UpperCase and remove any blank space
		bookTitle = bookTitle.toUpperCase().replaceAll("\\s+", "");

		// Step 4) Process the input and write out the response.
		// The following string should be extracted as a context from web.xml
		out.print("<html><head><title>A JEE Application for Measuring Document Similarity</title>");
		out.print("</head>");
		out.print("<body>");

		

		out.print("<html><head><title>A JEE Application for Measuring Document Similarity</title>");
		out.print("</head>");
		out.print("<body>");

		// Output some headings at the top of the generated page
		out.print("<H1>Book Service</H1>");
		out.print("Task N. " + taskNumber);

		// Print the keyWord
		out.print("<br><H3>Book title : " + bookTitle + "</H3>");

		// Button to return to index.jsp
		out.print("<button onClick=\"window.location='index.jsp'\"><b>Make another query</b></button><br>");

		// Print the defition
		out.print("<br><font face=\"verdana\">" + returningResult + "</font>");
		
		
		
		

		
		BufferedReader br = new BufferedReader(new InputStreamReader(part.getInputStream()));
		String line = null;
		
		Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();
		CompareBook compareBook = new CompareBook();
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

		// We could use the following to track asynchronous tasks. Comment it out
		// otherwise...
		if (taskNumber == null) {
			taskNumber = new String("T" + jobNumber);

			checkProcessed = false;

			Books requestBookResult = new Books(bookTitle, taskNumber, docsAsShingleSets);

			// Add job to in-queue
			inQueue.add(requestBookResult);

			// Start the Thread
			Runnable work = new ServiceQueue(inQueue, outQueue, service);
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

					// System.out.println("\nTask " + taskNumber + " Processed");
					// System.out.println("String " + keyWord + " - " + returningDefinitons);
				}
			}
		}
		
		
		
		
		
		
		

		// We can also dynamically write out a form using hidden form fields. The form
		// itself is not
		// visible in the browser, but the JavaScript below can see it.
		out.print("<form name=\"frmRequestDetails\">");
		out.print("<input name=\"keyWordIndex\" type=\"hidden\" value=\"" + bookTitle + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("</form>");
		out.print("</body>");
		out.print("</html>");

		// refresh the page, set the time
		// JavaScript to periodically poll the server for updates (this is ideal for an
		// asynchronous operation)
		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 5000);"); // Refresh every 5 seconds
		out.print("</script>");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
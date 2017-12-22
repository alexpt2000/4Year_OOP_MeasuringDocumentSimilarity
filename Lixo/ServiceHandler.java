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
	private Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();
	private List<String> initialBook = new ArrayList<>();

	private boolean checkProcessed;
	private List<BooksResults> returningResult;
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
			Random r = new Random();
			int count = 0;
			
			
			while ((line = br.readLine()) != null) {
				count++;
				String[] words = line.split("\\s");
				
				initialBook.add(count + " - " + line + "<br>");

				for (int i = 0; i < words.length; i++) {
					words[i] = words[i].toUpperCase();
				}
				assert words.length > 2;

				if (words.length > 2) {					
					final String[] document = Arrays.copyOfRange(words, 1, words.length);
					int docId = r.nextInt(200);
					
					docsAsShingleSets.put(docId, new ArrayList<>(compareBook.asHashes(compareBook.asShingles(document, 3))));
				}
		
				//out.print(line);
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
					
					for (BooksResults results : returningResult) {
						System.out.println("Result.: " + results.getValue() + "%   Book Name.: " + results.getBookName());
					}
					
					//System.out.println("Result.: " + returningResult );
					// System.out.println("String " + keyWord + " - " + returningDefinitons);
				}
			}
		}
		
		//Output some headings at the top of the generated page
		out.print("<H1>Processing request for Job#: " + taskNumber + "</H1>");
		out.print("<H3>Document Title: " + bookTitle + "</H3>");
		
		
		//Output some useful information for you (yes YOU!)
	

		
		//We can also dynamically write out a form using hidden form fields. The form itself is not
		//visible in the browser, but the JavaScript below can see it.
		//out.print("<form name=\"frmRequestDetails\" action=\"poll\">");
		out.print("<form name=\"frmRequestDetails\">");
		//out.print("<form name=\"frmRequestDetails1\" action=\"poll\">");
		out.print("<input name=\"txtTitle\" type=\"hidden\" value=\"" + bookTitle + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("</form>");								
		out.print("</body>");	
		out.print("</html>");	
		
		//JavaScript to periodically poll the server for updates (this is ideal for an asynchronous operation)
		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 10000);"); //Refresh every 10 seconds
		//out.print("var wait=setTimeout(\"document.frmRequestDetails1.submit();\", 10000);"); //Refresh every 10 seconds
		out.print("</script>");
		

	
		// ************************* Start Chart *****************************
		out.print("<script src=\"http://www.chartjs.org/dist/2.7.1/Chart.bundle.js\">");
		out.print("</script><style type=\"text/css\">");
		
		out.print("@-webkit-keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}@keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}.chartjs-render-monitor{-webkit-animation:chartjs-render-animation 0.001s;animation:chartjs-render-animation 0.001s;}</style>");
		out.print("<script src=\"http://www.chartjs.org/samples/latest/utils.js\"></script>");
		
		out.print("<style>");
		out.print("canvas {");
		out.print("    -moz-user-select: none;");
		out.print("    -webkit-user-select: none;");
		out.print("    -ms-user-select: none;");
		out.print("}");
		out.print("</style>");
		
		//out.print("<body data-gr-c-s-loaded=\"true\">");
		out.print(" <div id=\"container\" style=\"width: 75%;\"><div class=\"chartjs-size-monitor\" style=\"position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; overflow: hidden; pointer-events: none; visibility: hidden; z-index: -1;\"><div class=\"chartjs-size-monitor-expand\" style=\"position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;\"><div style=\"position:absolute;width:1000000px;height:1000000px;left:0;top:0\"></div></div><div class=\"chartjs-size-monitor-shrink\" style=\"position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;\"><div style=\"position:absolute;width:200%;height:200%;left:0; top:0\"></div></div></div>");
		out.print("<canvas id=\"canvas\" width=\"1062\" height=\"531\" class=\"chartjs-render-monitor\" style=\"display: block; width: 1062px; height: 531px;\"></canvas>");
		out.print("</div>");
		out.print("<script>");
		out.print("var color = Chart.helpers.color;");
		out.print("var barChartData = {");
		out.print("labels: [");
		
		for (BooksResults results : returningResult) {
			out.print("\"" +  results.getBookName() + "\",");
		}
		
		out.print(" ],");
		out.print("datasets: [{");
		out.print(" label: '"+bookTitle+"',");
		out.print("backgroundColor: color(window.chartColors.blue).alpha(0.5).rgbString(),");
		out.print(" borderColor: window.chartColors.red,");
		out.print("borderWidth: 1,");
		out.print("data: [");
		
		for (BooksResults results : returningResult) {
			out.print(results.getValue() + ",");
		}

		out.print("]");
		out.print("}]");
		
		out.print("};");
		
		out.print("window.onload = function() {");
		out.print("var ctx = document.getElementById(\"canvas\").getContext(\"2d\");");
		out.print("window.myBar = new Chart(ctx, {");
		out.print("type: 'bar',");
		out.print("data: barChartData,");
		out.print("options: {");
		out.print("responsive: true,");
		out.print("legend: {");
		out.print("position: 'top',");
		out.print("},");
		out.print("title: {");
		out.print("display: true,");
		out.print("text: 'Compare'");
		out.print("}");
		out.print("}");
		out.print("});");
		
		out.print("};");
		out.print("</script>");
		// ************************* End Chart *****************************

		

		out.print("<h3>Uploaded Document</h3>");	
		out.print("<font color=\"0000ff\">");	

		for (int i = 0; i < 15; i++) {
		out.print(initialBook.get(i)); 
		}
		out.print("</font>");
		
		
		
		
		
		
		
		
		
	}
	

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
	
	
}
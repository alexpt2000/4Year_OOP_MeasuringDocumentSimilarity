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

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.util.stream.Collectors;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import ie.gmit.sw.Runner.CompareDB;


/* NB: You will need to add the JAR file $TOMCAT_HOME/lib/servlet-api.jar to your CLASSPATH 
 *     variable in order to compile a servlet from a command line.
 */
@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB. The file size in bytes after which the file will be temporarily stored on disk. The default size is 0 bytes.
                 maxFileSize=1024*1024*10,      // 10MB. The maximum size allowed for uploaded files, in bytes
                 maxRequestSize=1024*1024*50)   // 50MB. he maximum size allowed for a multipart/form-data request, in bytes.
public class ServiceHandler extends HttpServlet {

	private String environmentalVariable = null; //Demo purposes only. Rename this variable to something more appropriate
	private static long jobNumber = 0;

	private boolean checkProcessed = true;
	private Part part;
	private String returningResult;

	private BooksDB saveBook = new BooksDB();
	private CompareBook compare = new CompareBook();

	
	

	public void init() throws ServletException {
		ServletContext ctx = getServletContext(); //The servlet context is the application itself.

		environmentalVariable = ctx.getInitParameter("SOME_GLOBAL_OR_ENVIRONMENTAL_VARIABLE"); 
		
		

	}


	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BookService service = null;
		
		//Step 1) Write out the MIME type
		resp.setContentType("text/html"); 
		
		//Step 2) Get a handle on the PrintWriter to write out HTML
		PrintWriter out = resp.getWriter(); 
		
		//Step 3) Get any submitted form data. These variables are local to this method and thread safe...
		String title = req.getParameter("txtTitle");
		String taskNumber = req.getParameter("frmTaskNumber");
		
		if(checkProcessed) {
			part = req.getPart("txtDocument");
		}
		
		// Change "bookTitle" to UpperCase and remove any blank space
		title = title.toUpperCase().replaceAll("\\s+", "");
		
		//Step 4) Process the input and write out the response. 
		//The following string should be extracted as a context from web.xml 
		out.print("<html><head><title>A JEE Application for Measuring Document Similarity</title>");		
		out.print("</head>");		
		out.print("<body>");
		
		
		out.print("<h3>Uploaded Document</h3>");	
		out.print("<font color=\"0000ff\">");	
		
		Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();
	
			checkProcessed = false;
			BufferedReader br = new BufferedReader(new InputStreamReader(part.getInputStream()));
			String line = null;
			
			
	
			CompareBook compareBook = new CompareBook();
			Random r = new Random();
			
			while ((line = br.readLine()) != null) {
				String[] words = line.split("\\s");
	
				for (int i = 0; i < words.length; i++) {
					words[i] = words[i].toUpperCase();
					out.print(words[i] + " ");
				}
	
				assert words.length > 2;
	
				if (words.length > 2) {
					
					final String[] document = Arrays.copyOfRange(words, 1, words.length);
					int docId = r.nextInt(200);
					
					docsAsShingleSets.put(docId, new ArrayList<>(compareBook.asHashes(compareBook.asShingles(document, 3))));
				}
				
	//			for (List<Integer> value : docsAsShingleSets.values()) {
	//			    System.out.println("Value = " + value);
	//			}
				
			out.print("</font>");
					
			}
		
			List<Books> loadDocumentsDB = saveBook.loadAllBooks();
			
			
			Books requestBookResult = new Books(title, taskNumber, docsAsShingleSets);
		
			//saveBook.loadAllBooksCompare(requestBookResult);
			
			
			for (Books books : loadDocumentsDB) {

				System.out.println(String.format("%.2f", (compare.similarityHashMap(requestBookResult, books)))	+ " %" + "\t\t" + books.getBookName());


			}
			


		
		//Output some headings at the top of the generated page
		out.print("<H1>Processing request for Job#: " + taskNumber + "</H1>");
		out.print("<H3>Document Title: " + title + "</H3>");
		
		
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
		out.print("<input name=\"txtTitle\" type=\"hidden\" value=\"" + title + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("</form>");								
		out.print("</body>");	
		out.print("</html>");	
		
		//JavaScript to periodically poll the server for updates (this is ideal for an asynchronous operation)
		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 10000);"); //Refresh every 10 seconds
		out.print("</script>");
	
	}
	


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
}
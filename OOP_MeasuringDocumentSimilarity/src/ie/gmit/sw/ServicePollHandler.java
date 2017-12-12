package ie.gmit.sw;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

public class ServicePollHandler extends HttpServlet {
	
	private static Map<String, Validator> outQueue;
	private boolean checkProcessed;
	
	public void init() throws ServletException {
		ServletContext ctx = getServletContext();
		outQueue = new HashMap<String, Validator>();
		
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html"); 
		PrintWriter out = resp.getWriter(); 
		
		String title = req.getParameter("txtTitle");
		String taskNumber = req.getParameter("frmTaskNumber");
		int counter = 1;
		if (req.getParameter("counter") != null){
			counter = Integer.parseInt(req.getParameter("counter"));
			counter++;
		}

		out.print("<html><head><title>A JEE Application for Measuring Document Similarity</title>");		
		out.print("</head>");		
		out.print("<body>");
		out.print("<H1>Processing request for Job#: " + taskNumber + "</H1>");
		out.print("<H3>Document Title: " + title + "</H3>");
		out.print("<b><font color=\"ff0000\">A total of " + counter + " polls have been made for this request.</font></b> ");
		out.print("Place the final response here... a nice table (or graphic!) of the document similarity...");
		
		
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
				List<BooksResults> returningDefinitons = outQItem.getResult();

				// System.out.println("\nTask " + taskNumber + " Processed");
				// System.out.println("String " + keyWord + " - " + returningDefinitons);
			}
		}
		
		
		
		out.print("<form name=\"frmRequestDetails\">");
		out.print("<input name=\"txtTitle\" type=\"hidden\" value=\"" + title + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("<input name=\"counter\" type=\"hidden\" value=\"" + counter + "\">");
		out.print("</form>");								
		out.print("</body>");	
		out.print("</html>");	
		
		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 5000);"); //Refresh every 5 seconds
		out.print("</script>");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
}
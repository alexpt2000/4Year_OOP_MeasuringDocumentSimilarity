package ie.gmit.sw;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
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

/**
 * The Class ServiceHandler.
 */
/* NB: You will need to add the JAR file $TOMCAT_HOME/lib/servlet-api.jar to your CLASSPATH 
 *     variable in order to compile a servlet from a command line.
 */
@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB. The file size in bytes after which the file will be
														// temporarily stored on disk. The default size is 0 bytes.
		maxFileSize = 1024 * 1024 * 10, // 10MB. The maximum size allowed for uploaded files, in bytes
		maxRequestSize = 1024 * 1024 * 50) // 50MB. he maximum size allowed for a multipart/form-data request, in bytes.

public class ServiceHandler extends HttpServlet {

	private static long jobNumber = 0;
	private final int POOL_SIZE = 6;
	private int timerRefreshPage = 10000;

	private static Map<String, Validator> outQueue;
	private static BlockingQueue<Documents> inQueue;
	private static ExecutorService executor;
	
	private Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();
	private List<String> initialDocument = new ArrayList<>();

	private boolean firstRefreshPage = true;
	private boolean checkProcessed;
	private ArrayList<DocumentResults> returningResult;

	private Runnable work = new ServiceQueue();

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	/*
	 * This method is only called once, when the servlet is first started (like a
	 * constructor). It's the Template Patten in action! Any application-wide
	 * variables should be initialised here. Note that if you set the xml element
	 * <load-on-startup>1</load-on-startup>, this method will be automatically fired
	 * by Tomcat when the web server itself is started.
	 */
	public void init() throws ServletException {
		ServletContext ctx = getServletContext(); // The servlet context is the application itself.
		ctx.getInitParameter("SOME_GLOBAL_OR_ENVIRONMENTAL_VARIABLE");

		// init Queue
		outQueue = new HashMap<String, Validator>();
		inQueue = new LinkedBlockingQueue<Documents>();
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

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		DocumentService service = new DocumentServiceImpl();
		CompareDocuments compareDocument = new CompareDocuments();
		String resultSave = "";
		int count = 0;

		// Step 1) Write out the MIME type
		resp.setContentType("text/html");

		// Step 2) Get a handle on the PrintWriter to write out HTML
		PrintWriter out = resp.getWriter();

		// Step 3) Get any submitted form data. These variables are local to this method
		// and thread safe...
		String documentTitle = req.getParameter("txtTitle");
		String taskNumber = req.getParameter("frmTaskNumber");

		// Step 4) Process the input and write out the response.
		// The following string should be extracted as a context from web.xml
		out.print("<html><head><title>A JEE Application for Measuring Document Similarity</title>");
		out.print("<meta charset=\"utf-8\">");
		out.print("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		out.print(
				"<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\" type=\"text/css\">");
		out.print(
				"<link rel=\"stylesheet\" href=\"https://v40.pingendo.com/assets/bootstrap/bootstrap-4.0.0-beta.1.css\" type=\"text/css\"> ");
		out.print("</head>");
		out.print("<body>");

		// If new request, will set a new jobNumber
		if (taskNumber == null) {
			taskNumber = new String("T" + jobNumber);

			// read file fom the form
			Part part = req.getPart("txtDocument");

			BufferedReader br = new BufferedReader(new InputStreamReader(part.getInputStream()));
			String line = null;
			Random r = new Random();

			// Read each Lime of document
			while ((line = br.readLine()) != null) {
				String[] words = line.split("\\s");
				initialDocument.add(line);

				for (int i = 0; i < words.length; i++) {
					words[i] = words[i].toUpperCase();
				}

				assert words.length > 2;

				if (words.length > 2) {
					final String[] document = Arrays.copyOfRange(words, 1, words.length);
					int docId = r.nextInt(200);
					docsAsShingleSets.put(docId, new ArrayList<>(compareDocument.asHashes(compareDocument.asShingles(document, 3))));
				}
			}

			checkProcessed = false;

			Documents requestDocumentResult = new Documents(documentTitle, docsAsShingleSets);

			// Add job to in-queue
			inQueue.add(requestDocumentResult);

			// Start the Thread
			work = new ServiceQueue(inQueue, outQueue, service, taskNumber);
			executor.execute(work);

			jobNumber++;
		} else {
			if (outQueue.containsKey(taskNumber)) {

				firstRefreshPage = false;

				Validator outQItem = outQueue.get(taskNumber);

				checkProcessed = outQItem.isProcessed();

				if (checkProcessed == true) {
					outQueue.remove(taskNumber);
					returningResult = outQItem.getResult();
					resultSave = outQItem.getResultSave();

					Collections.sort(returningResult, new DocumentResults());

					for (DocumentResults results : returningResult) {
						System.out
								.println("Result.: " + results.getValue() + "%   Docuement Name.: " + results.getDocumentName());
					}
				}
			}
		}

		// Output some headings at the top of the generated page
		out.print("<div class=\"py-5 bg-dark text-white\">");
		out.print("<div class=\"container\">");
		out.print("<div class=\"row\">");
		out.print("<div class=\"col-md-12\">");
		out.print("<h1 class=\"display-3 text-center\">Measuring Document Similarity</h1>");
		out.print("<h3 class=\"display-5 text-center\">Measuring Document: " + documentTitle + "</h3>");
		out.print("</div>");
		out.print("</div>");
		out.print("</div>");
		out.print("</div>");

		out.print("<form name=\"frmRequestDetails\">");
		out.print("<input name=\"txtTitle\" type=\"hidden\" value=\"" + documentTitle + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("</form>");
		out.print("</body>");
		out.print("</html>");

		// refresh just one time
		if (firstRefreshPage) {
			out.print("<script>");
			out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\"," + timerRefreshPage + ");");
			out.print("</script>");
		}

		// Script bootstrap
		out.print(
				"<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>");
		out.print(
				"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js\" integrity=\"sha384-vFJXuSJphROIrBnz7yo7oB41mKfc8JzQZiCq4NCceLEaO4IHwicKwpJf9c9IpFgh\" crossorigin=\"anonymous\"></script>");
		out.print(
				"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js\" integrity=\"sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1\" crossorigin=\"anonymous\"></script>");

		// Script Time for progress bar
		out.print("<script>");
		out.print("$(function() {");
		out.print("var current_progress = 0;");
		out.print("var interval = setInterval(function() {");
		out.print("current_progress += 1;");
		out.print("$(\"#dynamic\")");
		out.print(".css(\"width\", current_progress + \"%\")");
		out.print(".attr(\"aria-valuenow\", current_progress)");
		out.print(".text(current_progress + \"% Complete\");");
		out.print("if (current_progress >= 100)");
		out.print("clearInterval(interval);");
		out.print("}, 100);");
		out.print("});");
		out.print("</script>");

		// Green alert on page if the Document exist on database
		if (!firstRefreshPage) {
			out.print("<div class=\"alert alert-success alert-dismissable\">");
			out.print("<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>");
			out.print(resultSave);
			out.print("</div>");
		}

		// ************************* Start Page Bootstrap *****************************
		out.print("<div class=\"py-5\">");
		out.print("<div class=\"container\">");
		out.print("<div class=\"row\">");
		out.print("<div class=\"col-md-12\">");
		out.print(
				"<p class=\"\">The Jaccard index, also known as Intersection over Union and the Jaccard similarity coefficient, is a statistic used for comparing the similarity and diversity of sample sets. The Jaccard coefficient measures similarity between finite sample sets, and is defined as the size of the intersection divided by the size of the union.</p>");
		out.print("</div>");
		out.print("</div>");
		out.print("<div class=\"row\">");
		out.print("<div class=\"col-md-12 text-center\">");
		out.print(
				"<a class=\"btn btn-primary text-center\" href=\"index.jsp\">Measuring Another Document Similarity</a>");
		out.print("</div>");
		out.print("</div>");
		out.print("</div>");
		out.print("</div>");

		if (firstRefreshPage) {
			// Print the progress bar on screen
			out.print("<div class=\"py-5\">");
			out.print("<div class=\"container\">");
			out.print("<div class=\"row\">");
			out.print("<div class=\"col-md-12\">");
			out.print("<div class=\"progress\">");
			out.print(
					"<div id=\"dynamic\" class=\"progress-bar progress-bar-striped\" role=\"progressbar\" style=\"width: "
							+ 0 + "%\" aria-valuenow=\"50\" aria-valuemin=\"0\" aria-valuemax=\100\">" + 0 + "%</div>");
			out.print("</div>");
			out.print("</div>");
			out.print("</div>");
			out.print("</div>");
			out.print("</div>");
			// End print the progress bar on screen
		}

		// ************************* Start Chart *****************************
		if (!firstRefreshPage) {
			out.print("<script src=\"http://www.chartjs.org/dist/2.7.1/Chart.bundle.js\">");
			out.print("</script><style type=\"text/css\">");
			out.print(
					"@-webkit-keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}@keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}.chartjs-render-monitor{-webkit-animation:chartjs-render-animation 0.001s;animation:chartjs-render-animation 0.001s;}</style>");
			out.print("<script src=\"http://www.chartjs.org/samples/latest/utils.js\"></script>");
			out.print("<style>");
			out.print("canvas {");
			out.print("    -moz-user-select: none;");
			out.print("    -webkit-user-select: none;");
			out.print("    -ms-user-select: none;");
			out.print("}");
			out.print("</style>");

			out.print(
					" <div id=\"container\" style=\"width: 95%;\"height: 95%;\"><div class=\"chartjs-size-monitor\" style=\"position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; overflow: hidden; pointer-events: none; visibility: hidden; z-index: -1;\"><div class=\"chartjs-size-monitor-expand\" style=\"position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;\"><div style=\"position:absolute;width:1000000px;height:1000000px;left:0;top:0\"></div></div><div class=\"chartjs-size-monitor-shrink\" style=\"position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;\"><div style=\"position:absolute;width:200%;height:200%;left:0; top:0\"></div></div></div>");
			out.print(
					"<canvas id=\"canvas\" width=\"1062\" height=\"531\" class=\"chartjs-render-monitor\" style=\"display: block; width: 1062px; height: 531px;\"></canvas>");
			out.print("</div>");
			out.print("<script>");
			out.print("var color = Chart.helpers.color;");
			out.print("var barChartData = {");
			out.print("labels: [");

			// Print the document Name on Chart
			count = 0;
			for (DocumentResults results : returningResult) {
				count++;
				out.print("\"" + results.getDocumentName() + "\",");
				//Limit 10 document
				if (count == 10)
					break;
			}

			out.print(" ],");
			out.print("datasets: [{");
			out.print(" label: '" + documentTitle + "',");
			out.print("backgroundColor: color(window.chartColors.blue).alpha(0.5).rgbString(),");
			out.print(" borderColor: window.chartColors.red,");
			out.print("borderWidth: 1,");
			out.print("data: [");

			// Print the document Value on Chart
			count = 0;
			for (DocumentResults results : returningResult) {
				count++;
				out.print(results.getValue() + ",");
				//Limit 10 document
				if (count == 10)
					break;
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
			out.print("text: 'Compare Measuring Document Similarity (max 10)'");
			out.print("}");
			out.print("}");
			out.print("});");
			out.print("};");
			out.print("</script>");
			// ************************* End Chart *****************************

			// textarea of sample of document
			out.print("<div class=\"py-5\">");
			out.print("<div class=\"container\">");
			out.print("<div class=\"row\">");
			out.print("<div class=\"col-md-6\">");
			out.print("<div class=\"card\">");
			out.print("<div class=\"card-header\">" + "200 lines of document: " + documentTitle + "</div>");
			out.print("<div class=\"card-body h-75\">");

			// Load sample of document limited by 200 lines
			String sampleDocument = "";
			for (int i = 0; i < 200; i++) {
				sampleDocument += initialDocument.get(i);
			}
			initialDocument.clear();

			out.print("<textarea readonly class=\"form-control noresize\" id=\"text\" name=\"text\" rows=\"25\" style=\"min-width: 100%\" style=\"min-height: 100%\">");
			out.print(sampleDocument);
			out.print("</textarea>");
			
			out.print("</div>");
			out.print("</div>");
			out.print("</div>");
			
			// List of Documents results
			out.print("<div class=\"col-md-6\">");
			out.print("<table class=\"table\">");
			out.print("<thead>");
			out.print("<tr>");
			out.print("<th>#</th>");
			out.print("<th>Similarity %</th>");
			out.print("<th>Document Names (max 20)</th>");
			out.print("</tr>");
			out.print("</thead>");
			out.print("<tbody>");

			count = 0;
			for (DocumentResults results : returningResult) {
				count++;

				out.print("<tr>");
				out.print("<td>" + count + "</td>");
				out.print("<td>" + results.getValue() + "%" + "</td>");
				out.print("<td>" + results.getDocumentName() + "</td>");
				out.print("</tr>");
				// Limit by 20 lines of result
				if (count == 20)
					break;
			}

			out.print("</tbody>");
			out.print("</table>");
			out.print("</div>");
			out.print("</div>");
			out.print("</div>");
			out.print("</div>");
			
			firstRefreshPage = true;
		}

		// Status bar
		out.print("<div class=\"py-5 bg-dark text-white\">");
		out.print("<div class=\"container\">");
		out.print("<div class=\"row\">");
		out.print("<div class=\"col-md-12\">");
		out.print("<h6 class=\"display-15 text-center\">Alexander Souza - G00317835</h6>");
		out.print("<h6 class=\"display-15 text-center\">Job#: " + taskNumber + "</h6>");
		out.print("</div>");
		out.print("</div>");
		out.print("</div>");
		out.print("</div>");
		// ************************* End Page Bootstrap *****************************
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
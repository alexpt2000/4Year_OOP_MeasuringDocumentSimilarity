package ie.gmit.sw;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

//TODO

/**
 * The Class BookServiceImpl.
 * 
 * @author Alexander Souza
 * @version 1.0
 * @since Dec 2017
 */
public class DocumentServiceImpl extends UnicastRemoteObject implements DocumentService {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new book service implementation.
	 *
	 * @throws RemoteException the remote exception
	 */
	public DocumentServiceImpl() throws RemoteException {
		super();
	}

	/* (non-Javadoc)
	 * @see ie.gmit.sw.BookService#campareBooks(ie.gmit.sw.Books)
	 */
	public Validator campareDocument(Documents document) throws RemoteException {

		// All results are save into List, list will get value and document name
		ArrayList<DocumentResults> sendResultToPage = new ArrayList<DocumentResults>();
		Validator resultDefinition = new ValidatorImp();
		CompareDocuments compare = new CompareDocuments();
		// Instance Database
		DocumentsDB saveDocument = new DocumentsDB();
		//load all documents from to database file, into a list of documents
		List<Documents> loadDocumentsDB = saveDocument.LoadAllDocuments();

		// control exist document, base if true, the document already exit on database
		boolean existBook = false;
		int resultSililary = 0;

		// for each document into list will compare to document sent by user
		for (Documents documents : loadDocumentsDB) {
			resultSililary = compare.similarityHashMap(document, documents);
			sendResultToPage.add(new DocumentResults(resultSililary, documents.getDocumentName()));

			// compare exist document names
			if (document.getDocumentName().equals(documents.getDocumentName())) {
				existBook = true;
			}
		}

		// base on result, the document will be save on database
		if (existBook) {
			System.out.println("The document " + document.getDocumentName() + " already exist on database.");
			// sent to page the result
			resultDefinition.setResultSave("The document " + document.getDocumentName() + " already exist on database.");
		} else {
			System.out.println("The document " + document.getDocumentName() + " will be save on database.");
			// sent to page the result
			resultDefinition.setResultSave("The document " + document.getDocumentName() + " will be save on database.");

			// save the document
			saveDocument.AddDocumentToDatabase(document);
		}

		// add the result in the Validator instance 
		resultDefinition.setResult(sendResultToPage);

		// Set the definition as Processed into Queue
		resultDefinition.setProcessed();

		return resultDefinition;
	}
}

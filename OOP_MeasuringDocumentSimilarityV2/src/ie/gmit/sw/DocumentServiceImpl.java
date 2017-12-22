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
 * @since Dez 2017
 */
public class DocumentServiceImpl extends UnicastRemoteObject implements DocumentService {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new book service impl.
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

		ArrayList<DocumentResults> sendResultToPage = new ArrayList<DocumentResults>();
		Validator resultDefinition = new ValidatorImp();
		CompareDocuments compare = new CompareDocuments();
		DocumentsDB saveDocument = new DocumentsDB();
		List<Documents> loadDocumentsDB = saveDocument.LoadAllDocuments();

		boolean existBook = false;
		int resultSililary = 0;

		for (Documents documents : loadDocumentsDB) {
			resultSililary = compare.similarityHashMap(document, documents);
			sendResultToPage.add(new DocumentResults(resultSililary, documents.getDocumentName()));

			if (document.getDocumentName().equals(documents.getDocumentName())) {
				existBook = true;
			}
		}

		if (existBook) {
			System.out.println("The document " + document.getDocumentName() + " already exist on database.");
			resultDefinition.setResultSave("The document " + document.getDocumentName() + " already exist on database.");
		} else {
			System.out.println("The document " + document.getDocumentName() + " will be save on database.");
			resultDefinition.setResultSave("The document " + document.getDocumentName() + " will be save on database.");

			saveDocument.AddDocumentToDatabase(document);
		}

		resultDefinition.setResult(sendResultToPage);

		// Set the definition as Processed into Queue
		resultDefinition.setProcessed();

		return resultDefinition;
	}
}

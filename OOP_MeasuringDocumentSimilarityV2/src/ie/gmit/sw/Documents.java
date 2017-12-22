package ie.gmit.sw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Class Documents.
 */
public class Documents {

	private String documentName;
	private Map<Integer, List<Integer>> documentHash = new HashMap<>();

	/**
	 * Instantiates a new documents.
	 */
	public Documents() {
		super();
	}


	public Documents(String documentName) {
		super();
	}


	/**
	 * Instantiates a new documents.
	 *
	 * @param documentName the String documentName
	 * @param documentHash the Map<Integer, List<Integer>> documentHash
	 */
	public Documents(String documentName, Map<Integer, List<Integer>> documentHash) {
		super();
		this.documentName = documentName;
		this.documentHash = documentHash;
	}

	/**
	 * Gets the document name.
	 *
	 * @return the document name
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Sets the document name.
	 *
	 * @param documentName the new document name
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/**
	 * Gets the document hash.
	 *
	 * @return the document hash
	 */
	public Map<Integer, List<Integer>> getDocumentHash() {
		return documentHash;
	}

	/**
	 * Sets the document hash.
	 *
	 * @param documentHash the document hash
	 */
	public void setDocumentHash(Map<Integer, List<Integer>> documentHash) {
		this.documentHash = documentHash;
	}


}

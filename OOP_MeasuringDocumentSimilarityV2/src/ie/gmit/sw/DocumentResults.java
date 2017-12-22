package ie.gmit.sw;

import java.util.Comparator;

/**
 * The Class DocumentResults is responsible for similarity of 
 * document and Document name.
 */
public class DocumentResults implements Comparator<DocumentResults> {
	private int value;
	private String documentName;

	/**
	 * Instantiates a new document results.
	 */
	public DocumentResults() {
		super();
	}

	/**
	 * Instantiates a new document results, takes similarity and documment name.
	 *
	 * @param value the value of similarity
	 * @param documentName the document name
	 */
	public DocumentResults(int value, String documentName) {
		super();
		this.value = value;
		this.documentName = documentName;
	}

	/**
	 * Gets the similarity value.
	 *
	 * @return the  value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(int value) {
		this.value = value;
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

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(DocumentResults arg0, DocumentResults arg1) {
		if (arg0.getValue() < arg1.getValue()) {
			return 1;
		} else {
			return -1;
		}
	}

}

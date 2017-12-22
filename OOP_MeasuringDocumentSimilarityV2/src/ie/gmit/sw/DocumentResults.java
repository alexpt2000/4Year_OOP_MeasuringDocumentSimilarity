package ie.gmit.sw;

import java.util.Comparator;

public class DocumentResults implements Comparator<DocumentResults> {
	private int value;
	private String documentName;

	public DocumentResults() {
		super();
	}

	public DocumentResults(int value, String documentName) {
		super();
		this.value = value;
		this.documentName = documentName;
	}

	public double getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	@Override
	public int compare(DocumentResults arg0, DocumentResults arg1) {
		if (arg0.getValue() < arg1.getValue()) {
			return 1;
		} else {
			return -1;
		}
	}

}

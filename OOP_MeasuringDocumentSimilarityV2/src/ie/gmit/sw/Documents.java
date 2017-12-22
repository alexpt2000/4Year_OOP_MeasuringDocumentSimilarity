package ie.gmit.sw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Documents {

	private String documentName;
	private String taskNumber;
	private Map<Integer, List<Integer>> documentHash = new HashMap<>();

	public Documents() {
		super();
	}

	public Documents(String documentName) {
		super();
	}

	public Documents(String documentName, Map<Integer, List<Integer>> documentHash) {
		super();
		this.documentName = documentName;
		this.documentHash = documentHash;
	}

	public Documents(String documentName, String taskNumber, Map<Integer, List<Integer>> documentHash) {
		super();
		this.documentName = documentName;
		this.taskNumber = taskNumber;
		this.documentHash = documentHash;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public Map<Integer, List<Integer>> getDocumentHash() {
		return documentHash;
	}

	public void setDocumentHash(Map<Integer, List<Integer>> documentHash) {
		this.documentHash = documentHash;
	}

	public String getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(String taskNumber) {
		this.taskNumber = taskNumber;
	}

}

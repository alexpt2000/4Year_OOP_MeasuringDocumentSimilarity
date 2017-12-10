package ie.gmit.sw.db4o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Books {

	private String bookName;
	Map<Integer, List<Integer>> bookHash = new HashMap<>();

	public Books() {
		super();
	}

	public Books(String bookName, Map<Integer, List<Integer>> bookHash) {
		super();
		this.bookName = bookName;
		this.bookHash = bookHash;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public Map<Integer, List<Integer>> getBookHash() {
		return bookHash;
	}

	public void setBookHash(Map<Integer, List<Integer>> bookHash) {
		this.bookHash = bookHash;
	}

}

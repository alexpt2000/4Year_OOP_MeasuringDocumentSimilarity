package ie.gmit.sw;

public class BooksResults {

	private double value;
	private String bookName;

	public BooksResults() {
		super();
	}

	public BooksResults(double value, String bookName) {
		super();
		this.value = value;
		this.bookName = bookName;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

}

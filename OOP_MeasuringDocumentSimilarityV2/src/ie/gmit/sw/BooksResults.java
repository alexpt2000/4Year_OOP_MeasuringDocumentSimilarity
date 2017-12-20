package ie.gmit.sw;

import java.util.Comparator;

public class BooksResults implements Comparator<BooksResults>{
	private int value;
	private String bookName;
	
	
	public BooksResults() {
		super();
	}


	public BooksResults(int value, String bookName) {
		super();
		this.value = value;
		this.bookName = bookName;
	}


	public double getValue() {
		return value;
	}


	public void setValue(int value) {
		this.value = value;
	}


	public String getBookName() {
		return bookName;
	}


	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	@Override
	public int compare(BooksResults arg0, BooksResults arg1) {
        if(arg0.getValue() < arg1.getValue()){
            return 1;
        } else {
            return -1;
        }
	}
	
	







	
}

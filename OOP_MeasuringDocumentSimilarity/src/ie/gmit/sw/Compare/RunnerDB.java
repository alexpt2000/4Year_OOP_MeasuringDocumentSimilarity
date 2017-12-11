package ie.gmit.sw.Compare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import ie.gmit.sw.db4o.Books;
import ie.gmit.sw.db4o.BooksDB;

public class RunnerDB {

	public static void main(String[] args) throws IOException {
		//private List<Books> book = new ArrayList<Books>();
		BooksDB saveBook = new BooksDB();
		
		CompareDB compareDOC1 = new CompareDB();
		final String datafileDOC1 = "C:/books/WarAndPeaceLeoTolstoy.txt";
		Stream<String> dataFileStreamDOC1 = Files.lines(Paths.get(datafileDOC1));
		List<Books> documentHashedShinglesDOC1 = compareDOC1.computeShingles(datafileDOC1, dataFileStreamDOC1);
				
		CompareDB compareDOC2 = new CompareDB();
		//final String datafileDOC2 = "C:/books/bible1.txt";
		final String datafileDOC2 = "C:/books/WarAndPeaceLeoTolstoyModify.txt";
		//final String datafileDOC2 = "C:/books/WarAndPeaceLeoTolstoy.txt";
		Stream<String> dataFileStreamDOC2 = Files.lines(Paths.get(datafileDOC2));
		List<Books> documentHashedShinglesDOC2 = compareDOC2.computeShingles(datafileDOC2, dataFileStreamDOC2);
		
		CompareDB compare = new CompareDB();
		System.out.println(String.format("%.2f", (compare.similarityHashMap(documentHashedShinglesDOC1, documentHashedShinglesDOC2))) + " %");

		
		
		saveBook.addBookssToDatabase(documentHashedShinglesDOC1);
		saveBook.showAllBooks();
		
		saveBook.addBookssToDatabase(documentHashedShinglesDOC2);
		saveBook.showAllBooks();
		saveBook.getBooksNative(documentHashedShinglesDOC2.get(0));
		
		//BooksDB saveBook2 = new BooksDB(documentHashedShinglesDOC2);
		
	}
}

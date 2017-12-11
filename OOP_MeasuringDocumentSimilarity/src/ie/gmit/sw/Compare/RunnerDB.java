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
		BooksDB saveBook = new BooksDB();
		CompareDB compare = new CompareDB();

		List<Books> loadDocumentsDB = saveBook.loadAllBooks();

		//final String docName = "Bible - King James";
		final String docName = "War And Peace Leo Tolstoy";
		//final String docName = "War And Peace Leo Tolstoy - Modify";
		
		final String datafileDOC = "C:/books/WarAndPeaceLeoTolstoy.txt";
		//final String datafileDOC = "C:/books/WarAndPeaceLeoTolstoyModify.txt";
		//final String datafileDOC = "C:/books/bible1.txt";

		Stream<String> dataFileStreamDOC = Files.lines(Paths.get(datafileDOC));
		Books documentHashedShinglesDOC = compare.computeShingles(docName, dataFileStreamDOC);

		System.out.println(docName);
		System.out.println("==============================================");

		for (Books books : loadDocumentsDB) {

			System.out.println(String.format("%.2f", (compare.similarityHashMap(documentHashedShinglesDOC, books)))
					+ " %" + "\t\t" + books.getBookName());

		}
		
		//saveBook.addBookssToDatabase(documentHashedShinglesDOC);

	}
}

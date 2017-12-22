package ie.gmit.sw.tester;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import ie.gmit.sw.Documents;
import ie.gmit.sw.DocumentsDB;
import ie.gmit.sw.CompareDocuments;

public class RunnerDB {

	public static void main(String[] args) throws IOException {
		DocumentsDB saveDocument = new DocumentsDB();
		CompareDB compare = new CompareDB();
		List<Documents> loadDocumentsDB = saveDocument.LoadAllDocuments();
		boolean existDocument = false;

		// final String docName = "War And Peace Leo Tolstoy";
		final String docName = "War And Peace Leo Tolstoy - Modify";
		// final String docName = "Bible - King James";

		// final String datafileDOC = "C:/books/WarAndPeaceLeoTolstoy.txt";
		final String datafileDOC = "C:/books/WarAndPeaceLeoTolstoyModify.txt";
		// final String datafileDOC = "C:/books/bible1.txt";

		Stream<String> dataFileStreamDOC = Files.lines(Paths.get(datafileDOC));

		Documents documentHashedShinglesDOC = compare.computeShingles(docName, dataFileStreamDOC);

		System.out.println(docName);
		System.out.println("==============================================");

		for (Documents documents : loadDocumentsDB) {
			System.out.println(String.format("%.2f", (compare.similarityHashMap(documentHashedShinglesDOC, documents)))
					+ " %" + "\t\t" + documents.getDocumentName());

			if (docName.equals(documents.getDocumentName())) {
				existDocument = true;
			}
		}
		if (existDocument) {
			System.out.println("This document exist on database.");
		} else {
			System.out.println("The document " + docName + " will be save on database.");
			saveDocument.AddDocumentToDatabase(documentHashedShinglesDOC);
		}
	}
}

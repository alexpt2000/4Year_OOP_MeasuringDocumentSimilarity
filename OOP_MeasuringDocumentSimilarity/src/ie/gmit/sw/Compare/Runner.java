package ie.gmit.sw.Compare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Runner {

	public static void main(String[] args) throws IOException {
		
		Compare compareDOC1 = new Compare();
		final String datafileDOC1 = "C:/books/WarAndPeaceLeoTolstoy.txt";
		Stream<String> dataFileStreamDOC1 = Files.lines(Paths.get(datafileDOC1));
		Map<Integer, List<Integer>> documentHashedShinglesDOC1 = compareDOC1.computeShingles(dataFileStreamDOC1);
				
		Compare compareDOC2 = new Compare();
		final String datafileDOC2 = "C:/books/WarAndPeaceLeoTolstoyModify.txt";
		Stream<String> dataFileStreamDOC2 = Files.lines(Paths.get(datafileDOC2));
		Map<Integer, List<Integer>> documentHashedShinglesDOC2 = compareDOC2.computeShingles(dataFileStreamDOC2);
		
		Compare compare = new Compare();
		System.out.println(String.format("%.2f", (compare.similarityHashMap(documentHashedShinglesDOC1, documentHashedShinglesDOC2)) * 10) + " %");

	}
}

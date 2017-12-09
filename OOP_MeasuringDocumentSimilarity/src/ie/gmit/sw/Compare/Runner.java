package ie.gmit.sw.Compare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Runner {

	public static void main(String[] args) throws IOException {
		
		Compare compareDOC1 = new Compare();
		final String datafileDOC1 = "C:/books/doc1.txt";
		Stream<String> dataFileStreamDOC1 = Files.lines(Paths.get(datafileDOC1));
		Map<Integer, List<Integer>> documentHashedShinglesDOC1 = compareDOC1.computeShingles(dataFileStreamDOC1);
		
		
		Compare compareDOC2 = new Compare();
		final String datafileDOC2 = "C:/books/doc2.txt";
		Stream<String> dataFileStreamDOC2 = Files.lines(Paths.get(datafileDOC2));
		Map<Integer, List<Integer>> documentHashedShinglesDOC2 = compareDOC2.computeShingles(dataFileStreamDOC2);
	
		
		Compare compare = new Compare();
		
		//System.out.println(compare.similarityHashMap(documentHashedShinglesDOC1, documentHashedShinglesDOC2));
		System.out.println(String.format("%.2f", (compare.similarityHashMap(documentHashedShinglesDOC1, documentHashedShinglesDOC2)) * 100) + " %");
		
//		Set<String> text1 = new HashSet<String>();
//		text1.add("Dora");
//		text1.add("is");
//		text1.add("a");
//		text1.add("stupid");
//		text1.add("lovely");
//		text1.add("happy");
//		text1.add("puppy");
//
//		Set<String> text2 = new HashSet<String>();
//		text2.add("Dora");
//		text2.add("is");
//		text2.add("a");
//		text2.add("stupid");
//		text2.add("lovely");
//		text2.add("happy");
//		text2.add("puppy");
//
//		Set<String> text3 = new HashSet<String>();
//		text3.add("Dora");
//		text3.add("the");
//		text3.add("happy");
//		text3.add("puppy");
//		text3.add("loves");
//		text3.add("Shirley");
//
//		Set<String> text4 = new HashSet<String>();
//		text4.add("Dora");
//		text4.add("stupid");
//		text4.add("is");
//		text4.add("lovely");
//		text4.add("happy");
//		text4.add("a");
//		text4.add("puppy");
//
//		Compare mh = new Compare();
//
//		System.out.println(
//				String.format("%.2f", (mh.similarity(text1, text2, text1.size() + text2.size()) * 100)) + " %");
//		System.out.println(
//				String.format("%.2f", (mh.similarity(text1, text3, text1.size() + text3.size()) * 100)) + " %");
//		System.out.println(
//				String.format("%.2f", (mh.similarity(text1, text4, text1.size() + text4.size()) * 100)) + " %");
//		// System.out.format("%.3f", mh.similarity(text1, text4, text1.size() +
//		// text4.size()));

	}

}

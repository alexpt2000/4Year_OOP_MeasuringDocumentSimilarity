package ie.gmit.sw.Compare;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Compare {

	public double similarity(Set<String> text1, Set<String> text2, int numHash) {

		long[][] minHashValues = new long[2][numHash];
		Arrays.fill(minHashValues[0], Long.MAX_VALUE);
		Arrays.fill(minHashValues[1], Long.MAX_VALUE);
		Random r = new Random(200);
		int similarity = 0;
		for (int i = 0; i < numHash; i++) {
			int a = r.nextInt() + 1;
			for (String s : text1)
				minHashValues[0][i] = Math.min(minHashValues[0][i], getHash(s.hashCode(), a, i));
			for (String s : text2)
				minHashValues[1][i] = Math.min(minHashValues[1][i], getHash(s.hashCode(), a, i));
			if (minHashValues[0][i] == minHashValues[1][i]) {
				similarity++;
			}
		}
		return (double) similarity / numHash;
	}
	
	
	public double similarityHashMap(Map<Integer, List<Integer>> book1, Map<Integer, List<Integer>> bookDB) {

		int sizeHanh = book1.size() + bookDB.size();
		
		long[][] minHashValues = new long[2][sizeHanh];
		Arrays.fill(minHashValues[0], Long.MAX_VALUE);
		Arrays.fill(minHashValues[1], Long.MAX_VALUE);
		
		Random r = new Random(200);
		int similarity = 0;

		Set<Integer> book1Key = book1.keySet();
		Set<Integer> bookDBKey = bookDB.keySet();
		
		for (int i = 0; i < sizeHanh; i++) {
			int a = r.nextInt() + 1;
			for(Integer key : book1Key)
			{
			   //System.out.println(((List)book1.get(key)).toString());
				minHashValues[0][i] = Math.min(minHashValues[0][i], getHash(key, a, i));
				//minHashValues[0][i] = key;
			}
			
			for(Integer key : bookDBKey)
			{
				minHashValues[1][i] = Math.min(minHashValues[1][i], getHash(key, a, i));
				//minHashValues[1][i] = key;
			}
			
			if (minHashValues[0][i] == minHashValues[1][i]) {
				similarity++;
			}
		}
		
//		for (int i = 0; i < sizeHanh; i++) {
//			int a = r.nextInt() + 1;
//			for (String s : book1) {
//				
//				minHashValues[0][i] = Math.min(minHashValues[0][i], getHash(s.hashCode(), a, i));
//			}
//			for (String s : bookDB) {
//				minHashValues[1][i] = Math.min(minHashValues[1][i], getHash(s.hashCode(), a, i));
//			}
//			
//			if (minHashValues[0][i] == minHashValues[1][i]) {
//				similarity++;
//			}
//		}
		return (double) similarity / sizeHanh;
	}
	

	// using circular shifts: http://en.wikipedia.org/wiki/Circular_shift
	// http://stackoverflow.com/questions/5844084/java-circular-shift-using-bitwise-operations
	// circular shifts XOR random number
	private long getHash(int value, int random, int shift) {
		// the first hash function comes from string.hashCode()
		// http://www.codatlas.com/github.com/openjdk-mirror/jdk7u-jdk/master/src/share/classes/java/lang/String.java?keyword=String&line=1494
		if (shift == 0)
			return value;
		int rst = (value >>> shift) | (value << (Integer.SIZE - shift));
		return rst ^ random;
	}

	static Map<Integer, List<Integer>> computeShingles(Stream<String> dataFileStream) throws IOException {
		Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();
		Random r = new Random();
		//int[] populate = new int[200];
		
//		for (int i = 0; i < populate.length; i++) {
//			populate[i] = r.nextInt(200);
//			System.out.println(populate[i]);
//		}
		
		 //int docId = 0;

		dataFileStream.forEach(line -> {
			String[] words = line.split("\\s");
			assert words.length > 2;
			//final String docId = getDocumentName(words);
			
			final String[] document = getDocument(words);
			
			int docId = r.nextInt(200);
			docsAsShingleSets.put(docId, new ArrayList<>(asHashes(asShingles(document, 3))));
		});

		// Loop
		//docsAsShingleSets.forEach((k, v) -> System.out.println(k + "=" + v));
		
//		docsAsShingleSets.entrySet().stream().forEach((entry) -> {
//			Object currentKey = entry.getKey();
//			Object currentValue = entry.getValue();
//			System.out.println(currentKey + "=" + currentValue);
//		});

		return docsAsShingleSets;
	}

	static List<List<String>> asShingles(final String[] document, final int length) {
		List<List<String>> shinglesInDoc = new ArrayList<>();
		for (int i = 0; i < (document.length - length + 1); ++i) {
			String[] t = Arrays.copyOfRange(document, i, i + length);
			shinglesInDoc.add(Arrays.asList(t));
		}
		return shinglesInDoc;
	}

	static List<Integer> asHashes(final List<List<String>> shingles) {
		HashFunction hf = Hashing.murmur3_32();

		return shingles.stream().map(shingle -> {
			String sentence = shingle.stream().collect(Collectors.joining(" "));
			return hf.newHasher().putString(sentence, Charset.defaultCharset()).hash().asInt();
		}).collect(Collectors.toList());
	}

//	static String getDocumentName(String[] line) {
//		return line[0];
//	}

	static String[] getDocument(String[] line) {
		return Arrays.copyOfRange(line, 1, line.length);
	}
}

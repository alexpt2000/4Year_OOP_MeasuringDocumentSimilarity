package ie.gmit.sw.tester;

import java.awt.print.Book;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import ie.gmit.sw.Books;
import ie.gmit.sw.BooksDB;

public class CompareDB {

	public double similarityHashMap(Books book1, Books bookDB) {
		int similarity = 0;

		int similaritySize = 0;
		int CountsimilaritySize = 0;

		// System.out.println(" Booke name" + book1.getBookName());

		for (Map.Entry<Integer, List<Integer>> me : book1.getBookHash().entrySet()) {
			int key = me.getKey();
			List<Integer> valueList = me.getValue();

			// System.out.println("Key: " + key);
			// System.out.println(b2.getBookName());

			for (Map.Entry<Integer, List<Integer>> meDB : bookDB.getBookHash().entrySet()) {
				int keyDB = meDB.getKey();

				List<Integer> valueListDB = meDB.getValue();

				int numHashSize = valueList.size() + valueListDB.size();

				if (numHashSize == 0) {
					numHashSize = 1;
				}

				long[][] minHashValues = new long[2][numHashSize];
				Arrays.fill(minHashValues[0], Long.MAX_VALUE);
				Arrays.fill(minHashValues[1], Long.MAX_VALUE);

				Random r = new Random(200);

				for (int i = 0; i < numHashSize; i++) {
					int a = r.nextInt() + 1;

					for (Integer s : valueList) {
						minHashValues[0][i] = Math.min(minHashValues[0][i], getHash(s, a, i));
					}

					for (Integer s : valueListDB) {
						minHashValues[1][i] = Math.min(minHashValues[1][i], getHash(s, a, i));
					}

					if (minHashValues[0][i] == minHashValues[1][i]) {

						similarity++;
					}
				}

				CountsimilaritySize++;
				similaritySize += numHashSize;
			}

		}

		double totalSimilarity = similarity / (similaritySize / CountsimilaritySize);

		if (totalSimilarity > 100) {
			totalSimilarity = 100;
		}

		return totalSimilarity;

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

	static Books computeShingles(String bookName, Stream<String> dataFileStream) throws IOException {

		Map<Integer, List<Integer>> docsAsShingleSets = new HashMap<>();
		Random r = new Random();

		dataFileStream.forEach(line -> {
			String[] words = line.split("\\s");

			for (int i = 0; i < words.length; i++) {
				words[i] = words[i].toUpperCase();
			}

			assert words.length > 2;

			if (words.length > 2) {
				final String[] document = getDocument(words);

				int docId = r.nextInt(200);
				docsAsShingleSets.put(docId, new ArrayList<>(asHashes(asShingles(document, 3))));
			}
		});

		Books book = new Books(bookName, "T0", docsAsShingleSets);

		return book;
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

	static String[] getDocument(String[] line) {
		return Arrays.copyOfRange(line, 1, line.length);
	}
}

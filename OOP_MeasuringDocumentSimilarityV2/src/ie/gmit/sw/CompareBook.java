package ie.gmit.sw;


import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class CompareBook implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public int similarityHashMap(Books book1, Books bookDB) {
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

		int totalSimilarity = similarity / (similaritySize / CountsimilaritySize);

		if (totalSimilarity > 100) {
			totalSimilarity = 100;
		}

		return totalSimilarity;

	}

	// using circular shifts: http://en.wikipedia.org/wiki/Circular_shift
	// http://stackoverflow.com/questions/5844084/java-circular-shift-using-bitwise-operations
	// circular shifts XOR random number
	public long getHash(int value, int random, int shift) {
		// the first hash function comes from string.hashCode()
		// http://www.codatlas.com/github.com/openjdk-mirror/jdk7u-jdk/master/src/share/classes/java/lang/String.java?keyword=String&line=1494
		if (shift == 0)
			return value;
		int rst = (value >>> shift) | (value << (Integer.SIZE - shift));
		return rst ^ random;
	}



	public List<List<String>> asShingles(final String[] document, final int length) {
		List<List<String>> shinglesInDoc = new ArrayList<>();
		for (int i = 0; i < (document.length - length + 1); ++i) {
			String[] t = Arrays.copyOfRange(document, i, i + length);
			shinglesInDoc.add(Arrays.asList(t));
		}
		return shinglesInDoc;
	}

	public List<Integer> asHashes(final List<List<String>> shingles) {
		HashFunction hf = Hashing.murmur3_32();

		return shingles.stream().map(shingle -> {
			String sentence = shingle.stream().collect(Collectors.joining(" "));
			return hf.newHasher().putString(sentence, Charset.defaultCharset()).hash().asInt();
		}).collect(Collectors.toList());
	}

	public String[] getDocument(String[] line) {
		return Arrays.copyOfRange(line, 1, line.length);
	}
}

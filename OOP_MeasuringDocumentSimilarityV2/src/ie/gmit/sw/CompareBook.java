package ie.gmit.sw;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

// TODO: Auto-generated Javadoc
/**
 * The Class CompareBook.
 */
public class CompareBook implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Similarity hash map.
	 *
	 * @param book1 the book 1
	 * @param bookDB the book DB
	 * @return the int
	 */
	public int similarityHashMap(Books book1, Books bookDB) {
		int similarity = 0;
		int similaritySize = 0;
		int CountsimilaritySize = 0;

		for (Map.Entry<Integer, List<Integer>> me : book1.getBookHash().entrySet()) {
			int key = me.getKey();
			List<Integer> valueList = me.getValue();

			for (Map.Entry<Integer, List<Integer>> meDB : bookDB.getBookHash().entrySet()) {
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

	/**
	 * Gets the hash.
	 *
	 * @param value the value
	 * @param random the random
	 * @param shift the shift
	 * @return the hash
	 */
	public long getHash(int value, int random, int shift) {
		if (shift == 0)
			return value;

		int rst = (value >>> shift) | (value << (Integer.SIZE - shift));

		return rst ^ random;
	}

	/**
	 * As shingles.
	 *
	 * @param document the document
	 * @param length the length
	 * @return the list
	 */
	public List<List<String>> asShingles(final String[] document, final int length) {
		List<List<String>> shinglesInDoc = new ArrayList<>();

		for (int i = 0; i < (document.length - length + 1); ++i) {
			String[] t = Arrays.copyOfRange(document, i, i + length);
			shinglesInDoc.add(Arrays.asList(t));
		}

		return shinglesInDoc;
	}

	/**
	 * As hashes.
	 *
	 * @param shingles the shingles
	 * @return the list
	 */
	public List<Integer> asHashes(final List<List<String>> shingles) {
		HashFunction hf = Hashing.murmur3_32();

		return shingles.stream().map(shingle -> {
			String sentence = shingle.stream().collect(Collectors.joining(" "));
			return hf.newHasher().putString(sentence, Charset.defaultCharset()).hash().asInt();
		}).collect(Collectors.toList());
	}

}

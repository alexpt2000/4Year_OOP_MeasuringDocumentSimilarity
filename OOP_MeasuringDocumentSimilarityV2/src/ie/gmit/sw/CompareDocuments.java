package ie.gmit.sw;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;


/**
 * The Class Compare Documents, send by user and document into Database.
 * 
 * @author Alexander Souza
 * @version 1.0
 * @since Dec 2017
 */
public class CompareDocuments implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Similarity hash map.
	 * 
	 * Returns an Integer representing the Similarity of the documents provided as parameters.
	 * 
	 * @param document provide by user.
	 * @param documentDB is the document into Database
	 * @return the int, the value as % Similarity
	 */
	public int similarityHashMap(Documents document, Documents documentDB) {
		int similarity = 0;
		int similaritySize = 0;
		int CountsimilaritySize = 0;

		// Read each line from the document send by user
		for (Map.Entry<Integer, List<Integer>> doc : document.getDocumentHash().entrySet()) {
			int key = doc.getKey();
			List<Integer> valueList = doc.getValue();

			// Read each line from each document into the database
			for (Map.Entry<Integer, List<Integer>> docDB : documentDB.getDocumentHash().entrySet()) {
				List<Integer> valueListDB = docDB.getValue();

				// Avoid if not similarity between documents, set numHashSize =1
				// Otherwise the total sends an error, because similarity would divide by 0
				int numHashSize = valueList.size() + valueListDB.size();
				if (numHashSize == 0) {
					numHashSize = 1;
				}

				// creates the Arrays and fill whit the MAX values
				long[][] minHashValues = new long[2][numHashSize];
				Arrays.fill(minHashValues[0], Long.MAX_VALUE);
				Arrays.fill(minHashValues[1], Long.MAX_VALUE);

				// The similarity will be performed only on 200 value, at random, to 
				//obtain a media, and it allows the system to be faster
				Random r = new Random(200);
				for (int i = 0; i < numHashSize; i++) {
					int a = r.nextInt() + 1;

					//Reading all the values from the document provided by the user is looking 
					//for the lowest value.
					for (Integer s : valueList) {
						minHashValues[0][i] = Math.min(minHashValues[0][i], getHash(s, a, i));
					}
					
					//Reading all the values from the document provided by DB is looking 
					//for the lowest value.
					for (Integer s : valueListDB) {
						minHashValues[1][i] = Math.min(minHashValues[1][i], getHash(s, a, i));
					}
					
					//If there is similarity, the value will be increased.
					if (minHashValues[0][i] == minHashValues[1][i]) {
						similarity++;
					}
				}

				// It counts all similarities so that it is used to find a more 
				//accurate average to get the final result.
				CountsimilaritySize++;
				
				//Hash Size
				similaritySize += numHashSize;
			}
		}

		//Total similarity will be divided by the Hash numbers.
		int totalSimilarity = similarity / (similaritySize / CountsimilaritySize);

		// Case the value is over 100, the value will set to 100
		if (totalSimilarity > 100) {
			totalSimilarity = 100;
		}

		return totalSimilarity;
	}

	/**
	 * Gets the hash values.
	 *
	 * @param In the value
	 * @param In the random
	 * @param In the shift
	 * @return the hash
	 */
	public long getHash(int value, int random, int shift) {
		if (shift == 0)
			return value;

		int rst = (value >>> shift) | (value << (Integer.SIZE - shift));

		return rst ^ random;
	}

	/**
	 * Convert each Array String line As shingles.
	 *
	 * @param String[] the document
	 * @param Int the length
	 * @return the list asShingles
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
	 * @return the list asHashes
	 */
	public List<Integer> asHashes(final List<List<String>> shingles) {
		HashFunction hf = Hashing.murmur3_32();

		return shingles.stream().map(shingle -> {
			String sentence = shingle.stream().collect(Collectors.joining(" "));
			return hf.newHasher().putString(sentence, Charset.defaultCharset()).hash().asInt();
		}).collect(Collectors.toList());
	}

}

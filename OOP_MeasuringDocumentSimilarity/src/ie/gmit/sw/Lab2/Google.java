package ie.gmit.sw.Lab2;

import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Google {
	final static int numberOfHashes = 200;
	final static int maxShingleId = (int) (Math.pow(2, 32) - 1);
	final static long nextPrime = 4294967311L;
	final static Integer[] coeffA = pickRandomCoeffs(numberOfHashes);
	final static Integer[] coeffB = pickRandomCoeffs(numberOfHashes);

	static Integer[] pickRandomCoeffs(int k) {
		Random rand = new Random();
		Set<Integer> coefficients = new HashSet<>();
		while (coefficients.size() < k)
			coefficients.add(rand.nextInt());
		return coefficients.toArray(new Integer[k]);
	}

	static List<Long> asSignature(final List<Integer> shingleHashes) {
		List<Long> signature = Collections.synchronizedList(new ArrayList<>());

		// For each of the random hash functions
		IntStream.range(0, numberOfHashes).parallel().forEach(i -> {
			// For each shingle in the document
			Long minHashCode = shingleHashes.stream()
					.mapToLong(shingleHash -> ((long) coeffA[i] * (long) shingleHash + coeffB[i]) % nextPrime).min()
					.getAsLong();
			signature.add(minHashCode);
		});
		assert signature.size() == numberOfHashes;
		return signature;
	}

	static Map<String, List<Long>> getSignatures(Map<String, List<Integer>> shingledDocuments) {
		Map<String, List<Long>> signatures = new HashMap<>();
		shingledDocuments.forEach((document, shingles) -> {signatures.put(document, asSignature(shingles));});
		return signatures;
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

	static String getDocumentName(String[] line) {
		return line[0];
	}

	static String[] getDocument(String[] line) {
		return Arrays.copyOfRange(line, 1, line.length);
	}

	static Map<String, List<Integer>> computeShingles(Stream<String> dataFileStream) throws IOException {
		Map<String, List<Integer>> docsAsShingleSets = new HashMap<>();

		dataFileStream.forEach(line -> {
			String[] words = line.split("\\s");
			assert words.length > 2;
			final String docId = getDocumentName(words);
			final String[] document = getDocument(words);
			docsAsShingleSets.put(docId, new ArrayList<>(asHashes(asShingles(document, 3))));
		});
		return docsAsShingleSets;
	}

	static Set<String> collapseWithSpaces(Set<List<String>> a) {
		return a.stream().map(array -> {
			return array.stream().collect(Collectors.joining(" "));
		}).collect(Collectors.toSet());
	}

	static double jaccardArraySimilarity(Set<List<String>> a, Set<List<String>> b) {
		return jaccardSimilarity(collapseWithSpaces(a), collapseWithSpaces(b));
	}

	static <T> double jaccardSimilarity(Set<T> a, Set<T> b) {
		// | A n B | / ( |A| + |B| - | A n B|)
		double intersectionSize = Sets.intersection(a, b).size();
		return intersectionSize / (a.size() + b.size() - intersectionSize);
	}

	static int countCommonElements(final List<Long> a, final List<Long> b) {
		List<Long> common = new ArrayList<>(a);
		common.retainAll(b);
		return common.size();
	}

	public static Map<String, Map<String, Double>> compareAllSignatures(final Map<String, List<Long>> docSignatures) {
		Map<String, Map<String, Double>> estJaccardSimilarity = new HashMap<>();

		for (String document : docSignatures.keySet()) {
			List<Long> signaure1 = docSignatures.get(document);
			Map<String, Double> similarityToDocument = new HashMap<>();
			for (String document2 : docSignatures.keySet()) {
				if (document == document2)
					continue;
				if (estJaccardSimilarity.containsKey(document2)
						&& estJaccardSimilarity.get(document2).containsKey(document))
					continue;

				List<Long> signature2 = docSignatures.get(document2);
				int count = countCommonElements(signaure1, signature2);
				similarityToDocument.put(document2, count / (double) numberOfHashes);
			}
			estJaccardSimilarity.put(document, similarityToDocument);
		}
		return estJaccardSimilarity;
	}

	public static void main(final String[] args) throws IOException {
		//final String datafile = args[0];
		final String datafile = "C:/books/PoblachtNaHEireann.txt";
		final double minimumSimilarity = 0.80;
		assert maxShingleId == Integer.MAX_VALUE;
		assert maxShingleId < nextPrime;

		try (Stream<String> dataFileStream = Files.lines(Paths.get(datafile))) {
			Map<String, List<Integer>> documentHashedShingles = computeShingles(dataFileStream);
			Map<String, List<Long>> signatures = getSignatures(documentHashedShingles);
			//Map<String, Map<String, Double>> estimatedSimilarity = compareAllSignatures(signatures);

			System.out.println("==== Matched Document ====");

//			estimatedSimilarity.forEach((document, relatedDocumentMap) -> {
//				relatedDocumentMap.forEach((doc2, estJs) -> {
//					if (document == doc2)
//						return;
//					if (estJs > minimumSimilarity)
//						System.out.println(document + " -> " + doc2 + " = " + estJs);
//				});
//			});
		}
		return;
	}

}
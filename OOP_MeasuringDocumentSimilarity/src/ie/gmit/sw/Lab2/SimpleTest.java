package ie.gmit.sw.Lab2;

import java.util.*;

class MinHashTester {
	// numHash: number of hash functions needed
	// in this small case, we set it size of text a + size of text b in the test
	// case
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
}

public class SimpleTest {

	public static void main(String[] args) {
		
		Set<String> text1 = new HashSet<String>();
		text1.add("Dora");
		text1.add("is");
		text1.add("a");
		text1.add("stupid");
		text1.add("lovely");
		text1.add("happy");
		text1.add("puppy");
		
		Set<String> text2 = new HashSet<String>();
		text2.add("Dora");
		text2.add("is");
		text2.add("a");
		text2.add("stupid");
		text2.add("lovely");
		text2.add("happy");
		text2.add("puppy");

		Set<String> text3 = new HashSet<String>();
		text3.add("Dora");
		text3.add("the");
		text3.add("happy");
		text3.add("puppy");
		text3.add("loves");
		text3.add("Shirley");

		Set<String> text4 = new HashSet<String>();
		text4.add("Dora");
		text4.add("stupid");
		text4.add("is");
		text4.add("lovely");
		text4.add("happy");
		text4.add("a");
		text4.add("puppy");
		
		MinHashTester mh = new MinHashTester();
		
		System.out.println(String.format("%.2f", (mh.similarity(text1, text2, text1.size() + text2.size())*100)) + " %");
		System.out.println(String.format("%.2f", (mh.similarity(text1, text3, text1.size() + text3.size())*100)) + " %");
		System.out.println(String.format("%.2f", (mh.similarity(text1, text4, text1.size() + text4.size())*100)) + " %");
		//System.out.format("%.3f", mh.similarity(text1, text4, text1.size() + text4.size()));

	}

}

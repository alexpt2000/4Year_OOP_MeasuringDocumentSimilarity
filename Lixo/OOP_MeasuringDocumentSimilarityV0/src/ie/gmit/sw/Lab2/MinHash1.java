package ie.gmit.sw.Lab2;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;


/**
* A simple MinHash implementation inspired by https://github.com/jmhodges/minhash
*
* @author tpeng (pengtaoo@gmail.com)
*/
public class MinHash1 {

    /** The hash. */
    private HashFunction hash = Hashing.murmur3_32();

    /**
     * Hash.
     *
     * @param string the string
     * @return the string
     */
    public String hash(String string) {
        int min = Integer.MAX_VALUE;
        for (int i=0; i<string.length(); i++) {
            int c = string.charAt(i);
            int n = hash.hashInt(c).asInt();
            if (n < min) {
                min = n;
            }
        }
        return Integer.toHexString(min);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String... args) {
        
    	MinHash1 minHash = new MinHash1();
        
        System.out.println(minHash.hash("Alexander Souza Vanessa"));
        System.out.println(minHash.hash("jj"));
        System.out.println(minHash.hash("abcd"));
    }
}
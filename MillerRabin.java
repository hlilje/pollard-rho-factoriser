import java.math.BigInteger;
import java.util.Random;

/**
 * Class for performing Miller-Rabin's primality test.
 */
public class MillerRabin {

    private static final BigInteger ZERO  = BigInteger.ZERO;
    private static final BigInteger ONE   = BigInteger.ONE;
    private static final BigInteger TWO   = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    // Repeat 100 times for error 2^100
    private static final int ITERATIONS = 100;

    private static final Random rand = new Random();

    /**
     * Test the if a witness n as composite with Miller-Rabin.
     */
    private static boolean isWitness(BigInteger a, BigInteger n) {
        // Compute t and u such that p - 1 = 2^t * u, where u is odd
        int t = 1;
        BigInteger u = n.subtract(ONE).divide(TWO);

        while (u.mod(TWO).equals(ZERO)) {
            u = u.divide(TWO);
            ++t;
        }

        // Check if a witness n as composite
        BigInteger x = Maths.modPow(a, u, n);

        for (int i=0; i<t; ++i) {
            BigInteger xNew = x.multiply(x).mod(n);

            if (xNew.equals(ONE) && !x.equals(ONE) && !x.equals(n.subtract(ONE)))
                return true;
            x = xNew;
        }

        return !x.equals(ONE);
    }

    /**
     * Test if n is probably prime according to the Miller-Rabin test.
     */
    public static boolean isProbablePrime(BigInteger n) {
        // Handle simple cases and if it is even
        if (n.compareTo(ZERO) <= 0)  return false;
        if (n.equals(ONE))           return false;
        if (n.equals(TWO))           return true;
        if (n.equals(THREE))         return true;
        if (n.mod(TWO).equals(ZERO)) return false;

        // Increase ITERATIONS to decrease error rate
        for (int i=0; i<ITERATIONS; i++) {
            BigInteger a;

            // Generate random integer in (1, n-1)
            do {
                a = new BigInteger(n.bitLength(), rand);
            } while (a.compareTo(ONE) <= 0 || a.compareTo(n.subtract(ONE)) >= 0);

            // Check if it's a witness
            if (isWitness(a, n)) return false; // Definitely composite
        }

        return true; // Pribably prime
    }

    /**
     * Get next probably prime >= n.
     */
    public static BigInteger nextProbablePrime(BigInteger n) {
        while (!isProbablePrime(n)) n = n.add(ONE);

        return n;
    }

    /**
     * Get n-bit random probable prime.
     */
    public static BigInteger probablePrime(int numBits) {
        BigInteger x;

        do {
            x = new BigInteger(numBits, rand);
        } while (!isProbablePrime(x));

        return x;
    }
}

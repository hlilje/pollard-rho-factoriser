import java.math.BigInteger;

/*
 * Class for performing Miller-Rabin's primality test.
 */
public class MillerRabin {

    public static final BigInteger ZERO = BigInteger.ZERO;
    public static final BigInteger ONE  = BigInteger.ONE;
    public static final BigInteger TWO  = BigInteger.valueOf(2);
    // Values for bases
    public static final int[] aValues   = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};

    /*
     * Performs a primality test on the given number.
     */
    public static boolean isPrime(BigInteger n, BigInteger a, BigInteger d, int s) {
        // Write n - 1 as 2^s * d with d odd by factoring powers of 2 from n - 1
        for (int i=0; i<s; ++i) {
            BigInteger exp = TWO.pow(i);
            exp = exp.multiply(d);

            // TODO Implement this myself
            BigInteger res = a.modPow(exp, n);

            if (res.equals(n.subtract(ONE)) || res.equals(ONE)) return true;
        }

        return false;
    }

    /*
     * Performs the Miller-Rabin algorithm to decide if the given
     * number is prime.
     * Returns true if the number probably is prime.
     * n is the number to test, k is the number of bases to test.
     */
    public static boolean millerRabin(BigInteger n, int k) {
        BigInteger d = n.subtract(ONE);
        int s = 0;

        while (d.mod(TWO).equals(ZERO)) {
            ++s;
            d = d.divide(TWO);
        }

        System.out.print("Base ");

        // Loops through all the bases and exits early if it is composite
        for (int i=0; i<k; ++i) {
            BigInteger a = BigInteger.valueOf(aValues[i]);
            boolean r = isPrime(n, a, d, s);
            System.out.print(aValues[i] + " ");

            if (!r) return false;
        }

        return true;
    }
}

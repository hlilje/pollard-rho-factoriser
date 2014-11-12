import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Implementation of the quadratic sieve algorithm for factoring
 * composite numbers into prime factors.
 */
public class QuadraticSieve {

    private static final BigInteger ZERO        = BigInteger.ZERO;
    private static final BigInteger ONE         = BigInteger.ONE;
    private static final BigInteger TWO         = BigInteger.valueOf(2);
    private static final BigInteger THREE       = BigInteger.valueOf(3);

    private static int B; // Smoothness bound
    private static int K;

    private static ArrayList<BigInteger> primes = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> roots  = new ArrayList<BigInteger>();


    /**
     * L function for quadratic sieve.
     */
    private static double L(BigInteger n) {
        // e^(sqrt(ln(n) * ln(ln(n))))
        return Math.exp(Math.sqrt(Maths.ln(n) * Math.log(Maths.ln(n))));
    }

    /**
     * Finds a root +-a_i with (a_i)^2 cong n (mod p_i).
     */
    private static BigInteger findRoot(BigInteger n) {
        return ONE;
    }

    /**
     * Initialises the quadratic sieve algorithm.
     */
    private static void initialise(BigInteger n) {
        // B could be tuned after taste instead
        // L(n)^(1/2) rounded up
        BigInteger B = BigInteger.valueOf((int) Math.ceil(Math.sqrt(L(n))));
        primes.add(TWO); // p_1 = 2
        roots.add(ONE); // a_1 = 1

        // Find all odd primes <= B for which n/p == 1 holds and label them
        for (BigInteger p = THREE; p.compareTo(B) <= 0; p = p.add(ONE)) {
            if(MillerRabin.isProbablePrime(p) && (n.divide(p) == ONE)) {
                primes.add(p);
            }
        }

        K = primes.size();
        for (BigInteger a = TWO; a.compareTo(BigInteger.valueOf(K)) <= 0; a = a.add(ONE)) {
            roots.add(a);
        }
    }

    /**
     * Finds one factor of the given number using the quadratic sieve
     * algorithm.
     */
    public static BigInteger quadraticSieve(BigInteger n) {
        // Return if divisible by 2
        if (n.mod(TWO).equals(ZERO)) return TWO;

        initialise(n);

        return ONE;
    }
}

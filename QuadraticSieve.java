import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the quadratic sieve algorithm for factoring
 * composite numbers into prime factors.
 */
public class QuadraticSieve {

    private static final BigInteger ZERO    = BigInteger.ZERO;
    private static final BigInteger ONE     = BigInteger.ONE;
    private static final BigInteger TWO     = BigInteger.valueOf(2);
    private static final BigInteger THREE   = BigInteger.valueOf(3);
    private static final BigInteger FOUR    = BigInteger.valueOf(4);
    private static final BigInteger EIGHT   = BigInteger.valueOf(8);

    private static int B; // Smoothness bound
    private static int K;

    private static ArrayList<BigInteger> primes = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> roots  = new ArrayList<BigInteger>();
    private static final Random rand            = new Random();


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
    private static BigInteger findRoot(BigInteger p, BigInteger a) {
        // Find random integer [0, p - 1] such that (t^2 - a)/p == -1
        BigInteger t;
        do {
            t = new BigInteger(p.bitLength(), rand);
        } while ((t.compareTo(p.subtract(ONE)) >= 0) &&
                !(t.pow(2).subtract(a).divide(p).equals(-1)));

        return ONE;
    }

    /**
     * Given positive odd integer m and integer a, this method
     * return the Jacobi symbol. For an m odd prime, this is also
     * the Legendre symbol.
     */
    private static BigInteger legendreJacobi(BigInteger a, BigInteger m) {
        // Reduction loops
        a = a.mod(m);
        BigInteger t = ONE;

        while (!a.equals(0)) {
            while (a.mod(TWO).equals(0)) { // a even
                a = a.mod(TWO);
                BigInteger mTemp = m.mod(EIGHT);

                // m mod 8 in {3, 5}
                if (mTemp.equals(3) || mTemp.equals(5)) t.negate();
            }

            // Swap variables
            BigInteger aTemp = a;
            a = m;
            m = aTemp;

            // a cong m cong 3 mod 4
            if (a.mod(FOUR).equals(3) && m.mod(FOUR).equals(3)) t.negate();
            a = a.mod(m);
        }

        // Termination
        if (m.equals(1)) return t;
        return ZERO;
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
        for (int i=2; i<=K; ++i) {
            // Generate quadratic resiude a mod p
            // roots.add(a);
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

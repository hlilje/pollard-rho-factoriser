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
    private static ArrayList<BigInteger> qs     = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> bs     = new ArrayList<BigInteger>();
    private static final Random rand            = new Random();


    /**
     * L function for quadratic sieve.
     */
    private static double L(BigInteger n) {
        // e^(sqrt(ln(n) * ln(ln(n))))
        return Math.exp(Math.sqrt(Maths.ln(n) * Math.log(Maths.ln(n))));
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
     * Finds a root +-a_i with (a_i)^2 cong n (mod p_i).
     */
    private static BigInteger findRoot(BigInteger a, BigInteger p) {
        // Find random integer [0, p - 1] such that Legendre(t^2 - a, p) == -1
        BigInteger t;
        do {
            t = new BigInteger(p.bitLength(), rand);
        } while ((t.compareTo(p.subtract(ONE)) >= 0) &&
                !legendreJacobi(t.pow(2).subtract(a), p).equals(-1));

        // Find a square root in F
        // x = (t + sqrt(t^2 - a))^((p + 1) / 2)
        BigInteger x = Maths.sqrt(t.pow(2).subtract(a)).add(t)
            .pow(p.add(ONE).divide(TWO).intValue()); // pow must be int

        return x;
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

        // Find all odd primes <= B for which Legendre == 1 and label them
        for (BigInteger p = THREE; p.compareTo(B) <= 0; p = p.add(ONE)) {
            if(MillerRabin.isProbablePrime(p) && legendreJacobi(n, p).equals(1)) {
                primes.add(p);
            }
        }

        K = primes.size();
        for (int i=1; i<K; ++i) { // 0-indexed 2 to K inclusive
            // Generate quadratic residue a mod p
            roots.add(findRoot(n, primes.get(i)));
        }
    }

    /**
     * Sieves the sequence for smooth B-values.
     */
    private static BigInteger sieve(BigInteger n) {
        // TODO What should these come from and be?
        BigInteger T = ONE, L = ONE, R = ONE, B = ONE;

        // TODO Are the indices 0-indexed in this pseudocode!?
        // Initialise the offsets
        // Go from p_1 = 2 the final prime
        for (int i=1; i<K; ++i) {
            BigInteger p = primes.get(i);
            BigInteger q = L.add(ONE).add(p).divide(TWO).negate().mod(p);
            qs.add(q);
        }

        // Process blocks
        while (T.compareTo(R) < 0) {
            for (int j=0; BigInteger.valueOf(j).compareTo(B) < 0; ++j) {
                BigInteger b = ONE;
                bs.add(b);
            }

            for (int k=1; k<K; ++k) { // k = 2 to pi(P)
                for (BigInteger j=qs.get(k); j.compareTo(B) < 0; j = j.add(primes.get(k)))
                    bs.add(j.intValue(), ZERO); // TODO Index off by one?
                BigInteger q = qs.get(k);
                BigInteger p = primes.get(k);
                q.subtract(B).mod(p);
                qs.add(k, q);
            }

            // j in [0, B-1]
            for (BigInteger j = ZERO; j.compareTo(B) < 0; j = j.add(ONE)) {
                BigInteger b = bs.get(j.intValue());
                // Output the prime = T + 2j + 1
                if (b.equals(1)) return T.add(j.multiply(TWO)).add(ONE);
            }

            T = T.add(B.multiply(TWO));
        }

        return T; // TODO No return value here in algorithm
    }

    /**
     * Finds one factor of the given number using the quadratic sieve
     * algorithm.
     */
    public static BigInteger quadraticSieve(BigInteger n) {
        // Return if divisible by 2
        if (n.mod(TWO).equals(0)) return TWO;

        initialise(n);
        sieve(n);

        return ONE;
    }
}

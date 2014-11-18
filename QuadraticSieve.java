import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the quadratic sieve algorithm for factoring
 * composite numbers into prime factors.
 */
public class QuadraticSieve {

    private static final boolean DEBUG = true;

    private static final BigInteger ZERO  = BigInteger.ZERO;
    private static final BigInteger ONE   = BigInteger.ONE;
    private static final BigInteger TWO   = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR  = BigInteger.valueOf(4);
    private static final BigInteger FIVE  = BigInteger.valueOf(5);
    private static final BigInteger EIGHT = BigInteger.valueOf(8);

    private static BigInteger B; // Smoothness bound
    private static int K;

    private static ArrayList<BigInteger> primes      = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> roots       = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> qs          = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> bs          = new ArrayList<BigInteger>();
    private static ArrayList<BigInteger> bSmoothNums = new ArrayList<BigInteger>();

    private static ArrayList<ArrayList<BigInteger>> factorTable = new ArrayList<ArrayList<BigInteger>>();

    private static final Random rand = new Random();

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
    private static BigInteger jacobi(BigInteger a, BigInteger m) {
        if (DEBUG) System.out.println("jacobi a, m: " + a + ", " + m);

        if (m.compareTo(ZERO) <= 0 || m.mod(TWO).equals(ZERO)) return ZERO;

        // Reduction loops
        // TODO Used in book
        // a = a.mod(m);
        BigInteger t = ONE;

        if (a.compareTo(ZERO) < 0) {
            a = a.negate();
            if (m.mod(FOUR).equals(THREE)) t = t.negate();
        }

        while (!a.equals(ZERO)) {
            // TODO Should this be skipped for a == 2 or a == 0?
            // while (a.mod(TWO).equals(ZERO)) { // a even
            while (a.mod(TWO).equals(ZERO) && !a.equals(TWO) && !a.equals(ZERO)) { // a even
                a = a.mod(TWO);
                BigInteger mTemp = m.mod(EIGHT);

                // m mod 8 in {3, 5}
                if (mTemp.equals(THREE) || mTemp.equals(FIVE)) t = t.negate();
            }

            // Swap variables
            BigInteger aTemp = a;
            a = m;
            m = aTemp;

            // a cong m cong 3 mod 4
            if (a.mod(FOUR).equals(THREE) && m.mod(FOUR).equals(THREE)) t = t.negate();
            a = a.mod(m);
        }

        // Termination
        if (m.equals(ONE)) return t;
        return ZERO;
    }

    /**
     * Finds a root +-a_i with (a_i)^2 cong n (mod p_i) given an
     * odd prime p and a quadratic residue a mod p.
     */
    private static BigInteger findRoot(BigInteger a, BigInteger p) {
        if (DEBUG) System.out.println("findRoot a, p: " + a + ", " + p);

        // Find random integer [0, p - 1] such that Jacobi(t^2 - a, p) == -1
        BigInteger t;
        do {
            t = new BigInteger(p.bitLength(), rand);
            if (DEBUG) System.out.println("found t: " + t);
        } while ((t.compareTo(p.subtract(ONE)) >= 0) ||
                !jacobi(t.pow(2).subtract(a), p).equals(ONE.negate()));

        // Find a square root in F
        // x = (t + sqrt(t^2 - a))^((p + 1) / 2)
        if (DEBUG) System.out.println("findRoot a: " + a);
        BigInteger x = t.pow(2).subtract(a);
        if (x.compareTo(ZERO) < 0) x = x.negate(); // TODO Is this allowed?
        x = Maths.sqrt(x).add(t).pow(p.add(ONE).divide(TWO).intValue()); // pow must be int
        return x;
    }

    /**
     * Initialises the quadratic sieve algorithm.
     */
    private static void initialise(BigInteger n) {
        // B could be tuned after taste instead
        // L(n)^(1/2) rounded up
        B = BigInteger.valueOf((int) Math.ceil(Math.sqrt(L(n))));
        if (DEBUG) System.out.println("initialise B: " + B);
        primes.add(TWO); // p_1 = 2
        roots.add(ONE); // a_1 = 1

        // Find all odd primes <= B for which Jacobi == 1 and label them
        for (BigInteger p = THREE; p.compareTo(B) <= 0; p = p.add(ONE)) {
            if(MillerRabin.isProbablePrime(p) && jacobi(n, p).equals(ONE)) {
                primes.add(p);
                if (DEBUG) System.out.println("Add prime p: " + p);
            }
        }

        K = primes.size();
        // TODO Correct table size?
        factorTable = new ArrayList<ArrayList<BigInteger>>(K);
        for (int i=1; i<K; ++i) { // 0-indexed 2 to K inclusive
            // Generate quadratic residue a mod p
            roots.add(findRoot(n, primes.get(i)));
        }
    }

    /**
     * Sieves the sequence for smooth B-values.
     * Finds primes in the interval (L, R).
     * L and R should be even and satisfy R > L, B | R - L, L > P = sqrt(R).
     */
    private static void sieve(BigInteger n, BigInteger L, BigInteger R) {
        BigInteger T;

        // Initialise the offsets
        // Go from p_1 = 2 the final prime
        for (int k=2; k<K; ++k) {
            BigInteger p = primes.get(k);
            BigInteger q = L.add(ONE).add(p).divide(TWO).negate().mod(p);
            qs.add(q);
        }

        // Process blocks
        T = L;
        while (T.compareTo(R) < 0) {
            // Initialise with 1s
            // for (int j=0; BigInteger.valueOf(j).compareTo(B) < 0; ++j) {
            //     BigInteger b = ONE;
            //     bs.add(b);
            // }

            // TODO Should the list of factors be initialised as b?
            for (int k=2; k<K; ++k) { // k = 2 to pi(P)
                for (BigInteger j=qs.get(k); j.compareTo(B) < 0; j = j.add(primes.get(k)))
                    bs.add(j.intValue(), ZERO); // TODO Index off by one?
                BigInteger q = qs.get(k);
                BigInteger p = primes.get(k);
                q.subtract(B).mod(p);
                qs.add(k, q);
            }

            // j in [0, B-1]
            for (BigInteger j = ZERO; j.compareTo(B) < 0; j = j.add(ONE)) {
                // BigInteger b = bs.get(j.intValue());
                // Output the prime = T + 2j + 1
                BigInteger p = T.add(j.multiply(TWO)).add(ONE);

                // Save factor if it's the first or if it's smaller than the current factor
                // if (b.equals(ONE)) bs.add(j.intValue(), p);
                // else if (p.compareTo(b) < 0) bs.add(j.intValue(), p);

                // Add to lists p, p2, ..., p * floor(n/p)
                BigInteger limit = n.divide(p).multiply(p);
                BigInteger k = p.subtract(ONE); // 0-indexed
                BigInteger mult = ONE;
                while (k.compareTo(limit) < 0) {
                    ArrayList<BigInteger> ft = factorTable.get(k.intValue());
                    ft.add(p);
                    k = k.multiply(mult);
                    mult = mult.add(ONE);
                }

                // Add to lists p^1, p^2, ..., n
                limit = n;
                k = p.subtract(ONE);
                int a = 1;
                while (k.compareTo(limit) < 0) {
                    ArrayList<BigInteger> ft = factorTable.get(k.intValue());
                    ft.add(p);
                    k = k.pow(a);
                    // Do not sieve with p^a > B
                    if (k.compareTo(B) > 0) break;
                    ++a;
                }
            }

            // Pick out the B-smooth numbers
            for (BigInteger j = ZERO; j.compareTo(B) < 0; j = j.add(ONE)) {
                ArrayList<BigInteger> ft = factorTable.get(j.intValue());

                BigInteger prod = ONE;
                for (int k=0; k<ft.size(); ++k) {
                    prod = prod.multiply(ft.get(k));
                }

                // If the product of factors equals the number it is B-smooth
                if (prod.equals(j)) bSmoothNums.add(j);
            }

            T = T.add(B.multiply(TWO));
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
        if (DEBUG) System.out.println("Initialisation done");
        // TODO Give proper arguments
        // sieve(n, ONE, ONE);
        if (DEBUG) System.out.println("Sieving done");

        // Reset arrays of values
        primes = new ArrayList<BigInteger>();
        roots = new ArrayList<BigInteger>();
        qs = new ArrayList<BigInteger>();
        bs = new ArrayList<BigInteger>();
        bSmoothNums = new ArrayList<BigInteger>();

        if (DEBUG) System.out.println("Quadratic sieve done");
        return ONE;
    }
}

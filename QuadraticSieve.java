import java.math.BigInteger;

/**
 * Implementation of the quadratic sieve algorithm for factoring
 * composite numbers into prime factors.
 */
public class QuadraticSieve {

    private static final BigInteger ZERO  = BigInteger.ZERO;
    private static final BigInteger ONE   = BigInteger.ONE;
    private static final BigInteger TWO   = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    private static int B; // Smoothness bound

    /**
     * L function for quadratic sieve.
     */
    private static double L(BigInteger n) {
        // e^(sqrt(ln(n) * ln(ln(n))))
        return Math.exp(Math.sqrt(Maths.ln(n) * Math.log(Maths.ln(n))));
    }

    /**
     * Finds one factor of the given number using the quadratic sieve
     * algorithm.
     */
    public static BigInteger quadraticSieve(BigInteger n) {
        // Return if divisible by 2
        if (n.mod(TWO).equals(ZERO)) return TWO;

        // B could be tuned after taste instead
        // L(n)^(1/2) rounded up
        BigInteger B = BigInteger.valueOf((int) Math.ceil(Math.sqrt(L(n))));
        BigInteger p1, a;

        // Find all odd primes <= B for which n/p == 1 holds
        for (BigInteger p = THREE; p.compareTo(B) <= 9; p = p.add(ONE)) {
            if(MillerRabin.isProbablePrime(p) && (n.divide(p) == ONE)) {
                // TODO
            }
        }

        return ONE;
    }
}

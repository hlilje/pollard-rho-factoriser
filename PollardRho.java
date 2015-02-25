import java.math.BigInteger;
import java.util.Random;

/**
 * Class for performing Pollarad's Rho factorisation.
 */
public class PollardRho {

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE  = BigInteger.ONE;
    private static final BigInteger TWO  = BigInteger.valueOf(2);

    private static final Random rand = new Random();

    /**
     * g(x) in Pollard rho, a congruence PRNG based on the given numbers.
     */
    private static BigInteger g(BigInteger x, BigInteger n, BigInteger c) {
        return x.multiply(x).mod(n).add(c); // (x^2 mod n) + c
    }

    /**
     * Finds one factor of the given number.
     * n is the number to factorise.
     */
    public static BigInteger pollardRho(BigInteger n) {
        // Randomise initial values for x's and c
        BigInteger x = new BigInteger(n.bitLength(), rand);
        BigInteger x2 = x;
        BigInteger c = new BigInteger(n.bitLength(), rand);
        BigInteger d; // Divisor

        // Return if divisible by 2
        if (n.mod(TWO).equals(ZERO)) return TWO;

        do {
            // Quit early if too much time has been spent on the number
            if (System.nanoTime() - Factor.prevTime > Factor.timeLimit) {
                System.out.println("====== TIME LIMIT EXCEEDED ======");
                return ONE;
            }

            // Find x(i+1) and x(2*(i+1)) and increment running values
            x = g(x, n, c).mod(n); // g(x) mod n
            x2 = g(g(x2, n, c).mod(n), n, c).mod(n); // g(g(x2)) mod n
            d = Maths.gcd(x.subtract(x2), n); // gcd(x-x2, n)
            // Loop while gcd == 1, otherwise n is composite and a factor is found
        } while (d.equals(ONE));

        return d;
    }
}

import java.math.BigInteger;

/*
 * Class for performing modular exponentiation.
 */
public class ModExp {

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE  = BigInteger.ONE;
    private static final BigInteger TWO  = BigInteger.valueOf(2);

    /*
     * Performs modular exponentiation on the given number as
     * per Applied Cryptography.
     */
    public static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger mod) {
        BigInteger res = ONE;

        while (exp.compareTo(ZERO) > 0) {
            // Check if exponent is odd
            if (exp.testBit(0))
                res = (res.multiply(base)).mod(mod); // (res * base) % mod

            exp = exp.shiftRight(1);
            base = (base.multiply(base)).mod(mod); // (base * base) % mod
        }

        return res.mod(mod);

    }

    /*
     * Recursive variant on modular exponentiation.
     */
    public static BigInteger modPowVariant(BigInteger a, BigInteger b, BigInteger n) {
        // Return a^b mod n
        if (b.equals(ZERO)) return ONE;

        BigInteger t = modPow(a, b.divide(TWO), n);
        BigInteger c = (t.multiply(t)).mod(n);

        if (b.mod(TWO).equals(ONE)) c = (c.multiply(a)).mod(n);
        return c;
    }
}

import java.math.BigInteger;

/**
 * Class with generic mathematical utility functions.
 */
public class Maths {

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE  = BigInteger.ONE;
    private static final BigInteger TWO  = BigInteger.valueOf(2);

    /**
     * Returns the greatest common divisor of the given numbers.
     */
    public static BigInteger gcd(BigInteger a, BigInteger b) {

        // Loop until either is 0
        while (!a.equals(ZERO) && !b.equals(ZERO)) {
            BigInteger c = b;
            b = a.mod(b);
            a = c;
        }

        return a.add(b); // Return the non-zero value
    }

    /**
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

    /**
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

    /**
     * Computes the natural logarithm of a BigInteger. Works for really big
     * integers (practically unlimited).
     * Takes a positive number as argument.
     */
    public static double ln(BigInteger val) {
        double LOG2 = Math.log(2.0d);
        int blex = val.bitLength() - 1022; // Any value in 60..1023 is ok

        if (blex > 0) val = val.shiftRight(blex);
        double res = Math.log(val.doubleValue());

        return blex > 0 ? res + blex * LOG2 : res;
    }

    /**
     * Computes the square root of the given (positive) number.
     */
    public static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
        BigInteger div2 = div;

        // Loop until the same value is hit twice in a row, or
        // alternate.
        for(;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
    }
}

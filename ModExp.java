import java.math.BigInteger;

/*
 * Class for performing modular exponentiation.
 */
public class ModExp {

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE  = BigInteger.ONE;

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
    return res .mod(mod);
    }
}

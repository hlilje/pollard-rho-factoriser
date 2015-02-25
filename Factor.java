import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for factoring numbers using various algorithms.
 */
public class Factor {

    private static final boolean DEBUG = true;

    // The number to factorise, PP * 10^60 + i
    private static final BigInteger SSN  = new BigInteger("1000000000");
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE  = BigInteger.ONE;

    private static final int J_FACTOR = 0; // 'j' value to use when factoring
    private static int NUM_NUMBERS    = 100; // The number of number to factorise
    private static int I_START        = 1; // The number to start factorising from

    public static final long timeLimit = 86400000000000L; // Max 24 hrs factoring time per number
    // public static final long timeLimit = 43200000000000L; // Max 12 hrs factoring time per number
    // public static final long timeLimit = 21600000000000L; // Max 6 hrs factoring time per number
    // public static final long timeLimit = 5000000000L; // Max 5 s factoring time per number
    public static long startTime, prevTime;

    private static TreeMap<BigInteger, Integer> factors = new TreeMap<BigInteger, Integer>();

    /**
     * Returns SSN * 10^(60 + j) + i.
     */
    public static BigInteger getSSN(int i, int j) {
        return SSN.multiply(BigInteger.valueOf(10).pow(60 + j))
                .add(BigInteger.valueOf(i));
    }

    /**
     * Factorises the given number and prints the results.
     */
    public static void factor(BigInteger n) {
        // Quit early if too much time has been spent on the number
        if (System.nanoTime() - prevTime > timeLimit) {
            System.out.println("====== TIME LIMIT EXCEEDED ======");
            return;
        }

        if (n.equals(ONE)) return;

        if (MillerRabin.isProbablePrime(n)) {
            // if (DEBUG) {
            //     long elapsedTime = System.nanoTime() - prevTime;
            //     System.out.println("(" + Time.formatNanos(elapsedTime) + ")\t" + n);
            // }

            // Increment the factor count
            if (factors.containsKey(n)) {
                int count = factors.get(n);
                factors.put(n, ++count);
            } else
                factors.put(n, 1);

            if(DEBUG) System.out.println("= " + n); // Print for redundancy
            return;
        }

        BigInteger divisor = PollardRho.pollardRho(n);
        // BigInteger divisor = QuadraticSieve.quadraticSieve(n);
        factor(divisor);
        factor(n.divide(divisor));
    }

    /**
     * Prints the map of factors and their frequencies in ascending order.
     */
    private static void printFactors() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<BigInteger, Integer> entry : factors.entrySet()) {
            BigInteger key = entry.getKey();
            Integer value = entry.getValue();

            // Print factor followed by number of occurrences as
            // per assignment
            sb.append(key + " " + value + " ");
        }

        sb.setLength(Math.max(sb.length() - 1, 0)); // Remove trailing space
        System.out.println("======== ALL FACTORS ========");
        System.out.println(sb);

        if (DEBUG) {
            long elapsedTime = System.nanoTime() - prevTime;
            System.out.println("=== Factored in: " + Time.formatNanos(elapsedTime));
        }
    }

    public static void main(String[] args) {
        if (DEBUG) System.out.println("================ PROGRAM START ================");
        startTime = System.nanoTime();
        prevTime = startTime; // Initially same time as first
        BigInteger n;
        int j = J_FACTOR;

        System.out.println(SSN + " " + j); // As per assignment

        if (args.length == 1) {
            n = new BigInteger(args[0]);
            factor(n);
            printFactors();
        } else if(args.length == 2) {
            // Read which range of numbers should be factored
            I_START = Integer.parseInt(args[0]);
            NUM_NUMBERS = Integer.parseInt(args[1]);

            // Factor all the numbers
            for (int i=I_START; i<=NUM_NUMBERS; ++i) {
                n = getSSN(i, j);
                if(DEBUG) System.out.println("=== Now factoring: " + n + " (i = " + i + ")");
                factor(n);
                printFactors();

                factors = new TreeMap<BigInteger, Integer>(); // Remove previous factors
                prevTime = System.nanoTime(); // Reset time for next number
                if(DEBUG) System.out.println("================================");
            }
        } else {
            System.err.println("Missing arguments.");
        }

        if (DEBUG) System.out.println("======= Total execution time: " + Time.formatNanos(System.nanoTime() - startTime) + " =======");
        if (DEBUG) System.out.println("================= PROGRAM END =================");
    }
}

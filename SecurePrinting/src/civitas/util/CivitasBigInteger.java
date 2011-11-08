/*
 * This file is part of the Civitas software distribution.
 * Copyright (c) 2007-2008, Civitas project group, Cornell University.
 * See the LICENSE file accompanying this distribution for further license
 * and copyright information.
 */ 
package civitas.util;

import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class CivitasBigInteger implements Serializable {
    /** did we load the native lib correctly? */
    private static boolean _nativeOk = false;
    private static final boolean USE_NATIVE = true;
    private static final boolean DEBUG = false;
    
    private static long numModPows = 0;
    public static long numModPows() {
        return numModPows;
    }

    static {
        _nativeOk = USE_NATIVE && loadLibrary();
    }

    private final BigInteger i;

    public static final CivitasBigInteger ZERO = CivitasBigInteger.valueOf(0);
    public static final CivitasBigInteger ONE = CivitasBigInteger.valueOf(1);
    public static final CivitasBigInteger TWO = CivitasBigInteger.valueOf(2);

    public CivitasBigInteger(byte[] bytes) {
        i = new BigInteger(bytes);
    }
    public CivitasBigInteger(int signum, byte[] magnitude) {
        i = new BigInteger(signum, magnitude);
    }

    /** Construct a probable prime. */
    public CivitasBigInteger(int length, int certainty, Random random) {
        i = new BigInteger(length, certainty, random);
    }
    
    /** Construct a random integer. */
    public CivitasBigInteger(int i, Random random) {
        this.i = new BigInteger(i, random);
    }
    private CivitasBigInteger(BigInteger integer) {
        this.i = integer;
    }
    public static CivitasBigInteger valueOf(int i) {
        return new CivitasBigInteger(BigInteger.valueOf(i));
    }

    /**
     * calculate (base ^ exponent) % modulus.
     * 
     * @param base
     *            big-endian twos complement representation of
     *            the base (but it must be positive)
     * @param exponent
     *            big-endian twos complement representation of the exponent
     * @param modulus
     *            big-endian twos complement representation of the modulus
     * @return big endian twos complement representation of
     *         (base ^ exponent) % modulus
     */
    public native static byte[] nativeModPow(byte base[], byte exponent[],
             byte modulus[]);

    /**
     * Calculate (x * y) % modulus.
     * 
     * @param x
     *            big endian twos complement representation of the operand 
     * @param y
     *            big endian twos complement representation of the operand 
     * @param modulus
     *            big endian twos complement representation of the modulus
     * @return big endian twos complement representation of (x * y) % modulus
     */
    public native static byte[] nativeModMultiply(byte x[], byte y[],
                                                  byte modulus[]);

    /**
     * Calculate (x * y^(-1)) % modulus.
     * 
     * @param x
     *            big endian twos complement representation of the operand 
     * @param y
     *            big endian twos complement representation of the operand 
     * @param modulus
     *            big endian twos complement representation of the modulus
     * @return big endian twos complement representation of (x * y^(-1)) % modulus
     */
    public native static byte[] nativeModDivide(byte x[], byte y[],
                                                  byte modulus[]);

    public int bitLength() {
        return i.bitLength();
    }
    public CivitasBigInteger add(CivitasBigInteger x) {
        if (x == ZERO) return this;
        return new CivitasBigInteger(i.add(x.i));
    }
    public CivitasBigInteger modAdd(CivitasBigInteger x, CivitasBigInteger p) {
        if (x == ZERO) return this.mod(p);
        return new CivitasBigInteger(i.add(x.i).mod(p.i));
    }
    public int intValue() {
        return i.intValue();
    }
    public int compareTo(CivitasBigInteger n) {
        return i.compareTo(n.i);
    }
    public CivitasBigInteger modPow(CivitasBigInteger x, CivitasBigInteger p) {
        numModPows++; // record the number of modPows called.
        if (_nativeOk)
            return new CivitasBigInteger(nativeModPow(toByteArray(), x.toByteArray(), p.toByteArray()));
        else
            return new CivitasBigInteger(this.i.modPow(x.i, p.i));
    }
    public boolean isProbablePrime(int certainty) {
        return i.isProbablePrime(certainty);
    }
	public CivitasBigInteger nextProbablePrime() {
		return new CivitasBigInteger(i.nextProbablePrime());
	}
    public CivitasBigInteger mod(CivitasBigInteger q) {
        BigInteger m = this.i.mod(q.i);
        if (this.i == m) return this;
        return new CivitasBigInteger(m);
    }
    public CivitasBigInteger multiply(CivitasBigInteger x) {
        if (x == ONE) return this;
        if (x == ZERO) return ZERO;
        return new CivitasBigInteger(this.i.multiply(x.i));
    }
	public CivitasBigInteger divide(CivitasBigInteger q) {
		return new CivitasBigInteger(this.i.divide(q.i));
	}
    public CivitasBigInteger modMultiply(CivitasBigInteger x, CivitasBigInteger p) {
	if (x == ZERO) return ZERO;
	if (x == ONE) return this.mod(p);
	if (_nativeOk)
	    return new CivitasBigInteger(nativeModMultiply(toByteArray(),
				      x.toByteArray(), p.toByteArray()));
	else
	    return new CivitasBigInteger(this.i.multiply(x.i).mod(p.i));
    }

    public CivitasBigInteger modDivide(CivitasBigInteger x, CivitasBigInteger p) {
        if (x == ONE) return this.mod(p);
        if (_nativeOk)
            return new CivitasBigInteger(nativeModDivide(toByteArray(),
				      x.toByteArray(), p.toByteArray()));
        else
            return new CivitasBigInteger(this.i.multiply(x.i.modInverse(p.i)).mod(p.i));
    }

    public CivitasBigInteger modInverse(CivitasBigInteger p) {
        return new CivitasBigInteger(this.i.modInverse(p.i));
    }
    public CivitasBigInteger modNegate(CivitasBigInteger p) {
        return new CivitasBigInteger(this.i.negate().mod(p.i));
    }
    public CivitasBigInteger subtract(CivitasBigInteger x) {
        if (x == ZERO) return this;
        return new CivitasBigInteger(this.i.subtract(x.i));
    }
    public CivitasBigInteger modSubtract(CivitasBigInteger x, CivitasBigInteger p) {
        if (x == ZERO) return this.mod(p);
        return new CivitasBigInteger(this.i.subtract(x.i).mod(p.i));
    }
    
	public CivitasBigInteger pow(int j) {
		return new CivitasBigInteger(this.i.pow(j));
	} 
    
    public String toString() {
        return i.toString();
    }
    public boolean equals(Object o) {
        if (o instanceof CivitasBigInteger) {
            return this.i.equals(((CivitasBigInteger)o).i);
        }
        return false;
    }
    public int hashCode() {
        return i.hashCode();
    }
    /*
     * Cache the byte array. Rely on no-one changing it. 
     */
    private byte[] byteArray = null;
    public byte[] toByteArray() {
        if (byteArray == null) {
            byteArray = i.toByteArray();
        }
        return byteArray;
    }

    public static void main(String[] args) {
        // test performance of BigInteger versus CivitasBigInteger
        // parse arguments
        // usage: [-v] [numModPows [numMults [numDivs]]]
        
        int[] numTests = new int[] {100, 10000, 10000};
        boolean verbose = true;
        int baseSize = 1024;
        int expSize = 1024;
        int modSize = 1024;

        int numTestIndex = 0;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            try {
                if (args[argIndex].startsWith("-q")) {
                    verbose = false;
                }
                else if (args[argIndex].startsWith("-h") || args[argIndex].startsWith("--help")) {
                    usage(System.out);
                    System.exit(1);
                }
                else if (args[argIndex].startsWith("-base")) {
                    baseSize = Integer.parseInt(args[++argIndex]);
                }
                else if (args[argIndex].startsWith("-mod")) {
                    modSize = Integer.parseInt(args[++argIndex]);
                }
                else if (args[argIndex].startsWith("-exp")) {
                    expSize = Integer.parseInt(args[++argIndex]);
                }
                else {
                    int n = Integer.parseInt(args[argIndex]);
                    numTests[numTestIndex++] = n;
                }
            }
            catch (NumberFormatException e) {
                usage(System.err);
                System.exit(1);
            }
            catch (IndexOutOfBoundsException e) {
                usage(System.err);
                System.exit(1);
            }
        }
                        
        System.out.println("civitas.util.CivitasBigInteger performance tests\n");
        if (numTests[0] > 0) runModPowTest(numTests[0], baseSize, modSize, expSize, verbose, false);
        if (numTests[1] > 0) runArithTest(numTests[1], baseSize, modSize, false, verbose);
        if (numTests[2] > 0) runArithTest(numTests[2], baseSize, modSize, true, verbose);

    }
    private static void usage(PrintStream out) {
        out.println("usage: CivitasBigInteger [OPTION] [numModPows [numMults [numDivs]]]");        
        out.println("Run performance test using the Civitas Big Integer representation.");        
        out.println("This is useful to determine if the native library is accessible, and the performance improvement it provides");        
        out.println("Options:");        
        out.println("   -h , --help  display this message");        
        out.println("   -q           be quieter");        
        out.println("   -base n      length in bits of the base (for modExp, multiply and divide tests)");        
        out.println("   -mod n       length in bits of the modulus (for modExp, multiply and divide tests)");        
        out.println("   -exp n       length in bits of the exponent (for modExp tests)");        
    }
    private static void runModPowTest(int numRuns, int baseSize, int modSize, int expSize, boolean verbose, boolean tabularOutput) {
        System.out.println("Testing modPow for CivitasBigInteger versus java.math.BigInteger");
        if (!_nativeOk && !tabularOutput) {            
            System.out.println("WARNING: could not load native library, so falling back on java.math.BigInteger");
        }
        SecureRandom rand = new SecureRandom();
        rand.nextBoolean();

        BigInteger jp = new BigInteger(modSize, 10, rand);
        CivitasBigInteger p = new CivitasBigInteger(jp);

        BigInteger jg = new BigInteger(baseSize, 10, rand);
        CivitasBigInteger g = new CivitasBigInteger(jg);

        long totalTime = 0;
        long javaTime = 0;

        int runsProcessed = 0;
        for (runsProcessed = 0; runsProcessed < numRuns; runsProcessed++) {
            BigInteger bi = new BigInteger(expSize, rand); 
            CivitasBigInteger k = new CivitasBigInteger(1, bi.toByteArray());
            long beforeModPow = System.currentTimeMillis();
            CivitasBigInteger myValue = g.modPow(k, p);
            long afterModPow = System.currentTimeMillis();
            BigInteger jval = jg.modPow(bi, jp);
            long afterJavaModPow = System.currentTimeMillis();

            totalTime += (afterModPow - beforeModPow);
            javaTime += (afterJavaModPow - afterModPow);
            if (!myValue.i.equals(jval)) {
                if (!tabularOutput) {
                    System.err.println("   ERROR: [" + runsProcessed + "]\tCivitas modPow != java modPow");
                    System.err.println("   ERROR: Civitas modPow value: " + myValue.toString());
                    System.err.println("   ERROR: java modPow value: " + jval.toString());
                    System.err.println("   ERROR: run time: " + totalTime + "ms (" + (totalTime / (runsProcessed + 1)) + "ms each)");
                }
                break;
            } else {
              if (!tabularOutput && verbose && runsProcessed % 10 == 0) System.out.print('.');
            }
        }
        if (verbose) System.out.println();
        
        if (verbose && !tabularOutput) {
            if (numRuns == runsProcessed) {
                System.out.println("   INFO: Testing g^k mod p");
                System.out.println("   INFO: " + runsProcessed + " runs complete without any errors: g.modPow(k, p)");
                System.out.println("   INFO:         base size (g) = " + baseSize + " bits");
                System.out.println("   INFO:          mod size (p) = " + modSize + " bits");
                System.out.println("   INFO:     exponent size (k) = " + expSize + " bits");
            }
            else
                System.out.println("   ERROR: " + runsProcessed + " runs until we got an error");

            System.out.println("Civitas run time: \t" + totalTime + "ms (" + (totalTime / (runsProcessed + 1))
                               + "ms each)");
            System.out.println("Java run time: \t" + javaTime + "ms (" + (javaTime / (runsProcessed + 1)) + "ms each)");
            System.out.println("Civitas = " + ((totalTime * 100.0d) / (double) javaTime) + "% of pure java time");
            System.out.println();
        }
        else if (tabularOutput) {
            System.out.println(baseSize + "\t" + modSize + "\t" + expSize + "\t" + totalTime + "\t" + (totalTime / (runsProcessed + 1)) + "\t" + 
                                                javaTime + "\t" + (javaTime / (runsProcessed + 1)));
        }
        else {
            System.out.println(totalTime + "ms (" + (totalTime / (runsProcessed + 1)) + "ms each) vs " + javaTime + "ms (" + (javaTime / (runsProcessed + 1)) + "ms each)  = " + ((totalTime * 100.0d) / (double) javaTime) + " %");
            System.out.println();
        }
    }
    private static void runArithTest(int numRuns, int baseSize, int modSize, boolean testDiv, boolean verbose) {
        System.out.println("Testing mod " + (testDiv?"divide":"multiply") +" for CivitasBigInteger versus java.math.BigInteger");
        if (!_nativeOk) {
            System.out.println("WARNING: could not load native library, so falling back on java.math.BigInteger");
        }
        SecureRandom rand = new SecureRandom();
        rand.nextBoolean();
        BigInteger jp = new BigInteger(modSize, 10, rand);
        CivitasBigInteger p = new CivitasBigInteger(jp);

        long totalTime = 0;
        long javaTime = 0;

        int runsProcessed = 0;
        for (runsProcessed = 0; runsProcessed < numRuns; runsProcessed++) {
            BigInteger jx = new BigInteger(baseSize, rand); 
            CivitasBigInteger x = new CivitasBigInteger(1, jx.toByteArray());
            BigInteger jy = new BigInteger(baseSize, rand); 
            CivitasBigInteger y = new CivitasBigInteger(1, jy.toByteArray());
            long beforeMult, afterMult, afterJavaMult;
            CivitasBigInteger myValue;
            BigInteger jval;
            if (testDiv) {
                beforeMult = System.currentTimeMillis();
                myValue = x.modDivide(y, p);
                afterMult = System.currentTimeMillis();
                jval = jx.multiply(jy.modInverse(jp)).mod(jp);
                afterJavaMult = System.currentTimeMillis();
            }
            else {
                beforeMult = System.currentTimeMillis();
                myValue = x.modMultiply(y, p);
                afterMult = System.currentTimeMillis();
                jval = jx.multiply(jy).mod(jp);
                afterJavaMult = System.currentTimeMillis();                
            }

            totalTime += (afterMult - beforeMult);
            javaTime += (afterJavaMult - afterMult);
            if (!myValue.i.equals(jval)) {
                System.err.println("   ERROR: [" + runsProcessed + "]\tCivitas value != java value");
                System.err.println("   ERROR: Civitas value value: " + myValue.toString());
                System.err.println("   ERROR: java value value: " + jval.toString());
                break;
            } else {
                if (verbose && runsProcessed % 1000 == 0) System.out.print('.');
            }
        }
        if (verbose) {
            System.out.println();
            if (numRuns == runsProcessed) {
                System.out.println("   INFO: Testing (x " + (testDiv?"/":"*") + " y) mod p");
                System.out.println("   INFO: " + runsProcessed + " runs complete without any errors");
                System.out.println("   INFO:         base size (x,y) = " + baseSize + " bits");
                System.out.println("   INFO:          mod size (p) = " + modSize + " bits");
            }
            else
                System.out.println("   ERROR: " + runsProcessed + " runs until we got an error");

            System.out.println("Civitas run time: \t" + totalTime + "ms (" + (totalTime / (runsProcessed + 1))
                               + "ms each)");
            System.out.println("Java run time: \t" + javaTime + "ms (" + (javaTime / (runsProcessed + 1)) + "ms each)");
            System.out.println("Civitas = " + ((totalTime * 100.0d) / (double) javaTime) + "% of pure java time");
        }
        else {
            System.out.println(totalTime + "ms (" + (totalTime / (runsProcessed + 1)) + "ms each) vs " + javaTime + "ms (" + (javaTime / (runsProcessed + 1)) + "ms each)  = " + ((totalTime * 100.0d) / (double) javaTime) + " %");
        }
        System.out.println();
    }

    private static final boolean loadLibrary() {
        try {
            System.loadLibrary("civitasbigint");
            return true;
        } catch (UnsatisfiedLinkError ule) {
            // failed to load the library
            if (DEBUG) System.err.println("civitas.util.CivitasBigInteger: failed to load library. Falling back on java.math.BigInteger");          
            return false;
        }
    }


   
}

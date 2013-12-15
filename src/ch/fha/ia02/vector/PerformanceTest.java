package ch.fha.ia02.vector;

import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 * Tests used vector methods in perfomance.
 * 
 * @author Lukas Kellenberger
 * @author Thomas Gerstendoerfer
 */
public class PerformanceTest {

	/**
	 * Entry point from the command line.
	 */
	public static void main (String args[]) {
		try {
			long start = System.currentTimeMillis();
    		runTest(50, 2000, 500, (args.length > 0) ? args[0] : null);
    		long runtime = System.currentTimeMillis() - start;
    		System.err.println("Total run time: " + ((runtime/100)/10.0+0.5) + " seconds.");
    	}
    	catch (IOException e) {
    		System.err.println(e.getMessage());
    	}
    }


	/**
	 * Runs test for performance. 
	 *
	 * @param m Number of runs.
	 * @param n Number of runs of each math operation.
	 * @param len Number of test vectors per operation run.
	 * @param outfile name of the file to write the collected data to.
	 */
	public static void runTest(int m, int n, int len, String outfile)
		throws IOException
	{
		PrintStream out = null;
		if (outfile != null) {
			out = new PrintStream(
				new BufferedOutputStream(
					new FileOutputStream(outfile)));
			System.err.println("Collected performance data will be written to "
				+ outfile + "...");
		}
		runTest(m, n, len, out);
		if (out != null) {
			out.flush();
			out.close();
		}
	}
    
    
    /**
     * Runs test for performance. 
     *
     * @param m Number of runs
     * @param n Number of runs of each math operation
     * @param len Number of test vectors per operation run
     * @param out stream to write the performance data to
     */
    public static void runTest(int m, int n, int len, PrintStream out) {
    
    	Vector3f[] u1 = new Vector3f[len];
		Vector3f[] u2 = new Vector3f[len];

		float value;
		long start;
		float[] vals = new float[len];
		long[] t = new long[17];

		System.err.println("Testing " + t.length + " methods "
			+ (m*n*len) + " times...");
	
		//Repead a duration
		for(int run=0; run<m; run++) {
			System.err.print(".");

			for(int method=0; method<t.length; method++) { 
				Vector3f v = new Vector3f();
				Random r = new Random(2132);
				for (int i=0; i<vals.length; i++) {
					vals[i] = r.nextFloat()*10 - 5;
				}
				fillRandom(u1);
				fillRandom(u2);
				start = System.nanoTime();
				for(int i=0; i<n; i++) {
					for(int j=0; j<len; j++) {
						switch(method) {
							case  0:	u1[j].add(u2[j]);	break;
							case  1:	v.add(u1[j], u2[j]);	break;
							case  2:	u1[j].sub(u2[j]);	break;
							case  3:	v.sub(u1[j], u2[j]);	break;
							case  4:	u1[j].angle(u2[j]);	break;
							case  5:	u1[j].length();	break;
							case  6:	u1[j].lengthSquared();	break;
							case  7:	u1[j].cross(u1[j], u2[j]);	break;
							case  8:	u1[j].dot(u2[j]);	break;
							case  9:	u1[j].scale(vals[j]);	break;
							case 10:	v.scale(vals[j], u2[j]);	break;
							case 11:	u1[j].normalize();	break;
							case 12:	v.normalize(u2[j]);	break;
							case 13:	u1[j].set(u2[j]);	break;
							case 14:	
										u1[j].set(u2[j]);
										u1[j].add(u2[j]);	
										break;
										
							case 15:	Math.abs(vals[j]);	break;
							case 16:	u1[j].add(u2[j]);	break;
							default: throw new RuntimeException();
						}
					}
				}
				t[method] = System.nanoTime()-start;
			}
			if (out != null) {
				for(int i=0; i<t.length; i++) {
					out.print(t[i] + ", ");
				}
				out.println();
			}
		}
		System.err.println(); // finish ticks-line

		System.err.println("Test: "+m);
		System.err.println("Testsequenz: "+n);
		System.err.println("Vektor-Array Laenge: "+len);
		System.err.println("Time for          u1.add(u2): " + t[0]);
		System.err.println("Time for      u1.add(u1, u2): " + t[1]);
		System.err.println("Time for          u1.sub(u2): " + t[2]);
		System.err.println("Time for      u1.sub(u1, u2): " + t[3]);
		System.err.println("Time for        u1.angle(u2): " + t[4]);
		System.err.println("Time for         u1.length(): " + t[5]);
		System.err.println("Time for        u1.lengthS(): " + t[6]);
		System.err.println("Time for    u1.cross(u1, u2): " + t[7]);
		System.err.println("Time for          u1.dot(u2): " + t[8]);
		System.err.println("Time for     u1.scale(float): " + t[9]);
		System.err.println("Time for u1.scale(float, u2): " + t[10]);
		System.err.println("Time for      u1.normalize(): " + t[11]);
		System.err.println("Time for    u1.normalize(u2): " + t[12]);
		System.err.println("Time for          u1.set(u2): " + t[13]);
		System.err.println("Time for u1.st(u2) u1.ad(u2): " + t[14]);
		System.err.println("Time for          abs(float): " + t[15]);
		System.err.println("Time for difference: " + (t[16]-t[0]));
	}
	
	private static void fillRandom(Vector3f[] va) {
		Random r = new Random(2001);
		for (int i=0; i<va.length; i++) {
			if(va[i] == null) {
				va[i] = new Vector3f();
			}
			va[i].set(r.nextFloat(), r.nextFloat(), r.nextFloat());
		}
	}
}

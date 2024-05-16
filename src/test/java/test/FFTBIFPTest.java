package test;

import java.math.BigInteger;

import io.nh.U;
import io.nh.kzg.FFTBIFPUtil;

public class FFTBIFPTest {

	public static void main(String[] args)
	{
		try {
			int []d = {1, 85, 148, 111, 336, 252, 189, 226};
			BigInteger []domain = _create_bi_a_(d);
			BigInteger mod = new BigInteger("337");
			int []x = {3,1,4,1,5,9,2,6};
			BigInteger []X = _create_bi_a_(x);
			
			for (int i = 0; i < X.length; i++) {
				U.pf("X     [%d] [%s]\n", i, X[i]);
			}
			// values should be [31, 70, 109, 74, 334, 181, 232, 4]
			BigInteger []values = FFTBIFPUtil.FFT(X, domain, mod);
			
			for (int i = 0; i < values.length; i++) {
				U.pf("values [%d] invFFT [%s]\n", i, values[i]);
			}			
			BigInteger []shouldBeX  = FFTBIFPUtil.inverseFFT(values, domain, mod);
			// should be X again. [3, 1, 4, 1, 5, 9, 2, 6]
			for (int i = 0; i < shouldBeX.length; i++) {
				U.pf("rez    [%d] X[%s] final [%s]\n", i, X[i], shouldBeX[i]);
			}					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * 
	 * @param d
	 * @return
	 */
	private static BigInteger[] _create_bi_a_(int[] inA)
	{
		BigInteger []rval = new BigInteger[inA.length];
		for (int i = 0; i < inA.length; i++) {
			rval[i] = new BigInteger(Integer.toString(inA[i]));
		}
		return rval;
	}

}

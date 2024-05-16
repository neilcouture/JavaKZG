package io.nh.fft;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import io.nh.U;

public class FFTUtil {

    public static int nextPowerOf2(final int a)
    {
        int b = 1;
        while (b < a)
        {
            b = b << 1;
        }
        return b;
    }
	/**
	 * 
	 * @param inp
	 * @param inq
	 * @return
	 * @throws Exception
	 */
	public static int[] polymul(
			int []inp,
			int []inq) throws Exception
	{
		int lp = inp.length;
		int lq = inq.length;
		
		int clen;
		if (lp != lq) {
			int lpp = lp + lq - 1;
			int lqp = lq + lp - 1;
			clen = Math.max(lpp,  lqp);
		} else {
			clen = lp + lq - 1;
		}
		clen = nextPowerOf2(clen);
		
		double []ppaded = new double[clen];
		double []qpaded = new double[clen];
		
		for (int i = 0; i < lp; i++) {
			ppaded[i] = (double)inp[i];
		}
		for (int i = 0; i < lq; i++) {
			qpaded[i] = (double)inq[i];
		}		
		
		FastFourierTransformer fftt = new FastFourierTransformer(
				DftNormalization.STANDARD);
		Complex[] cp = fftt.transform(ppaded, TransformType.FORWARD);
		Complex[] cq = fftt.transform(qpaded, TransformType.FORWARD);
		
		if (cp.length != cq.length) {
			String msg = U.f("cp.length [%d] != cq.length [%d]", cp.length, cq.length);
			throw new Exception(msg);
		}
		
		Complex[] tmpr = new Complex[cp.length];
		for (int i = 0; i < cp.length; i++) {
			tmpr[i] = cp[i].multiply(cq[i]);
		}
		
		Complex []r = fftt.transform(tmpr, TransformType.INVERSE);
				
		int []rval = new int[r.length];
		
		for (int i = 0; i < r.length; i++) {
			double tr = r[i].getReal();
			rval[i] = (int)tr;
		}
		return rval;			
	}
	public static String spoly(int []inP)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstDone = false;
		for (int i = inP.length - 1; i >= 0; i--) {
			int f = inP[i];
			if (f != 0) {
				if (f < 0) {
					
				} else {
					if (firstDone) {
						sb.append(" + ");
					}
				}
				firstDone = true;
				if (i > 1) { 
					sb.append(f).append("x^").append(i).append(" ");
				} else {
					if (i == 1)
						sb.append(f).append("x").append(" ");
					else 
						sb.append(f);
				}
			}
		}
		return sb.toString();
	}	
	/**
	 * 
	 * @param inA
	 */
	public static void main(String []inA)
	{
		try {
			toto0();
			//toto1();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * 
	 * 
	 * 	// 5x^4+4x^3+3x^2+2x+1
	 * https://www.emathhelp.net/en/calculators/algebra-1/multiplying-polynomials-calculator/
	 * 
	 * @throws Exception
	 */
	private static void toto0() throws Exception
	{
		int []p = {1,2,3,4,5};
		int []q = {1,2,3,4,-5};
		
		String ps = spoly(p);
		String qs = spoly(q);
		
		U.pf("%s\n", ps);
		U.pf("%s\n", qs);
		
		int []r = polymul(p, q);
		
		String rs = spoly(r);
		
		U.pf("%s\n", rs);
	}
}

package io.nh.kzg;

import java.math.BigInteger;

import io.nh.U;

public class FFTBIFPUtil {
	
	/**
	 * 
	 * @param a
	 * @return
	 */
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
     * @param n
     * @return
     */
    private final
    static boolean _is_p2_(int n)
    {
        if (n == 0)
            return false;
 
        while (n != 1) {
            if (n % 2 != 0)
                return false;
            n = n / 2;
        }
        return true;
    }
	/**
	 * 
	 * @param inp
	 * @param inq
	 * @return
	 * @throws Exception
	 */
	public static BigInteger[] FFT(
			BigInteger []inVals,
			BigInteger []inDomain,
			BigInteger inMod) throws Exception
	{
		if (!_is_p2_(inVals.length)) {
			throw new Exception("not a power of 2");
		}
		if (inVals.length != inDomain.length) {
			throw new Exception("domain.length != value.length");
		}
		
		BigInteger []rval = fft(inVals, inDomain, inMod);
		return rval;
		
	}
  
	/**
	 * 
	 * 
	 * 
	 * @param inp
	 * @param inq
	 * @return
	 * @throws Exception
	 */
	public static BigInteger[] inverseFFT(
			BigInteger []inVals,
			BigInteger []inDomain,
			BigInteger inMod) throws Exception
	{
		BigInteger []tmp = FFT(inVals, inDomain, inMod);
		
		BigInteger []rval = new BigInteger[tmp.length];
		rval[0] = tmp[0];
		int k = 1;
		for (int i = rval.length - 1; i >= 1; i--) {
			rval[k] = tmp[i];
			k++;
		}
		// modular inverse
		BigInteger mi = _get_mi_(inVals.length, inMod);
		
		for (int i = 0; i < rval.length; i++) {
			
			BigInteger toInverse = rval[i];
			BigInteger modularInverse = toInverse.multiply(mi).mod(inMod);
			rval[i] = modularInverse;
		}
		return rval;
		
		// FIXME:
		// write inverse fft

// fft-FP.ipynb
		
//		def inverse_fft(vals, modulus, domain):
//		    vals = fft(vals, modulus, domain)
//		    
//		    print(vals)
//		    tmp = [x * modular_inverse(len(vals), modulus) % modulus for x in [vals[0]] + vals[1:][::-1]]
//		           
//		    return  tmp		
	}
    /**
     * 				
     * 				x ^(n-2) mod n
     * 
     * @param inX
     * @param inMod
     * @return
     * @throws Exception
     */
	private static BigInteger _get_mi_(int inX, BigInteger inMod) throws Exception
	{
		BigInteger mm2 = inMod.subtract(BigInteger.TWO);
		BigInteger x = new BigInteger(Integer.toString(inX));
		
		BigInteger rval = x.modPow(mm2, inMod);
				
		return rval;
	}
	/**
	 * 
	 * @param inp
	 * @param inq
	 * @return
	 * @throws Exception
	 */
	private static BigInteger[] fft(
			BigInteger []inVals,
			BigInteger []inDomain,
			BigInteger inMod) throws Exception
	{
		if (inVals.length == 1) {
			return inVals;
		}
		BigInteger []left = fft(_skip_(0,2,inVals), _skip_(0,2,inDomain), inMod);
		BigInteger []right = fft(_skip_(1,2,inVals), _skip_(0,2,inDomain), inMod);
		BigInteger []rval = new BigInteger[inVals.length];
		
		int lenleft = left.length;
		for (int i = 0; i < left.length; i++) {
			BigInteger x = left[i];
			BigInteger y = right[i];
			
			BigInteger y_x_root = y.multiply(inDomain[i]);
			rval[i] = x.add(y_x_root).mod(inMod);
			rval[i + lenleft] = x.subtract(y_x_root).mod(inMod);		
		}
		return rval;
//	    if len(vals) == 1:
//	        return vals
//	    L = fft(vals[::2], modulus, domain[::2])
//	    R = fft(vals[1::2], modulus, domain[::2])
//	    o = [0 for i in vals]
//	    for i, (x, y) in enumerate(zip(L, R)):
//	        y_times_root = y*domain[i]
//	        o[i] = (x+y_times_root) % modulus
//	        o[i+len(L)] = (x-y_times_root) % modulus
//	    return o
		
	}
	/**
	 * 
	 * @param inB
	 * @param inSkip
	 * @param inA
	 * @return
	 */
	final
	private static BigInteger[] _skip_(int inB, int inSkip, BigInteger[] inA)
	{
		BigInteger []rval = new BigInteger[inA.length / 2];
		int k = 0;
		for (int i = inB; i < inA.length; i += inSkip) {
			rval[k] = new BigInteger(inA[i].toString());
			k++;
		}
		return rval;
	}
	/**
	 * Get String for Polynomial
	 * 
	 * @param inP
	 * @return
	 */
	public static String spoly(BigInteger []inP)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstDone = false;
		for (int i = inP.length - 1; i >= 0; i--) {
			BigInteger f = inP[i];
			if (f.compareTo(BigInteger.ZERO) == 0) {
				if (f.compareTo(BigInteger.ZERO) == -1) {
					
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
	 * @param ins
	 * @return
	 */
	private final static BigInteger _bi_(String ins) {
		BigInteger rval = new BigInteger(ins);
		return rval;
	}
	/**
	 * 
	 * @param inA
	 */
	public static void main(String []inA)
	{
		try {
			//fft([3,1,4,1,5,9,2,6], 337, [1, 85, 148, 111, 336, 252, 189, 226])
			BigInteger []values = {
					_bi_("3"),
					_bi_("1"),
					_bi_("4"),
					_bi_("1"),
					_bi_("5"),
					_bi_("9"),
					_bi_("2"),
					_bi_("6")
			};
			BigInteger []domain = {
					_bi_("1"),
					_bi_("85"),
					_bi_("148"),
					_bi_("111"),
					_bi_("336"),
					_bi_("252"),
					_bi_("189"),
					_bi_("226")
			};
			BigInteger mod = new BigInteger("337");
			
			BigInteger []res = FFT(values, domain, mod);
			
			for (BigInteger r : res) {
				System.out.print(r);
				System.out.print(",");
			}
			System.out.println("\nfinished");

			U.pf("Now interpolate with ifft: should be:\n[46L, 169L, 29L, 149L, 126L, 262L, 140L, 93L]\n");
			res = inverseFFT(values, domain, mod);

			for (BigInteger r : res) {
				System.out.print(r);
				System.out.print(",");
			}
			System.out.println("\nfinished");


						
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	/**
	 * 
	 * @param inp
	 * @param inq
	 * @return
	 * @throws Exception
	 */
	public static BigInteger[] polymul(
			BigInteger []inp,
			BigInteger []inq) throws Exception
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
		
		BigInteger []ppaded = new BigInteger[clen];
		BigInteger []qpaded = new BigInteger[clen];
		
		for (int i = 0; i < lp; i++) {
			ppaded[i] = inp[i];
		}
		for (int i = 0; i < lq; i++) {
			qpaded[i] = inq[i];
		}	
		for (int i = 0; i < clen; i++) {
			if (ppaded[i] == null) {
				ppaded[i] = new BigInteger("0");
			}
			if (qpaded[i] == null) {
				qpaded[i] = new BigInteger("0");
			}
		}
		BigInteger []domain = BLS381BIRootUnity.computeRootOfUnity(clen);//, inG, inMod).; // FIXME: BLS12381 domain based on clen
		BigInteger mod = BLS381BIRootUnity.MODULO;		// FIXME: BLS12381 domain based on clen and mod
				
		BigInteger[] cp = FFT(ppaded, domain, mod);
		BigInteger[] cq = FFT(qpaded, domain, mod);
	
		if (cp.length != cq.length) {
			String msg = U.f("cp.length [%d] != cq.length [%d]", cp.length, cq.length);
			throw new Exception(msg);
		}		
		BigInteger[] tmpr = new BigInteger[cp.length];
		for (int i = 0; i < cp.length; i++) {
			tmpr[i] = cp[i].multiply(cq[i]).mod(mod);
		}
		BigInteger []rval = inverseFFT(tmpr, domain, mod);
		return rval;			
	}
	
}

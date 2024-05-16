package io.nh.kzg;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import io.nh.U;
/**
 * 
 * Polynomial over finite field backed by BigInteger
 * 
 * @author neil
 *
 */
public class LagrangeInterpolationBIFP {
	
	private static final Logger sLog = Logger.getLogger(LagrangeInterpolationBIFP.class);
	
	private boolean mVerbose = false;
	private int mMinDegreeForFFT = 0; //1000 * 1000;
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			_should_be_4116_();
			_should_be_x2_();
			_should_be_100_100_();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @throws Exception
	 */
	private static void _should_be_100_100_() throws Exception
	{
		// > share.lagrange_interp(map(e, [1, 3, 2, 1]), map(e, [1, 2, 3, 4]))
		// [-7.0, 12.000000000000002, -4.5, 0.4999999999999999]
		BigInteger mod = new BigInteger(Integer.toString(251));
		int []x = {1,2,3};
		int []y = {200,500,1000};
		
		LagrangeInterpolationBIFP li = new LagrangeInterpolationBIFP(mod, false);
		li.setMinDegreeFFT(0);
		BigInteger []r = li.interpolate(x, y, mod, true);
		
		PolynomialBIFP rp = PolynomialBIFP.createFrom(r, mod);
		U.pf("res [%s]\n", rp.toString());
	} 
	/**
	 * https://blog.ethereum.org/2014/08/16/secret-sharing-erasure-coding-guide-aspiring-dropbox-decentralizer/
	 * 
	 * 4116 = 6x^3 + 1x^2 + 1x + 4
	 * @throws Exception
	 */
	private static void _should_be_4116_() throws Exception
	{
		// > share.lagrange_interp(map(e, [1, 3, 2, 1]), map(e, [1, 2, 3, 4]))
		// [-7.0, 12.000000000000002, -4.5, 0.4999999999999999]
		BigInteger mod = new BigInteger(Integer.toString(11));
		int []x = {1,2,3,4};
		int []y = {1,3,2,1};
		
		LagrangeInterpolationBIFP li = new LagrangeInterpolationBIFP(mod, false);
		li.setMinDegreeFFT(0);
		BigInteger []r = li.interpolate(x, y, mod, true);
		
		PolynomialBIFP rp = PolynomialBIFP.createFrom(r, mod);
		U.pf("res [%s]\n", rp.toString());
	} 

	/**
	 * https://www.dcode.fr/lagrange-interpolating-polynomial
	 * @throws Exception
	 */
	private static void _should_be_x2_() throws Exception
	{
		int []x = {0,2,4};
		int []y = {0,4,16};
		//int mod = 37;
		BigInteger mod = new BigInteger(Integer.toString(37));
		LagrangeInterpolationBIFP li = new LagrangeInterpolationBIFP(mod, false);
		li.setMinDegreeFFT(0);
		BigInteger []r = li.interpolate(x, y, mod, true);
		
		PolynomialBIFP rp = PolynomialBIFP.createFrom(r, mod);
		U.pf("res [%s]\n", rp.toString());
	} 
	/**
	 * 
	 * @param inMin
	 */
	public void setMinDegreeFFT(int inMin)
	{
		mMinDegreeForFFT = inMin;
	}
	/**
	 * 
	 */
	public LagrangeInterpolationBIFP(BigInteger inMod)
	{
		this(inMod, false);
	}
	/**
	 * 
	 * @param inV
	 */
	public LagrangeInterpolationBIFP(BigInteger inMod, boolean inV)
	{
		mVerbose = inV;
	}
	/**
	 * 
	 * @param inx
	 * @param iny
	 * @param inMod
	 * @return
	 */
	public BigInteger[] interpolate(
			int []inx, 
			int []iny, 
			BigInteger inMod,
			boolean inRemainderCheck) throws Exception
	{
		BigInteger []x = new BigInteger[inx.length];
		BigInteger []y = new BigInteger[iny.length];

		
		for (int i = 0; i < inx.length; i++) {
			x[i] = BIGUtil.createBI(Integer.toString(inx[i]), inMod);
		}
		for (int i = 0; i < iny.length; i++) {
			y[i] = BIGUtil.createBI(Integer.toString(iny[i]), inMod);
		}
		return interpolate(x, y, inMod, inRemainderCheck);
	}
	/**
	 * Lagrange interpolation with FFT
	 * 
	 * https://www.dcode.fr/lagrange-interpolating-polynomial
	 * @param inx
	 * @param iny
	 * @return
	 */
	public BigInteger[] interpolate(
			BigInteger []inx, 
			BigInteger []iny, 
			BigInteger inMod,
			boolean inRemainderCheck) throws Exception
	{
		PolynomialBIFP acc = null;
		
		for (int j = 0; j < iny.length; j++) {
			
			BigInteger y = iny[j];
			
			BigInteger []tmp = {y};
					
			if (BigInteger.ZERO.compareTo(y) != 0) {
				PolynomialBIFP yp = PolynomialBIFP.createFrom(tmp, inMod);
				PolynomialBIFP tmpy = null;
				if (mVerbose) {
					U.pf("%f X [\n", y);
				}
				for (int i = 0; i < inx.length; i++) {
					if (i != j) {
						BigInteger xi = inx[i];
						BigInteger xj = inx[j];
						if (mVerbose) {
							U.pf("\t\t(x - %f) / (%f - %f) * \n", 
								xi, xj, xi);
						}					
						BigInteger []tmpnum = new BigInteger[2]; // {-xi, 1};
						
						BigInteger tmp2 = inMod.subtract(xi);
						
						BIFP tmp3 = new BIFP(tmp2.toString(), inMod);
						
						tmpnum[0] = tmp3; //inMod.subtract(xi);
						tmpnum[1] = new BIFP(BigInteger.ONE.toString(), inMod);
						
						BigInteger []tmpden = new BigInteger[1]; //{(xj - xi)};
						
						BigInteger tmp4 = xj.subtract(xi).mod(inMod);
						tmpden[0] = new BIFP(tmp4.toString(), inMod);
						
						PolynomialBIFP num = PolynomialBIFP.createFrom(tmpnum, inMod);
						PolynomialBIFP den = PolynomialBIFP.createFrom(tmpden, inMod);
						PolynomialBIFP []pji = num.divides(den);
						
						if (mVerbose) {
							String _t1 = num.toString();
							String _t2 = den.toString();
							String _t3 = pji[0].toString();						
							U.pf("\t\tpji [%s] / [%s] = [%s] \n", _t1, _t2, _t3);
						}
						// check no remainder:
						if (inRemainderCheck) {
							BigInteger []remainC = pji[1].coefs();
							for (int ii = 0; ii < remainC.length; ii++) {
								//if (remainC[ii] != 0) {
								if (BigInteger.ZERO.compareTo(remainC[ii]) != 0) {
									String m = U.f("remaider[%d] degree wrong:%s \n", ii, remainC[ii].toString());
									throw new Exception(m);
								}
							}	
						}
						if (tmpy == null) {
							tmpy = pji[0];
						} else {
							if (mVerbose) {								
								String t1 = tmpy.toString();
								String t2 = pji[0].toString();
								U.pf("\t\ttmpy [%s] * [%s] \n", t1, t2);
							}
							// FIXME: add FFT code
							int degreetmpy = tmpy.degree();
							if (degreetmpy < this.mMinDegreeForFFT) {
								tmpy = tmpy.times(pji[0]);
							} else {
								//U.pf("LagrangeInterpolationBIFP using FFT\n");
								// use FFT
								BigInteger []pc = tmpy.coefs();
								BigInteger []qc = pji[0].coefs();
								
								if (sLog.isDebugEnabled()) {
									String msg = U.f("fft pc.len:%d qc.len:%d", pc.length, qc.length);
									sLog.debug(msg);
								}								
								BigInteger []res = FFTBIFPUtil.polymul(pc, qc);
								
								tmpy = PolynomialBIFP.createFrom(res, inMod);							
							}
						}	
					}
				}
				if (mVerbose) {
					String t1 = yp.toString();
					String t2 = tmpy.toString();
					U.pf("\typ [%s] times [%s] \n", t1, t2);
				}
				yp = yp.times(tmpy);
				
				if (acc != null) {
					if (mVerbose) {
						String t11 = acc.toString();
						String t22 = yp.toString();
						U.pf("\tacc [%s] + yp [%s] \n", t11, t22);
					}
					acc = acc.plus(yp);
				} else {
					acc = yp;
				}
				//U.pf("] + \n");
			}
		}
		if (mVerbose) {
			String rs = acc.toString();
			U.pf("LI res [%s]\n", rs);
		}
		BigInteger rval[] = acc.coefs();
		return rval;
	}
}

package io.nh.poly;

import java.math.BigInteger;

import io.nh.U;
import io.nh.fft.FFTUtilD;
/*
* Example: Passing in three points (2,3) (1,4) and (3,7) will produce
* the results [2.5, -8.5, 10] which means that the points is on the
* curve y = 2.5x² - 8.5x + 10.
*/
public class LagrangeInterpolation {
	private boolean mVerbose = false;
	private int mMinDegreeForFFT = 4;
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			_should_be_x2_();
			_should_be_7_12_m45_05_();
			
			_should_be_100_100_();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	private static void _should_be_100_100_() throws Exception
	{
		BigInteger mod = new BigInteger(Integer.toString(251));
		double []x = {1,2,3};
		double []y = {200,500,1000};
		
		LagrangeInterpolation li = new LagrangeInterpolation(false);
		li.setMinDegreeFFT(0);
		double []r = li.interpolate(x, y);
		
		PolynomialD rp = PolynomialD.createFrom(r);
		U.pf("res [%s]\n", rp.toString());
	}
	/**
	 * https://blog.ethereum.org/2014/08/16/secret-sharing-erasure-coding-guide-aspiring-dropbox-decentralizer/
	 * @throws Exception
	 */
	private static void _should_be_7_12_m45_05_() throws Exception
	{
		// > share.lagrange_interp([1.0, 3.0, 2.0, 1.0], [1.0, 2.0, 3.0, 4.0])
		// [-7.0, 12.000000000000002, -4.5, 0.4999999999999999]
		
		double []x = {1d,2d,3d,4d};
		double []y = {1d,3d,2d,1d};
		
		LagrangeInterpolation li = new LagrangeInterpolation(false);
		li.setMinDegreeFFT(0);
		double []r = li.interpolate(x, y);
		
		PolynomialD rp = PolynomialD.createFrom(r);
		U.pf("res [%s]\n", rp.toString());
	} 
	/**
	 * https://www.dcode.fr/lagrange-interpolating-polynomial
	 * @throws Exception
	 */
	private static void _should_be_x2_() throws Exception
	{
		double []x = {0,2,4};
		double []y = {0,4,16};
		
		LagrangeInterpolation li = new LagrangeInterpolation(false);
		li.setMinDegreeFFT(0);
		double []r = li.interpolate(x, y);
		
		PolynomialD rp = PolynomialD.createFrom(r);
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
	public LagrangeInterpolation()
	{
		this(false);
	}
	/**
	 * 
	 * @param inV
	 */
	public LagrangeInterpolation(boolean inV)
	{
		mVerbose = inV;
	}
	/**
	 * Lagrange interpolation with FFT
	 * @param inx
	 * @param iny
	 * @return
	 */
	public double[] interpolate(double []inx, double []iny) throws Exception
	{
		return interpolate(inx, iny, -1, false);
	}
	/**
	 * 
	 * @param inx
	 * @param iny
	 * @param mod
	 * @return
	 * @throws Exception
	 */ 
	public int[] interpolate(
			int []inx, 
			int []iny, 
			int inMod,
			boolean inRemainderCheck) throws Exception
	{
		double []x = new double[inx.length];
		double []y = new double[iny.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = (double)inx[i];
			y[i] = (double)iny[i];			
		}
		double []tmp = interpolate(x, y, inMod, true);
		int []rval = new int[tmp.length];
		for (int i = 0; i < x.length; i++) {
			rval[i] = (int)tmp[i];
		}
		return rval;
	}
	/**
	 * 
	 * @param inx
	 * @param iny
	 * @param mod
	 * @return
	 * @throws Exception
	 */
	public double[] interpolate(
			double []inx, 
			double []iny, 
			int mod) throws Exception
	{
		return interpolate(inx, iny, mod, false);
	}
	/**
	 * Lagrange interpolation with FFT
	 * 
	 * https://www.dcode.fr/lagrange-interpolating-polynomial
	 * @param inx
	 * @param iny
	 * @return
	 */
	public double[] interpolate(
			double []inx, 
			double []iny, 
			int inMod,
			boolean inRemainderCheck) throws Exception
	{
		PolynomialD acc = null;
		for (int j = 0; j < iny.length; j++) {
			
			double y = (double)iny[j];
			
			double []tmp = {y};
					
			if (y != 0) {
				PolynomialD yp = PolynomialD.createFrom(tmp);
				PolynomialD tmpy = null;
				if (mVerbose) {
					U.pf("%f X [\n", y);
				}
				for (int i = 0; i < inx.length; i++) {
					if (i != j) {
						double xi = inx[i];
						double xj = inx[j];
						if (mVerbose) {
							U.pf("\t\t(x - %f) / (%f - %f) * \n", 
								xi, xj, xi);
						}					
						double []tmpnum = {-xi, 1};
						double []tmpden = {(xj - xi)};
						
						PolynomialD num = PolynomialD.createFrom(tmpnum);
						PolynomialD den = PolynomialD.createFrom(tmpden);
						PolynomialD []pji = num.divides(den);
						
						if (mVerbose) {
							String _t1 = num.toString();
							String _t2 = den.toString();
							String _t3 = pji[0].toString();						
							U.pf("\t\tpji [%s] / [%s] = [%s] \n", _t1, _t2, _t3);
						}
						// check no remainder:
						if (inRemainderCheck) {
							double []remainC = pji[1].coefs();
							for (int ii = 0; ii < remainC.length; ii++) {
								if (remainC[ii] != 0) {
									String m = U.f("remaider[%d] degree wrong:%d \n", ii, remainC[ii]);
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
							int degreetmpy = tmpy.degree();
							if (degreetmpy < this.mMinDegreeForFFT) {
								tmpy = tmpy.times(pji[0]);
							} else {
								// use FFT
								double []pc = tmpy.coefs();
								double []qc = pji[0].coefs();
								double []res = FFTUtilD.polymul(pc, qc);
								
								tmpy = PolynomialD.createFrom(res);							
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
		double rval[] = acc.coefs();
		return rval;
	}
}

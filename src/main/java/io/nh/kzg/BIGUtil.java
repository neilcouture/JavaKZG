package io.nh.kzg;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ROM;

import io.nh.U;

public class BIGUtil {

	/**
	 * 
	 * @param inI
	 * @return
	 */
	public static BIG createBig(int inI)
	{
		BIG rval = null;
		
		if (inI >= 0) {
			rval = new BIG(inI);
		} else {
			BIG r =new BIG(ROM.CURVE_Order);
			BIG tmp = new BIG(-inI);
			rval = r.minus(tmp);
		}		
		return rval;
	}
	/**
	 * 
	 * @param inS
	 * @return
	 * @throws Exception
	 */
	public static BIG createBigFromString(String inS) throws Exception
	{
		return createBigFromString(inS, 1);
	}
	public final static BIG BIG_CURVE_ORDER = new BIG(ROM.CURVE_Order);
	public final static BigInteger BI_CURVE_ORDER = new BigInteger(BIG_CURVE_ORDER.toString(), 16);
	
	/**
	 * Crazy method that create a BIG from a String...
	 * 
	 * First we create a BigInteger and then use some magic to create
	 * a BIG. It use the base 16 representation of the BigInteger to reconstruct
	 * a genuine long array (6 longs) to recreated what is needed in the backend
	 * storage of a BIG.
	 * 
	 * @param inS
	 * @return
	 * @throws Exception if abs(inS) is a number larger than BLS12381.curveOrder
	 * 
	 */
	public static BIG createBigFromString(String inS, int inPow) throws Exception
	{
		BigInteger BI = new BigInteger(inS);
		String bi16 = BI.toString(16);
		
		if (inPow != 1) {
			BI = BI.pow(inPow);
		}
		String toParse = BI.toString();		
		bi16 = BI.toString(16);		
		
		boolean negative = false;
		if (bi16.startsWith("-")) {		
			bi16 = bi16.substring(1);
			negative = true;
		}
		int len = bi16.length();
		final int LONGLEN = 16;
		int k = len / LONGLEN;
		int modlonglen = len % LONGLEN;
		if (modlonglen > 0) {
			k++;
		}
		
		int end = len;
		// Byte buffer to store the BIG
		byte []tmp = new byte[6 * 8];
		ByteBuffer bbf = ByteBuffer.wrap(tmp);
		int vaI = 5;
		for (int i = k; i > 0; i--) {
			int start = end - LONGLEN;
			if (start < 0) {
				start = 0;
			}
			String sub = bi16.substring(start, end);	
			int subblen = sub.length();
			if (subblen == 16) {
				int dd = subblen / 2;
				int ddm = subblen % 2;
				if (ddm != 0) {
					dd += 1;
				}
				for (int j = 0; j < dd; j++) {
					int startj = j * 2;
					int endj = startj + 2;
					if (endj > subblen) {
						endj = subblen;
					}
					String ss = sub.substring(startj, endj);
					long v = Long.parseLong(ss, 16);
					byte newb = (byte)v;
					bbf.put(newb);
				}
			} else {
				long v = Long.parseLong(sub, 16);
				bbf.putLong(v);
			}
			end -= LONGLEN;
		}
		bbf.rewind();
		// to store our BIG
		long []vA = new long[6];
		vaI = 5;
		for (int i = 0; i < 6; i++) {
			long v = bbf.getLong();
			vA[vaI] = v;
			vaI--;
		}
		// reverse order...
		ByteBuffer bb = ByteBuffer.allocate(6 * 8);
		for (long v : vA) {
			bb.putLong(v);
		}
		byte []buf = bb.array();
		BIG rval = BIG.fromBytes(buf);
		
		String test = rval.toString();
		while (true) {
			char c1 = test.charAt(0);
			if (c1 == '0') {
				test = test.substring(1);
			} else {
				break;
			}
		}		
		
		if (test.equals(bi16)) {
			
		} else {
			U.pf("comparebbstr rval [%s]\n", test);
			U.pf("comparebbstr bi16 [%s]\n", bi16);
			String msg = U.f("Bad parse [%s]", toParse);
			throw new Exception(msg);
		}	
		if (BIG.comp(BIG_CURVE_ORDER, rval) < 0) {
			throw new Exception("number larger than BLS12381 curve order");
		}
		if (negative) {
			// if number was negative translate it curve order;
			rval = BIG_CURVE_ORDER.minus(rval);
		}		
		return rval;
	}
	/**
	 * 
	 * @param inN
	 * @param inMod
	 * @return
	 */
	public static final BigInteger createBI(String inN)
	{
		return createBI(inN, null);
	}
	/**
	 * 
	 * @param inN
	 * @param inMod
	 * @return
	 */
	public static final BigInteger createBI(String inN, BigInteger inMod)
	{
		
		BigInteger rval = null;
		if (inMod == null) {
			rval = new BigInteger(inN);
		} else {
			rval = new BIFP(inN, inMod);
		}
		
		return rval;
	}
	/**
	 * Create a BIG from BigInteger. If negative will return 
	 * BLS12381::CurveOrder - x.
	 * 
	 * If bigger than BLS12381::CurveOrder will throw an exception
	 * 
	 * @param inX
	 * @return
	 * @throws Exception
	 */
	public static final BIG createBIGWithCurveOrder(BigInteger inX) throws Exception
	{
		BIG rval = null;
		BigInteger babs = inX.abs();
		if (babs.compareTo(BIGUtil.BI_CURVE_ORDER) > 0) {
			throw new Exception("inX bigger than BLS12281.curveOrder");
		}
		if (inX.compareTo(BigInteger.ZERO) == 0) {
			rval = new BIG(0);
		} else if (inX.compareTo(BigInteger.ZERO) < 0) {
			BIG co = new BIG(ROM.CURVE_Order);
			BigInteger curveOrder = new BigInteger(co.toString(), 16);
			BigInteger minusX = curveOrder.add(inX);
			String bs = minusX.toString();
			rval = BIGUtil.createBigFromString(bs);
			//pi = tmp.minus(tmppi);
		} else {
			String bs = inX.toString();
			rval = BIGUtil.createBigFromString(bs);//, inPow); //new BIG(x);
		}
		return rval;
	}
}

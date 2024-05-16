package io.nh.kzg;

import io.nh.U;
import io.nh.kzg.blst.BLSTUtil;
import org.apache.log4j.Logger;
import org.apache.milagro.amcl.RAND;
import supranational.blst.P1;
import supranational.blst.P2;
import supranational.blst.Scalar;

import java.math.BigInteger;
import java.util.Random;

public class SRSBlst {

	static final Logger sLog = Logger.getLogger(SRSBlst.class);

	static final BigInteger CURVE_ORDER = new BigInteger("52435875175126190479447740508185965837690552500527637822603658699938581184513");
	
	public P1 []mSiG = null;
	public P2 []mSiH = null;
	
	/**
	 * 
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("SRS len:").append(mSiG.length).append("\ns^iGs:\n");
		for (P1 p : mSiG) {
			sb.append("  ").append(p.toString()).append("\n");
		}
		sb.append("s^iHs:\n");
		for (P2 p : mSiH) {
			sb.append("  ").append(p.toString()).append("\n");
		}	
		return sb.toString();
	}
	/**
	 * 
	 */
	public void openCommitment()
	{
		
	}


	/**
	 * inP is a Polynomial X
	 *  index i represent the factor f in f * x^i
	 *
	 *
	 *
	 * @param inPX
	 * @return
	 */
	public P1 G1polymul(int []inPX) throws Exception
	{
		BigInteger []bipx = new BigInteger[inPX.length];

		for (int i = 0; i < inPX.length; i++) {
			bipx[i] = new BigInteger(Integer.toString(inPX[i]));
		}
		return G1polymul(bipx);
	}

	/**
	 *
	 * @param inPX
	 * @return
	 * @throws Exception
	 */
	public P1 G1polymul(BigInteger []inPX) throws Exception
	{
		P1 rval = null;

		for (int i = 0; i < inPX.length; i++) {

			BigInteger x = inPX[i];
			if (x.compareTo(BigInteger.ZERO) != 0) {

				if (x.compareTo(BigInteger.ZERO) < 0) {
					x = CURVE_ORDER.add(x);
				}
				Scalar pi = new Scalar(x);

				P1 si = mSiG[i].dup();

				if (sLog.isDebugEnabled()) {
					String msg = U.f("  G1polymul si [%s] pi[%s]", si.toString(), pi.toString());
					sLog.debug(msg);
				}

				P1 tmpecp = si.mult(pi);

				//U.pf("  tmpC [%s]\n", tmpecp.toString());

				if (rval == null) {
					rval = tmpecp;
				} else {
					rval.add(tmpecp);
				}
				//U.pf("  rvalC [%s]\n", rval.toString());
			}
		}
		return rval;
	}

	/**
	 *
	 * @param inPX
	 * @return
	 * @throws Exception
	 */
	public P2 G2polymul(
			BigInteger []inPX) throws Exception
	{
		P2 rval = null;

		for (int i = 0; i < inPX.length; i++) {

			//int qxi = _get_qxi_()
			BigInteger x = inPX[i];
			Scalar tmp = new Scalar(x); //BIGUtil.createBIGWithCurveOrder(x);
			P2 P = mSiH[i].dup();

			if (sLog.isDebugEnabled()) {
				String msg = U.f("  G2polymul si [%s] P[%s]", P.toString(), P.toString());
				sLog.debug(msg);
			}
			P2 tmpecp = P.mult(tmp); //PAIR.G2mul(P, tmp);

			// U.pf("  tmpC [%s]\n", tmpecp.toString());

			if (rval == null) {
				rval = tmpecp;
			} else {
				rval.add(tmpecp);
			}
		}
		return rval;
	}

	/**
	 *
	 * @param inPX
	 * @return
	 * @throws Exception
	 */
	public P2 G2polymul(
			int []inPX) throws Exception
	{
		BigInteger []bipx = new BigInteger[inPX.length];
		
		for (int i = 0; i < inPX.length; i++) {
			bipx[i] = new BigInteger(Integer.toString(inPX[i]));
		}		
		return G2polymul(bipx);		
	}

	/**
	 * 
	 * @param inN
	 * @return
	 */
	public static SRSBlst dummyGenerate(int inN, RAND inRnd)
	{
		SRSBlst rval = new SRSBlst();
		
		rval.mSiG = new P1[inN];
		rval.mSiH = new P2[inN];
			 		
		//BIG s = BIG.random(inRnd);

		Random r = new Random(42);

		// FIXME remove:
		BigInteger rnd = new BigInteger(381, r);
		rnd = new BigInteger("20650170042820896942922516001373622548271078954099235583180748822798542671826088152310895112196614677158189527917090");


		Scalar s = new Scalar(rnd);

		U.pf("SRS s: [%s]\n", BLSTUtil.ss(s));
		//BIG m = new BIG(ROM.CURVE_Order); //BIG(ROM.Modulus);


		P1 G = P1.generator();

		
		P2 H = P2.generator();
		
		Scalar pow = new Scalar(BigInteger.ONE);
		for (int i = 0; i < inN; i++) {
			//BIG ei = new BIG(i);
			
			//U.pf("  ei s: [%s]\n", ei.toString());
			P1 tmp = G.dup().mult(pow);
			rval.mSiG[i] = tmp; //PAIR.G1mul(G, pow);
			rval.mSiH[i] = H.dup().mult(pow); //PAIR.G2mul(H, pow);

			String ctx = U.f("S[%d] ", i);

			BLSTUtil.outP1(tmp.serialize(),  ctx);

			//rval.mSiG[i].debugString();

			//BIG tmp = new BIG(pow);
			pow = pow.mul(s); //BIG.modmul(pow, s, m);

			U.pf("pow [%d] [%s]\n", i, BLSTUtil.ss(pow));
		}		
		return rval;
	}

}

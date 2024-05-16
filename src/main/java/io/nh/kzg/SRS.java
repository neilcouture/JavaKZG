package io.nh.kzg;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.apache.milagro.amcl.RAND;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.PAIR;
import org.apache.milagro.amcl.BLS381.ROM;

import io.nh.U;

public class SRS {

	static final Logger sLog = Logger.getLogger(SRS.class);
	
	
	public ECP []mSiG = null;
	public ECP2 []mSiH = null;
	
	/**
	 * 
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("SRS len:").append(mSiG.length).append("\ns^iGs:\n");
		for (ECP p : mSiG) {
			sb.append("  ").append(p.toString()).append("\n");
		}
		sb.append("s^iHs:\n");
		for (ECP2 p : mSiH) {
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
	 * 
	 * @param inPX
	 * @return
	 * @throws Exception
	 */
	public ECP G1polymul(BigInteger []inPX) throws Exception
	{
		ECP rval = null;
		
		for (int i = 0; i < inPX.length; i++) {
			
			BigInteger x = inPX[i];
			if (x.compareTo(BigInteger.ZERO) != 0) {

				if (x.compareTo(BigInteger.ZERO) < 0) {

				}

				BIG pi = BIGUtil.createBIGWithCurveOrder(x);
//				if (x.compareTo(BigInteger.ZERO) < 0) {
//					BIG co = new BIG(ROM.CURVE_Order);
//					BigInteger curveOrder = new BigInteger(co.toString(), 16);
//					BigInteger minusX = curveOrder.add(x);
//					String bs = minusX.toString();
//					pi = BIGUtil.createBigFromString(bs);
//					//pi = tmp.minus(tmppi);
//				} else {
//					String bs = x.toString();
//					pi = BIGUtil.createBigFromString(bs);//, inPow); //new BIG(x);
//				}
				ECP si = mSiG[i];
			
				if (sLog.isDebugEnabled()) {
					String msg = U.f("  G1polymul si [%s] pi[%s]", si.toString(), pi.toString());
					sLog.debug(msg);
				}
				
				ECP tmpecp = si.mul(pi);
				
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
	 * inP is a Polynomial X
	 *  index i represent the factor f in f * x^i
	 * 
	 * 
	 * 
	 * @param inPX
	 * @return
	 */
	public ECP G1polymul(int []inPX) throws Exception
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
	 * @param iny
	 * @param inz
	 * @return
	 */
	public ECP2 G2polymul(
			BigInteger []inPX) throws Exception
	{
		ECP2 rval = null;
		
		for (int i = 0; i < inPX.length; i++) {
			
			//int qxi = _get_qxi_() 
			BigInteger x = inPX[i];
			BIG tmp = BIGUtil.createBIGWithCurveOrder(x);
			ECP2 P = mSiH[i];
		
			if (sLog.isDebugEnabled()) {
				String msg = U.f("  G2polymul si [%s] P[%s]", P.toString(), P.toString());
				sLog.debug(msg);
			}
			ECP2 tmpecp = PAIR.G2mul(P, tmp);
			
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
	 * @param iny
	 * @param inz
	 * @return
	 */
	public ECP2 G2polymul(
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
	 * @param inPX
	 * @return
	 
	public ECP2 G2polymul__(
			int []inPX)
	{
		ECP2 rval = null;
		
		for (int i = 0; i < inPX.length; i++) {
			
			//int qxi = _get_qxi_() 
			
			BIG tmp = new BIG(inPX[i]);
			ECP2 P = mSiH[i];
		
			ECP2 tmpecp = PAIR.G2mul(P, tmp);
			
			// U.pf("  tmpC [%s]\n", tmpecp.toString());
			
			if (rval == null) {
				rval = tmpecp;
			} else {
				rval.add(tmpecp);
			}
		}		
		return rval;
	}*/
	/**
	 * 
	 * @param inN
	 * @return
	 */
	public static SRS dummyGenerate(int inN, RAND inRnd) throws Exception
	{
		SRS rval = new SRS();
		
		rval.mSiG = new ECP[inN];
		rval.mSiH = new ECP2[inN];

		// FIXME : cahnge
		BIG s = BIG.random(inRnd);
		s = BIGUtil.createBigFromString("11176975782092427528024412817437134724757850078576941836537795437579715744625");

		// 20650170042820896942922516001373622548271078954099235583180748822798542671826088152310895112196614677158189527917090
		BigInteger bbb = new BigInteger(s.toString(), 16);

		//bbb.toByteArray()

		U.pf("SRS s: [%s]\n", bbb.toString());
		
		BIG m = new BIG(ROM.CURVE_Order); //BIG(ROM.Modulus);
		
		ECP G = ECP.generator();	

		G.affine();
		
		ECP2 H = ECP2.generator();
		
		BIG pow = new BIG(1);
		for (int i = 0; i < inN; i++) {
				
			rval.mSiG[i] = PAIR.G1mul(G, pow);
			rval.mSiH[i] = PAIR.G2mul(H, pow);

			U.pf("S[%d] [%s]\n", i, rval.mSiG[i].toString());

			pow = BIG.modmul(pow, s, m);

			U.pf("pow [%d] [%s]\n", i, (new BigInteger(pow.toString(), 16).toString()));
		}		
		return rval;
	}

}

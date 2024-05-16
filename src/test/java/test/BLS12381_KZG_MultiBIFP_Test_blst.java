package test;

import io.nh.U;
import io.nh.kzg.BIGUtil;
import io.nh.kzg.PolynomialBIFP;
import io.nh.kzg.SRS;
import io.nh.kzg.SRSBlst;
import io.nh.kzg.blst.BLSTUtil;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.FP12;
import org.apache.milagro.amcl.BLS381.PAIR;
import org.apache.milagro.amcl.RAND;

import supranational.blst.*;

import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class BLS12381_KZG_MultiBIFP_Test_blst {

	/**
	 * THIS TEST IS NOT PASSING NEEDS TO DEBUG
	 * @throws Exception
	 */
	@Test
	public void KateCommitmentMultiTest() throws Exception
	{
		int pl = 5;
		RAND rng = U.getRAND();

		SRSBlst s = SRSBlst.dummyGenerate(pl, rng);
		
		BigInteger []p = new BigInteger[5];
		
		p[0] = new BigInteger("-29273458273485");
		p[1] = new BigInteger("-123413423423423423423234234563456345633333333333333331");
		p[2] = new BigInteger("200000");
		p[3] = new BigInteger("412341234234123412");
		p[4] = new BigInteger("-1");
		
		P1 C = s.G1polymul(p);
		// U.pf("Commitment for P(x) [%s]\n", C.toString());
		BigInteger mod = BIGUtil.BI_CURVE_ORDER;
		//int []Rs = {4, 8, 12};
		BigInteger []Rs = new BigInteger[4];
		Rs[0] = BIGUtil.createBI("4");
		Rs[1] = BIGUtil.createBI("12347676861267346880000134112341234");
		Rs[2] = BIGUtil.createBI("12");
		Rs[3] = BIGUtil.createBI("1123456789876543212");
		
		PolynomialBIFP px = PolynomialBIFP.createFrom(p, mod);
		BigInteger []vals = px.evaluate(Rs);
		
		PolynomialBIFP ix = PolynomialBIFP.createIX(Rs, vals, mod, true);
		PolynomialBIFP zx = PolynomialBIFP.createZX(Rs, mod);	
		
		PolynomialBIFP qx = px.minus(ix);
		PolynomialBIFP []res = qx.divides(zx, true);
		qx = res[0];
		
		BigInteger []qxp = qx.coefs();
		
		P1 pi = s.G1polymul(qxp);
		
		boolean resp = _validate_proof_multi_(s, C, ix, zx, pi);
		assertTrue("KZG blst failed", resp);
	}

	/**
	 *
	 * @param s
	 * @param inCommitment
	 * @param inIX
	 * @param inZX
	 * @param inPi
	 * @throws Exception
	 */
	private boolean _validate_proof_multi_(
			SRSBlst s,
			P1 inCommitment,
			PolynomialBIFP inIX,
			PolynomialBIFP inZX,
			P1 inPi) throws Exception
	{	
		// e1
		BigInteger []ixc = inIX.coefs();
		P1 yIG1 = s.G1polymul(ixc);
		P1 CmI = inCommitment.dup();
		CmI = BLSTUtil.sub(CmI, yIG1);

		// e2
		BigInteger []izc = inZX.coefs();
		P2 zG2 = s.G2polymul(izc);

		// pairing
		CmI.cneg(true);
		PT e1 = new PT(P2.generator(), CmI);
		PT e2 = new PT(zG2, inPi);

		PT tmp = e1.dup();
		tmp = tmp.mul(e2);

		PT tmp2 = tmp.final_exp();
		// is one?
		boolean pass2 = tmp2.is_one();
		if (pass2) {
			U.pf("BLST MultiBIFP Commitment verified\n");
		} else {
			U.pf("BLST MultiBIFP Commitment NOT verified\n");
		}
		return pass2;
	}
}

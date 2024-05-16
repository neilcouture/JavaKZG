package test;

import io.nh.U;
import io.nh.kzg.*;
import io.nh.kzg.blst.BLSTUtil;
import org.apache.milagro.amcl.RAND;
import supranational.blst.*;

import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class BLS12381_KZG_SingleBIFP_test_blst {


	/**
	 * https://github.com/protolambda/go-kzg/blob/master/kzg_single_proofs.go
	 *
	 * @param inPL polynomial lenght
	 */
	@Test
	public void _KateCommitmentTest_blst_() throws Exception
	{
		int pl = 5;
		RAND rng = U.getRAND();
		SRSBlst s = SRSBlst.dummyGenerate(pl, rng);

		int []p = new int[5];

		// -x^4 + 4x^3 + 2x^2 + x + 2
		p[0] = 2; p[1] = 1; p[2] = 2; p[3] = 4; p[4] = -1;
		P1 C = s.G1polymul(p);
		BigInteger mod = BIGUtil.BI_CURVE_ORDER;

		BigInteger R = new BigInteger("5");
		PolynomialBIFP py = PolynomialBIFP.createFrom(p, mod);
		BigInteger value = py.evaluate(R);

		U.pf("value [%d]\n", value);

		PolynomialBIFP qx = PolynomialBIFP.createQx(p, R, mod);
		BigInteger []qxp = qx.coefs();
		U.pf("qx [%s]\n", qx);
		for (int i = 0; i < qxp.length; i++) {
			U.pf("qxp:%d [%d]\n", i, qxp[i]);
		}

		P1 pi = s.G1polymul(qxp);
		//U.pf("Commitment for Q(x) [%s]\n", pi.toString());

		BLSTUtil.outP1(pi.serialize(), "proof for Q(x) ");

		boolean resp = _validate_proof_single_blst_(s, C, R, value, pi);
		assertTrue("blst single proof failed", resp);
	}

	/**
	 * bls12_381.c pairing_verify
	 *
	 * @param s
	 * @param inCommitment
	 * @param inX
	 * @param inY
	 * @param inPi
	 */
	private boolean _validate_proof_single_blst_(
			SRSBlst s,
			P1 inCommitment,
			BigInteger inX,
			BigInteger inY,
			P1 inPi) throws Exception
	{
		U.pf("commitment [%s]\n", inCommitment.toString());
		U.pf("X [%d]\n", inX);
		U.pf("Y [%d]\n", inY);
		U.pf("pi [%s]\n", inPi.toString());

		Scalar Y = new Scalar(inY);
		Scalar X = new Scalar(inX);

		P1 yG1 = P1.generator().mult(Y);
		P1 xMy = inCommitment.dup();
		xMy = BLSTUtil.sub(xMy, yG1);

		// e2
		P2 xG2 = P2.generator().dup().mult(X);
		P2 sMx = s.mSiH[1].dup();
		sMx = BLSTUtil.sub(sMx, xG2);

		// a1 = xMy = commitment minux y  	g1
		// a2 = g2.generator				g2
		// b1 = inPi = proof				g1
		// b2 = sMx = s minux X				g2

		//xMy = xMy.cneg(true);

		P1 negc = xMy.cneg(true);

		P1_Affine aa1 = new P1_Affine(negc);
		P2_Affine aa2 = new P2_Affine(P2.generator());
		P1_Affine bb1 = new P1_Affine(inPi);
		P2_Affine bb2 = new P2_Affine(sMx);


		PT loop0 = new PT(aa2, aa1);
		PT loop1 = new PT(bb2, bb1);

		//loop0 = loop0.inverse();

		PT gt = loop0.mul(loop1);

		gt = gt.final_exp();

		boolean pass2 = gt.is_one();

		if (pass2) {
			U.pf("BLST Single BIFP Commitment verified\n");
		} else {
			U.pf("BLST Single BIFP Commitment NOT verified\n");
		}
		return pass2;
	}
}

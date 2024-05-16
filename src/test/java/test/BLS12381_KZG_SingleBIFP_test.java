package test;

import java.math.BigInteger;

import io.nh.kzg.SRSBlst;
import org.apache.milagro.amcl.RAND;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.FP12;
import org.apache.milagro.amcl.BLS381.PAIR;

import io.nh.U;
import io.nh.kzg.BIGUtil;
import io.nh.kzg.PolynomialBIFP;
import io.nh.kzg.SRS;
import supranational.blst.P1;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class BLS12381_KZG_SingleBIFP_test {

	@Test
	public void _KateCommitmentTest_() throws Exception
	{
		int pl = 5;
		RAND rng = U.getRAND();		
		SRS s = SRS.dummyGenerate(pl, rng);
	
		
		int []p = new int[5];
		
		// -x^4 + 4x^3 + 2x^2 + x + 2
		p[0] = 2; p[1] = 1; p[2] = 2; p[3] = 4; p[4] = -1;
		ECP C = s.G1polymul(p);
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
		
		//ECP G = ECP.generator();
		ECP pi = s.G1polymul(qxp);
		U.pf("proof for Q(x) [%s]\n", pi.toString());
		
		boolean resp = _validate_proof_single_(s, C, R, value, pi);
		assertTrue("Sinble proof failed", resp);
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
	private boolean _validate_proof_single_(
			SRS s,
			ECP inCommitment, 
			BigInteger inX, 
			BigInteger inY, 
			ECP inPi) throws Exception
	{			
		U.pf("commitment [%s]\n", inCommitment.toString());
		U.pf("X [%d]\n", inX);
		U.pf("Y [%d]\n", inY);
		U.pf("pi [%s]\n", inPi.toString());
		// e1
		BIG Y = BIGUtil.createBIGWithCurveOrder(inY);
		BIG X = BIGUtil.createBIGWithCurveOrder(inX);
		
		ECP yG1 = ECP.generator().mul(Y);
		ECP xMy = new ECP(inCommitment);
		xMy.sub(yG1);
		FP12 e1 = PAIR.ate(ECP2.generator(), xMy);
		
		// e2
		ECP2 xG2 = ECP2.generator().mul(X);		
		ECP2 sMx = new ECP2(s.mSiH[1]);
		sMx.sub(xG2);
		FP12 e2 = PAIR.ate(sMx, inPi);

		//U.pf("e1 [%s]\n", e1.toString());
		//U.pf("e2 [%s]\n", e2.toString());
				
		// final step..
		e1.inverse();	
		FP12 tmp = new FP12(e1);
		tmp.mul(e2);
		
		FP12 tmp2 = PAIR.fexp(tmp);
		// is one?
		boolean pass2 = tmp2.isunity();
		if (pass2) {
			U.pf("Single BIFP Commitment verified\n");
		} else {
			U.pf("Single BIFP Commitment NOT verified\n");
		}
		return pass2;
	}
}

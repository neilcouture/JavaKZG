package test;

import java.math.BigInteger;

import org.apache.milagro.amcl.RAND;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.FP12;
import org.apache.milagro.amcl.BLS381.PAIR;

import io.nh.U;
import io.nh.kzg.BIGUtil;
import io.nh.kzg.PolynomialBIFP;
import io.nh.kzg.SRS;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BLS12381_KZG_MultiBIFP_Test1 {

	@Test
	public void _KateCommitmentMultiTest_() throws Exception
	{
		int pl = 5;
		RAND rng = U.getRAND();
		
		SRS s = SRS.dummyGenerate(pl, rng);
		
		// U.pf("G1G2s [%s]\n", s.toString());	
		//int []p = new int[5];
		// -x^4 + 4x^3 + 2x^2 + x + 2
		//p[0] = 2; p[1] = 1; p[2] = 2; p[3] = 4; p[4] = -1;
		
		BigInteger []p = new BigInteger[5];
		
		p[0] = new BigInteger("-29273458273485");
		p[1] = new BigInteger("-123413423423423423423234234563456345633333333333333331");
		p[2] = new BigInteger("200000");
		p[3] = new BigInteger("412341234234123412");
		p[4] = new BigInteger("-1");
		
		ECP C = s.G1polymul(p);
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
		boolean useOmegasForDomain = false;
		PolynomialBIFP ix = PolynomialBIFP.createIX(Rs, vals, mod, useOmegasForDomain);
		PolynomialBIFP zx = PolynomialBIFP.createZX(Rs, mod);	
		
		PolynomialBIFP qx = px.minus(ix);
		PolynomialBIFP []res = qx.divides(zx, true);
		qx = res[0];
		
		BigInteger []qxp = qx.coefs();
		
		ECP pi = s.G1polymul(qxp);
		
		boolean resp = _validate_proof_multi_(s, C, ix, zx, pi);

		assertTrue("MultiBIFP proof error", resp);
	}
	/**
	 * 
	 * @param s
	 * @param inCommitment
	 * @param inX
	 * @param inY
	 * @param inPi
	 */
	private boolean _validate_proof_multi_(
			SRS s, 
			ECP inCommitment, 
			PolynomialBIFP inIX,
			PolynomialBIFP inZX,
			ECP inPi) throws Exception
	{	
		// e1
		BigInteger []ixc = inIX.coefs();
		ECP yIG1 = s.G1polymul(ixc);
		ECP CmI = new ECP(inCommitment);
		CmI.sub(yIG1);		
		FP12 e1 = PAIR.ate(ECP2.generator(), CmI);
		
		// e2
		BigInteger []izc = inZX.coefs();
		ECP2 zG2 = s.G2polymul(izc);
		FP12 e2 = PAIR.ate(zG2, inPi);
				
		// final step..
		e1.inverse();	
		FP12 tmp = new FP12(e1);	
		tmp.mul(e2);
		
		FP12 tmp2 = PAIR.fexp(tmp);
		// is one?
		boolean pass2 = tmp2.isunity();
		if (pass2) {
			U.pf("MultiBIFP Commitment verified\n");
		} else {
			U.pf("MultiBIFP Commitment NOT verified\n");
		}
		return pass2;
	}

}

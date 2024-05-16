package test;

import java.math.BigInteger;

import io.nh.kzg.*;
import org.apache.milagro.amcl.RAND;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.FP12;
import org.apache.milagro.amcl.BLS381.PAIR;

import io.nh.U;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BLS12381_KZG_MultiBIFP_Test {

	@Test
	public void KateCommitmentMultiTest_fft_domain() throws Exception
	{
		int size = 16;
		RAND rng = U.getRAND();
		SRS s = SRS.dummyGenerate(size, rng);

		// -x^4 + 4x^3 + 2x^2 + x + 2
		//p[0] = 2; p[1] = 1; p[2] = 2; p[3] = 4; p[4] = -1;

		BigInteger []domain = BLS381BIRootUnity.computeRootOfUnity(size);

		// y values for polynmomial,
		BigInteger []valsForP = new BigInteger[size];

		// we need 16 values - or a power of 2 for FFT.inverse to works
		valsForP[0] = new BigInteger("345345");
		valsForP[1] = new BigInteger("12");
		valsForP[2] = new BigInteger("11");
		valsForP[3] = new BigInteger("111111");
		valsForP[4] = new BigInteger("10");
		valsForP[5] = new BigInteger("0");
		valsForP[6] = new BigInteger("0");
		valsForP[7] = new BigInteger("0");
		valsForP[8] = new BigInteger("0");
		valsForP[9] = new BigInteger("0");
		valsForP[10] = new BigInteger("0");
		valsForP[11] = new BigInteger("0");
		valsForP[12] = new BigInteger("0");
		valsForP[13] = new BigInteger("0");
		valsForP[14] = new BigInteger("0");
		valsForP[15] = new BigInteger("0");

		// compute polynomial values using domain as Xs
		BigInteger[]p = FFTBIFPUtil.inverseFFT(valsForP, domain, BIGUtil.BI_CURVE_ORDER);

		ECP C = s.G1polymul(p);
		// U.pf("Commitment for P(x) [%s]\n", C.toString());
		BigInteger mod = BIGUtil.BI_CURVE_ORDER;
		//int []Rs = {4, 8, 12};
		// evaluate polynomial at some points::
		BigInteger[] Rs = new BigInteger[7];
		Rs[0] = BIGUtil.createBI("4");
		Rs[1] = BIGUtil.createBI("12347676861267346880000134112341234");
		Rs[2] = BIGUtil.createBI("12");
		Rs[3] = BIGUtil.createBI("1123456789876543212");
		Rs[4] = BIGUtil.createBI("34");
		Rs[5] = BIGUtil.createBI("123123123");
		Rs[6] = BIGUtil.createBI("5");
		//Rs[7] = BIGUtil.createBI("6");

		boolean useOmegasForDomain = false;
		if (useOmegasForDomain) {
			Rs = BLS381BIRootUnity.computeRootOfUnity(8);
//			BigInteger []tmp = new BigInteger[7];
//			for (int i = 0; i < 7; i++) {
//				tmp[i] = Rs[i];
//			}
//			Rs = tmp;
		}
		PolynomialBIFP px = PolynomialBIFP.createFrom(p, mod);
		BigInteger []vals = px.evaluate(Rs);

		// interpolation polynomial
		PolynomialBIFP ix = PolynomialBIFP.createIX(Rs, vals, mod, useOmegasForDomain);
		// zero polynomial
		PolynomialBIFP zx = PolynomialBIFP.createZX(Rs, mod);	

		// quotient polynomial
		PolynomialBIFP qx = px.minus(ix);
		PolynomialBIFP []res = qx.divides(zx, true);
		qx = res[0];
		
		BigInteger []qxp = qx.coefs();
		
		ECP pi = s.G1polymul(qxp);

		// C is commitment on polynomial
		// ix
		// zx
		// pi is proof
		boolean resp = _validate_proof_multi_(s, C, ix, zx, pi);
		assertTrue("Multi Proof ok:", resp);
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
			SRS s, 
			ECP inCommitment, 
			PolynomialBIFP inIX,
			PolynomialBIFP inZX,
			ECP inPi) throws Exception
	{	
		// e1
		BigInteger []ixc = inIX.coefs();
		ECP yIG1 = s.G1polymul(ixc); //ECP.generator().mul(new BIG(inY));
		ECP CmI = new ECP(inCommitment);
		CmI.sub(yIG1);		
		FP12 e1 = PAIR.ate(ECP2.generator(), CmI);
		
		// e2
		BigInteger []izc = inZX.coefs();
		ECP2 zG2 = s.G2polymul(izc); //ECP2.generator().mul(new BIG(inX));		
		FP12 e2 = PAIR.ate(zG2, inPi);
				
		// final step..
		e1.inverse();	
		FP12 tmp = new FP12(e1);	
		tmp.mul(e2);
		
		FP12 tmp2 = PAIR.fexp(tmp);
		// is one?
		boolean rval = tmp2.isunity();
		if (rval) {
			U.pf("MultiBIFP Commitment with FFT Omegas verified\n");
		} else {
			U.pf("MultiBIFP Commitment NOT verified\n");
		}
		return rval;
	}


}

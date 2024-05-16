package io.nh.kzg;

import java.math.BigInteger;

import io.nh.U;

//see http://introcs.cs.princeton.edu/java/92symbolic/Polynomial.java.html 

/**
 * Polynomial with support for finite field if mMod is set.
 * @author neil
 *
 */
public class PolynomialBIFP {
	private BigInteger[] coef; // coefficients
	private int deg; // degree of polynomial (0 for the zero polynomial)
	private BigInteger mMod = null;

	/**
	 *
	 * @return
	 */
	public BigInteger getModulo()
	{
		return mMod;
	}

	/**
	 *
	 * @param a
	 * @param b
	 * @param inMod
	 */
	public PolynomialBIFP(BigInteger a, int b, BigInteger inMod) 
	{
		coef = new BigInteger[b + 1];
		mMod = inMod;
		
		coef[b] = BIGUtil.createBI(a.toString(), inMod);	

		_init_coef_(b, inMod);		
		deg = degree();
		
	}

	/**
	 *
	 * @param inA
	 * @param inB
	 * @param inMod
	 */
	public PolynomialBIFP(String inA, String inB, BigInteger inMod) {
		int a = Integer.parseInt(inA);
		int b = Integer.parseInt(inB);
		coef = new BigInteger[b + 1];
		coef[b] = BIGUtil.createBI(Integer.toString(a), inMod);		
		_init_coef_(b, inMod);		
		deg = degree();
		mMod = inMod;

	}
	/**
	 * 
	 * @param b
	 */
	private void _init_coef_(int b, BigInteger inMod)
	{
		for (int i = 0; i < b; i++) {
			coef[i] = BIGUtil.createBI("0", inMod); //new BigInteger("0");
		}
	}
	/**
	 * 
	 */
	public PolynomialBIFP()
	{
		coef = null;
		deg = -1;		
	}
	/**
	 * 
	 * @param inS
	 * @return
	 */	
	private BigInteger _create_bi_needed_(String inS, BigInteger inM)
	{
		BigInteger rval = BIGUtil.createBI(inS, inM);
		return rval;
	}

//	/**
//	 * 
//	 * @param inS
//	 * @return
//	 */
//	BigInteger _create_bi_(String inS)
//	{
//		BigInteger rval = null;
//		if (mMod != null) {
//			rval = new BIFP(inS, mMod);
//		} else {
//			rval = new BigInteger(inS);
//		}
//		return rval;
//	}
	/**
	 * 
	 * @param p
	 */
	public PolynomialBIFP(PolynomialBIFP p) {
		coef = new BigInteger[p.coef.length];
		for (int i = 0; i < p.coef.length; i++) {
			coef[i] = _create_bi_needed_(p.coef[i].toString(), p.mMod);
		} // end for
		deg = p.degree();
		mMod = p.mMod;
	}

	// return the degree of this polynomial (0 for the zero polynomial)
	public int degree()
	{
		int d = 0;
		for (int i = 0; i < coef.length; i++)
			if (coef[i].compareTo(BigInteger.ZERO) != 0)
			//if (coef[i] != null) {
			//	if (!coef[i].iszilch())
					d = i;
			//}
		return d;
	}
	
	private void _o_()
	{
		for (int i = 0; i < this.coef.length;  i++) {
			U.pf(" c[%d] [%s]\n", i, coef[i]);
		}
	}
	// return c = a + b
	public PolynomialBIFP plus(PolynomialBIFP b)
	{
		PolynomialBIFP a = this;
		
		int mm = Math.max(a.deg, b.deg);
		
		PolynomialBIFP c = new PolynomialBIFP("0", Integer.toString(mm), a.mMod);

		for (int i = 0; i <= a.deg; i++) {
			//c.coef[i] += a.coef[i];
			c.coef[i] = c.coef[i].add(a.coef[i]);

			//c._o_();
		}
		for (int i = 0; i <= b.deg; i++) {
			//c.coef[i] += b.coef[i];
			c.coef[i] = c.coef[i].add(b.coef[i]);
			//c._o_();
		}
		c.deg = c.degree();
		//U.pf("\t\t\tc2 [%s]\n", c);
		return c;
	}

	// return (a - b)
	public PolynomialBIFP minus(PolynomialBIFP b)
	{
		PolynomialBIFP a = this;
		int mm = Math.max(a.deg, b.deg);
		PolynomialBIFP c = new PolynomialBIFP("0", Integer.toString(mm), a.mMod );
		for (int i = 0; i <= a.deg; i++) {
			//c.coef[i] += a.coef[i];
			c.coef[i] = c.coef[i].add(a.coef[i]);
		}
		for (int i = 0; i <= b.deg; i++) {
			//c.coef[i] -= b.coef[i];
			c.coef[i] = c.coef[i].subtract(b.coef[i]);
		}
		c.deg = c.degree();
		return c;
	}

	// return (a * b)
	public PolynomialBIFP times(PolynomialBIFP b)
	{
		PolynomialBIFP a = this;
		int deg = a.deg + b.deg;
		PolynomialBIFP c = new PolynomialBIFP("0", Integer.toString(deg), this.mMod);
		for (int i = 0; i <= a.deg; i++) {
			for (int j = 0; j <= b.deg; j++) {
				// c.coef[i + j] += (a.coef[i] * b.coef[j]);
				BigInteger tmp = a.coef[i].multiply(b.coef[j]);
				c.coef[i + j] = c.coef[i + j].add(tmp);			
			}
		}
		c.deg = c.degree();
		return c;
	}

	// get the coefficient for the highest degree
	public BigInteger coeff()
	{
		int dd = degree();
		return coeff(dd);
	}

	// get the coefficient for degree d
	public BigInteger coeff(int degree)
	{
		if (degree > this.degree())
			throw new RuntimeException("bad degree");
		return _create_bi_needed_(coef[degree].toString(), mMod);
	}

	/*
	 * Implement the division as described in wikipedia function n / d: require d ≠
	 * 0 q ← 0 r ← n # At each step n = d × q + r while r ≠ 0 AND degree(r) ≥
	 * degree(d): t ← lead(r)/lead(d) # Divide the leading terms q ← q + t r ← r − t
	 * * d return (q, r)
	 */
	public PolynomialBIFP[] divides(PolynomialBIFP b) throws Exception
	{
		return divides(b, false);
	}

	/**
	 *
	 * @param b
	 * @param inNoRemainderCheck
	 * @return
	 * @throws Exception
	 */
	public PolynomialBIFP[] divides(
			PolynomialBIFP b, 
			boolean inNoRemainderCheck) throws Exception
	{
		//String mod = b.getModulo();
		
		PolynomialBIFP q = new PolynomialBIFP("0", "0", this.mMod);
		PolynomialBIFP r = new PolynomialBIFP(this);

		//U.pf("divide r0 [%s]\n", r);
		int dr = r.degree();
		//U.pf("divide R deg:%d\n", dr);
		while (!r.isZero() && r.degree() >= b.degree()) {
			//int coef = r.coeff() / b.coeff();
			
			BigInteger coef = r.coeff();
			BigInteger tmp = b.coeff();
			coef = coef.divide(tmp);
			int deg = r.degree() - b.degree();
			
			//String cs =  coef.toString();
			//String rs = r.coeff().toString();
			//String bs = b.coeff().toString();
			
			//U.pf("divide coef:%s [%s / %s] \n", cs, rs, bs);		
			//U.pf("divide deg:%d [%d - %d] \n", deg, r.degree(), b.degree());
			
			PolynomialBIFP t = new PolynomialBIFP(coef, deg, this.mMod);
			q = q.plus(t);
			//U.pf("divide q [%s]\n", q);
			PolynomialBIFP tmpp = t.times(b);
			//U.pf("divide tmpp [%s]\n", tmpp);
			//U.pf("divide r1 [%s]\n", r);
			r = r.minus(tmpp);
			//U.pf("divide r2 [%s]\n", r);
			
			boolean rz = r.isZero();
			dr = r.degree();
			int db = b.degree();
			
			//U.pf("\tdividerz:%b dr:%d db:%d\n", rz, dr, db);
		} // end while

		if (inNoRemainderCheck) {
			// check no remainder
			BigInteger []remainC = r.coefs();
			for (int i = 0; i < remainC.length; i++) {
				//if (remainC[i] != 0) {
				if (remainC[i].compareTo(BigInteger.ZERO) != 0) {
					String m = U.f("remaider[%d] degree wrong:%d \n", i, remainC[i]);
					throw new Exception(m);
				}
			}
		}	
		//System.out.printf("(%s) / (%s): %s, %s", this, b, q, r);
		return new PolynomialBIFP[] { q, r };
	}

	// return a(b(x)) - compute using Horner's method
	public PolynomialBIFP compose(PolynomialBIFP b)
	{
		PolynomialBIFP a = this;
		PolynomialBIFP c = new PolynomialBIFP("0", "0", this.mMod);
		for (int i = a.deg; i >= 0; i--) {
			PolynomialBIFP term = new PolynomialBIFP(a.coef[i], 0, this.mMod);
			c = term.plus(b.times(c));
		}
		return c;
	}

	// do a and b represent the same polynomial?
	public boolean eq(PolynomialBIFP b)
	{
		PolynomialBIFP a = this;
		if (a.deg != b.deg)
			return false;
		for (int i = a.deg; i >= 0; i--)
			if (a.coef[i] != b.coef[i])
				return false;
		return true;
	}

	// test wether or not this polynomial is zero
	public boolean isZero()
	{
		for (BigInteger i : coef) {
			//if (i != 0)
			if (i.compareTo(BigInteger.ZERO) != 0)
				return false;
		} // end for
		return true;
	}
	// use Horner's method to compute and return the polynomial evaluated at x
	public BigInteger[] evaluate(int []x)
	{
		BigInteger []X = new BigInteger[x.length];
		for (int i = 0; i < x.length; i++) {
			X[i] = _create_bi_needed_(Integer.toString(x[i]), this.mMod);
		}
		return evaluate(X);
	}
	// use Horner's method to compute and return the polynomial evaluated at x
	public BigInteger[] evaluate(BigInteger []x)
	{
		BigInteger []rval = new BigInteger[x.length];
		for (int i = 0; i < x.length; i++)
			rval[i] = evaluate(x[i]);
		return rval;
	}
	
	// use Horner's method to compute and return the polynomial evaluated at x
	public BigInteger evaluate(int x)
	{
		BigInteger X = _create_bi_needed_(Integer.toString(x), mMod);
		return evaluate(X);
	}
	// use Horner's method to compute and return the polynomial evaluated at x
	public BigInteger evaluate(BigInteger x)
	{
		return _bi_eval_(x);	
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	private final BigInteger _bi_eval_(BigInteger x)
	{
		BigInteger p = new BigInteger("0");
		for (int i = deg; i >= 0; i--) {
			//p = coef[i] + (x * p);
			BigInteger tmp = x.multiply(p);
			p = coef[i].add(tmp);
		}
		return p;
	}
	/**
	 * 
	 * @return
	 
	public PolynomialBIFP differentiate()
	{
		if (deg == 0)
			return new PolynomialBIFP("0", "0", this.mMod);
		
		
		int dd = deg - 1;
		
		PolynomialBIFP deriv = new PolynomialBIFP("0", Integer.toString(dd), this.mMod);
		deriv.deg = deg - 1;
		for (int i = 0; i < deg; i++) {
			//deriv.coef[i] = (i + 1) * coef[i + 1];
			BigInteger tmp = _create_bi_needed_(Integer.toString(i + 1), mMod);			
			BigInteger tmp2 = tmp.multiply(coef[i + 1]);
			deriv.coef[i] = tmp2;		
		}
		return deriv;
	}*/
	/**
	 * 
	 */
	public String toString()
	{
		if (deg == 0) {
			BigInteger b = _create_bi_needed_(coef[0].toString(), mMod);
			return "" + b;
		}
		if (deg == 1) {
			BigInteger b1 = _create_bi_needed_(coef[1].toString(), mMod);
			BigInteger b0 = _create_bi_needed_(coef[0].toString(), mMod);
			return b1 + "x + " + b0;
		}
		BigInteger bb = _create_bi_needed_(coef[deg].toString(), mMod);
		String s = bb + "x^" + deg;
		for (int i = deg - 1; i >= 0; i--) {
			//if (coef[i] == 0) {
			if (coef[i].compareTo(BigInteger.ZERO) == 0) {
				continue;
			} else if (coef[i].compareTo(BigInteger.ZERO) == 1) {
				BigInteger b = _create_bi_needed_(coef[i].toString(), mMod);	
				s = s + " + " + (b);
			} else if (coef[i].compareTo(BigInteger.ZERO) == -1) {
				BigInteger b = _create_bi_needed_(coef[i].toString(), mMod);
				s = s + " " +  (b);
			}
			if (i == 1) {
				s = s + "x";
			} else if (i > 1)
				s = s + "x^" + i;
		}
		if (mMod != null) {
			s += " mod " + mMod;
		}
		return s;
	}
	/**
	 * 
	 * @return
	 */
	public BigInteger[] coefs()
	{
		return this.coef;
	}
	/**
	 * 
	 * @param inP
	 * @return
	 */
	public static PolynomialBIFP createFrom(
			int[] inP,
			BigInteger inMod)
	{
		BigInteger []p = new BigInteger[inP.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = BIGUtil.createBI(Integer.toString(inP[i]));
		}
		return createFrom(p, inMod);
	}

	/**
	 * Create a Polynomial with BigInteger over a finite field mod inMod
	 * @param inP
	 * @param inMod
	 * @return
	 */
	public static PolynomialBIFP createFrom(BigInteger[] inP, BigInteger inMod)
	{
		PolynomialBIFP rval = null;

		for (int i = 0; i < inP.length; i++) {
			PolynomialBIFP t = new PolynomialBIFP(inP[i], i, inMod);
			if (rval == null) {
				rval = t;
			} else {
				
				rval = rval.plus(t);
				//U.pf("\t\trval [%s] t [%s]\n", rval, t);
			}
		}
		return rval;
	}
	/**
	 * 
	 * @param inPx
	 * @param inR
	 * @return
	 */
	public static PolynomialBIFP createQx(
			int []inPx, 
			BigInteger inR,
			BigInteger inMod) throws Exception
	{
		BigInteger []p = new BigInteger[inPx.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = BIGUtil.createBI(Integer.toString(inPx[i]), inMod);
		}
		return createQx(p, inR, inMod);
	}
	/**
	 * 
	 * @param inPx
	 * @param inR
	 * @return
	 */
	public static PolynomialBIFP createQx(
			BigInteger []inPx, 
			BigInteger inR,
			BigInteger inMod) throws Exception
	{		
		PolynomialBIFP fX = PolynomialBIFP.createFrom(inPx, inMod);	
		
		//System.out.println("createQx fX(x) =     " + fX);
		BigInteger eval = fX.evaluate(inR);
		
		//U.pf("createQx eval:%s\n", eval);
		
		BigInteger []pp = inPx.clone();
		pp[0] = pp[0].subtract(eval); 
		
//		for (int i = 0; i < pp.length; i++) {
//			U.pf("createQx [%d] pp:%s\n", i, pp[i]);
//		}
		
		PolynomialBIFP pX = PolynomialBIFP.createFrom(pp, inMod);
		System.out.println("createQx pX(x) =     " + pX);
		
		BigInteger zero = BIGUtil.createBI("0");
		BigInteger mR = zero.subtract(inR);
		BigInteger[] qp = { mR, BIGUtil.createBI("1") };
		PolynomialBIFP qpx = PolynomialBIFP.createFrom(qp, inMod);
		System.out.println("createQx qpx(x) =     " + qpx);
		
		int pxd = pX.degree();
		//U.pf("createQx px degree:%d\n", pxd);
		PolynomialBIFP[] qx = pX.divides(qpx);
		
		System.out.println("createQx qx0(x) =     " + qx[0]);
		System.out.println("createQx qx1(x) =     " + qx[1]);
		
		// check no remainder
		BigInteger []remainC = qx[1].coefs();
		for (int i = 0; i < remainC.length; i++) {
			//if (remainC[i] != 0) {
			if (remainC[i].compareTo(BigInteger.ZERO) != 0) {
				String m = U.f("remaider[%d] degree wrong:%d \n", i, remainC[i]);
				throw new Exception(m);
			}
		}
		PolynomialBIFP rval = qx[0];
		return rval;
	}

	/**
	 *
	 * @param inX
	 * @param inY
	 * @param inMod
	 * @return
	 * @throws Exception
	 */
	public static PolynomialBIFP createIX(
			int[] inX, 
			int[] inY,
			String inMod) throws Exception
	{
		BigInteger mod = new BigInteger(inMod);
		LagrangeInterpolationBIFP tmp = new LagrangeInterpolationBIFP(mod);
		BigInteger []p = tmp.interpolate(inX, inY, mod, true);
		PolynomialBIFP rval = PolynomialBIFP.createFrom(p, mod); 
		return rval;
	}

	/**
	 *
	 * @param inX
	 * @param inMod
	 * @return
	 */
	public static PolynomialBIFP createZX(
			int[] inX,
			String inMod)
	{
		PolynomialBIFP rval = null;
		BigInteger mod = new BigInteger(inMod);
		for (int i = 0; i < inX.length; i++) {		
			int z = inX[i];
			
			int []tmp = {-z, 1};
			
			PolynomialBIFP tmpp = PolynomialBIFP.createFrom(tmp, mod);
			if (rval == null) {
				rval = tmpp;
			} else {
				rval = rval.times(tmpp);
			}
		}
		return rval;
	}

	/**
	 *
	 * @param inX
	 * @param inY
	 * @param inMod
	 * @return
	 * @throws Exception
	 */
	public static PolynomialBIFP createIX(
			BigInteger[] inX, 
			BigInteger[] inY,
			BigInteger inMod,
			boolean inUseOmegasForDomain) throws Exception
	{
		boolean useFFT = inUseOmegasForDomain;
		PolynomialBIFP rval = null;
		if (useFFT) {
			rval = createIX_fft(inY, inMod);
		} else {
			rval = createIX_lagrange(inX, inY, inMod);
		}
		return rval;
	}

	/**
	 *
	 * @param inX
	 * @param inY
	 * @param inMod
	 * @return
	 * @throws Exception
	 */
	private static PolynomialBIFP createIX_lagrange(
			BigInteger[] inX,
			BigInteger[] inY,
			BigInteger inMod) throws Exception
	{
		LagrangeInterpolationBIFP tmp = new LagrangeInterpolationBIFP(inMod, false);
		BigInteger []p = tmp.interpolate(inX, inY, inMod, true);
		PolynomialBIFP rval = PolynomialBIFP.createFrom(p, inMod);
		return rval;
	}

	/**
	 *
	 * @param inY
	 * @param inMod
	 * @return
	 * @throws Exception
	 */
	private static PolynomialBIFP createIX_fft(
			//BigInteger[] inX,
			BigInteger[] inY,
			BigInteger inMod) throws Exception
	{
		int clen = inY.length;
		BigInteger []domain = BLS381BIRootUnity.computeRootOfUnity(clen);


		//PolynomialBIFP rval = createIX_lagrange(domain, inY, inMod); // wrong degree : 2284882216431274752321654375375741713450140042891873265679974356661513043619

		BigInteger []p = FFTBIFPUtil.inverseFFT(inY, domain, inMod);
		PolynomialBIFP rval = PolynomialBIFP.createFrom(p, inMod);
		return rval;
	}

	/**
	 *
	 * @param inX
	 * @param inMod
	 * @return
	 */
	public static PolynomialBIFP createZX(BigInteger[] inX, BigInteger inMod)
	{
		PolynomialBIFP rval = null;
		for (int i = 0; i < inX.length; i++) {		
			BigInteger z = inX[i];
			
			//int []tmp = {-z, 1};
			BigInteger []tmp = new BigInteger[2];
			BigInteger minusZ = BIGUtil.BI_CURVE_ORDER.subtract(z);
			tmp[0] = minusZ;
			tmp[1] = BigInteger.ONE;
			
			PolynomialBIFP tmpp = PolynomialBIFP.createFrom(tmp, inMod);
			if (rval == null) {
				rval = tmpp;
			} else {
				rval = rval.times(tmpp);
			}
		}
		return rval;
	}
}
package io.nh.poly;

import java.math.BigInteger;

import io.nh.U;
import io.nh.kzg.BIGUtil;
import io.nh.kzg.LagrangeInterpolationBIFP;

//see http://introcs.cs.princeton.edu/java/92symbolic/Polynomial.java.html 

/**
 * Polynomial with support for finite field if mMod is set.
 * @author neil
 *
 */
public class PolynomialBI {
	private BigInteger[] coef; // coefficients
	private int deg; // degree of polynomial (0 for the zero polynomial)
	private String mMod = null;
	
	/**
	 * 
	 * @return
	 */
	public String getModulo()
	{
		return mMod;
	}	
	/**
	 * 
	 * @param a
	 * @param b
	 */
	public PolynomialBI(BigInteger a, int b) 
	{
		coef = new BigInteger[b + 1];
		
		
		coef[b] = new BigInteger(a.toString());	

		_init_coef_(b);		
		deg = degree();
	}
	/**
	 * 
	 * @param a
	 * @param b
	 */
	public PolynomialBI(String inA, String inB) {
		int a = Integer.parseInt(inA);
		int b = Integer.parseInt(inB);
		coef = new BigInteger[b + 1];
		coef[b] = new BigInteger(Integer.toString(a));		
		_init_coef_(b);		
		deg = degree();

	}
	/**
	 * 
	 * @param b
	 */
	private void _init_coef_(int b)
	{
		for (int i = 0; i < b; i++) {
			coef[i] = BIGUtil.createBI("0"); //new BigInteger("0");
		}
	}
	/**
	 * 
	 */
	public PolynomialBI()
	{
		coef = null;
		deg = -1;		
	}
	/**
	 * 
	 * @param inS
	 * @return
	 */	
	private BigInteger _create_bi_needed_(String inS)
	{
		BigInteger rval = BIGUtil.createBI(inS);
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
	public PolynomialBI(PolynomialBI p) {
		coef = new BigInteger[p.coef.length];
		for (int i = 0; i < p.coef.length; i++) {
			coef[i] = _create_bi_needed_(p.coef[i].toString());
		} // end for
		deg = p.degree();
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
	public PolynomialBI plus(PolynomialBI b)
	{
		PolynomialBI a = this;
		
		int mm = Math.max(a.deg, b.deg);
		
		PolynomialBI c = new PolynomialBI("0", Integer.toString(mm));

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
	public PolynomialBI minus(PolynomialBI b)
	{
		PolynomialBI a = this;
		int mm = Math.max(a.deg, b.deg);
		PolynomialBI c = new PolynomialBI("0", Integer.toString(mm) );
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
	public PolynomialBI times(PolynomialBI b)
	{
		PolynomialBI a = this;
		int deg = a.deg + b.deg;
		PolynomialBI c = new PolynomialBI("0", Integer.toString(deg));
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
		return coeff(degree());
	}

	// get the coefficient for degree d
	public BigInteger coeff(int degree)
	{
		if (degree > this.degree())
			throw new RuntimeException("bad degree");
		return _create_bi_needed_(coef[degree].toString());
	}

	/*
	 * Implement the division as described in wikipedia function n / d: require d ≠
	 * 0 q ← 0 r ← n # At each step n = d × q + r while r ≠ 0 AND degree(r) ≥
	 * degree(d): t ← lead(r)/lead(d) # Divide the leading terms q ← q + t r ← r − t
	 * * d return (q, r)
	 */
	public PolynomialBI[] divides(PolynomialBI b) throws Exception
	{
		return divides(b, false);
	}
	/**
	 * 
	 * @param b
	 * @param inNoRemainder
	 * @return
	 * @throws Exception
	 */
	public PolynomialBI[] divides(
			PolynomialBI b, 
			boolean inNoRemainderCheck) throws Exception
	{
		//String mod = b.getModulo();
		
		PolynomialBI q = new PolynomialBI("0", "0");
		PolynomialBI r = new PolynomialBI(this);

		//U.pf("divide r0 [%s]\n", r);
		int dr = r.degree();
		//U.pf("divide R deg:%d\n", dr);
		while (!r.isZero() && r.degree() >= b.degree()) {
			//int coef = r.coeff() / b.coeff();
			
			BigInteger coef = r.coeff();
			coef = coef.divide(b.coeff());
			int deg = r.degree() - b.degree();
			
			//String cs =  coef.toString();
			//String rs = r.coeff().toString();
			//String bs = b.coeff().toString();
			
			//U.pf("divide coef:%s [%s / %s] \n", cs, rs, bs);		
			//U.pf("divide deg:%d [%d - %d] \n", deg, r.degree(), b.degree());
			
			PolynomialBI t = new PolynomialBI(coef, deg);
			q = q.plus(t);
			//U.pf("divide q [%s]\n", q);
			PolynomialBI tmpp = t.times(b);
			//U.pf("divide tmpp [%s]\n", tmpp);
			//U.pf("divide r1 [%s]\n", r);
			r = r.minus(tmpp);
			//U.pf("divide r2 [%s]\n", r);
			
			//boolean rz = r.isZero();
			//dr = r.degree();
			//int db = b.degree();
			
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
		return new PolynomialBI[] { q, r };
	}

	// return a(b(x)) - compute using Horner's method
	public PolynomialBI compose(PolynomialBI b)
	{
		PolynomialBI a = this;
		PolynomialBI c = new PolynomialBI("0", "0");
		for (int i = a.deg; i >= 0; i--) {
			PolynomialBI term = new PolynomialBI(a.coef[i], 0);
			c = term.plus(b.times(c));
		}
		return c;
	}

	// do a and b represent the same polynomial?
	public boolean eq(PolynomialBI b)
	{
		PolynomialBI a = this;
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
			X[i] = _create_bi_needed_(Integer.toString(x[i]));
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
		BigInteger X = _create_bi_needed_(Integer.toString(x));
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

	// differentiate this polynomial and return it
	public PolynomialBI differentiate()
	{
		if (deg == 0)
			return new PolynomialBI("0", "0");
		
		
		int dd = deg - 1;
		
		PolynomialBI deriv = new PolynomialBI("0", Integer.toString(dd));
		deriv.deg = deg - 1;
		for (int i = 0; i < deg; i++) {
			//deriv.coef[i] = (i + 1) * coef[i + 1];
			BigInteger tmp = _create_bi_needed_(Integer.toString(i + 1));			
			BigInteger tmp2 = tmp.multiply(coef[i + 1]);
			deriv.coef[i] = tmp2;		
		}
		return deriv;
	}
	// convert to string representation
	public String toString()
	{
		if (deg == 0) {
			BigInteger b = _create_bi_needed_(coef[0].toString());
			return "" + b;
		}
		if (deg == 1) {
			BigInteger b1 = _create_bi_needed_(coef[1].toString());
			BigInteger b0 = _create_bi_needed_(coef[0].toString());
			return b1 + "x + " + b0;
		}
		BigInteger bb = _create_bi_needed_(coef[deg].toString());
		String s = bb + "x^" + deg;
		for (int i = deg - 1; i >= 0; i--) {
			//if (coef[i] == 0) {
			if (coef[i].compareTo(BigInteger.ZERO) == 0) {
				continue;
			//} else if (coef[i] > 0) {
/* Compare a and b, return 0 if a==b, -1 if a<b, +1 if a>b. Inputs must be normalised */
			} else if (coef[i].compareTo(BigInteger.ZERO) == 1) {
				BigInteger b = _create_bi_needed_(coef[i].toString());	
				s = s + " + " + (b);
			//} else if (coef[i] < 0)
			} else if (coef[i].compareTo(BigInteger.ZERO) == -1) {
				BigInteger b = _create_bi_needed_(coef[i].toString());
				s = s + " " +  (b);
			}
			if (i == 1) {
				s = s + "x";
			} else if (i > 1)
				s = s + "x^" + i;
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
	public static PolynomialBI createFrom(
			int[] inP)
	{
		BigInteger []p = new BigInteger[inP.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = BIGUtil.createBI(Integer.toString(inP[i]));
		}
		return createFrom(p);
	}
	/**
	 * 
	 * @param inP
	 * @return
	 */
	public static PolynomialBI createFrom(BigInteger[] inP)
	{
		PolynomialBI rval = null;

		for (int i = 0; i < inP.length; i++) {
			PolynomialBI t = new PolynomialBI(inP[i], i);
			if (rval == null) {
				rval = t;
			} else {
				
				rval = rval.plus(t);
				//U.pf("\t\trval [%s] t [%s]\n", rval, t);
			}
		}
		return rval;
	}

	// test client
	public static void main(String[] args)
	{
		int[] pp = { 4, 2, 6, 4, 0, 17 };
		PolynomialBI nh = PolynomialBI.createFrom(pp);
		System.out.println("nh(x) =     " + nh);

		BigInteger []coefs = nh.coefs();
		
		int test = 67;
	
		int []f0 = { 0, 3, 1 };
		
	}
	/**
	 * 
	 * @param inPx
	 * @param inR
	 * @return
	 */
	public static PolynomialBI createQx(
			int []inPx, 
			BigInteger inR) throws Exception
	{
		BigInteger []p = new BigInteger[inPx.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = BIGUtil.createBI(Integer.toString(inPx[i]));
		}
		return createQx(p, inR);
	}
	/**
	 * 
	 * @param inPx
	 * @param inR
	 * @return
	 */
	public static PolynomialBI createQx(
			BigInteger []inPx, 
			BigInteger inR) throws Exception
	{		
		PolynomialBI fX = PolynomialBI.createFrom(inPx);	
		
		//System.out.println("createQx fX(x) =     " + fX);
		BigInteger eval = fX.evaluate(inR);
		
		//U.pf("createQx eval:%s\n", eval);
		
		BigInteger []pp = inPx.clone();
		pp[0] = pp[0].subtract(eval); 
		
//		for (int i = 0; i < pp.length; i++) {
//			U.pf("createQx [%d] pp:%s\n", i, pp[i]);
//		}
		
		PolynomialBI pX = PolynomialBI.createFrom(pp);
		System.out.println("createQx pX(x) =     " + pX);
		
		BigInteger zero = BIGUtil.createBI("0");
		BigInteger mR = zero.subtract(inR);
		BigInteger[] qp = { mR, BIGUtil.createBI("1") };
		PolynomialBI qpx = PolynomialBI.createFrom(qp);
		System.out.println("createQx qpx(x) =     " + qpx);
		
		int pxd = pX.degree();
		//U.pf("createQx px degree:%d\n", pxd);
		PolynomialBI[] qx = pX.divides(qpx);
		
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
		PolynomialBI rval = qx[0];
		return rval;
	}

	private static void toto() throws Exception
	{
		//BIG mod = new BIG(337);
		PolynomialBI zero = new PolynomialBI("0", "0");

		PolynomialBI p1 = new PolynomialBI("1", "3");
		PolynomialBI p2 = new PolynomialBI("2", "2");
		PolynomialBI p3 = new PolynomialBI("4", "0");
		PolynomialBI p4 = new PolynomialBI("0", "1");
		PolynomialBI p = p1.plus(p2).plus(p3).plus(p4); // 4x^3 + 3x^2 + 1

		PolynomialBI q1 = new PolynomialBI("1", "1");
		PolynomialBI q2 = new PolynomialBI("3", "0");
		PolynomialBI q = q1.plus(q2); // 3x^2 + 5

		PolynomialBI r = p.plus(q);
		PolynomialBI s = p.times(q);
		PolynomialBI t = p.compose(q);

		System.out.println("zero(x) =     " + zero);
		System.out.println("p(x) =        " + p);
		System.out.println("q(x) =        " + q);
		System.out.println("p(x) + q(x) = " + r);
		System.out.println("p(x) * q(x) = " + s);
		System.out.println("p(q(x))     = " + t);
		System.out.println("0 - p(x)    = " + zero.minus(p));
		System.out.println("p(3)        = " + p.evaluate(3));
		System.out.println("p'(x)       = " + p.differentiate());
		System.out.println("p''(x)      = " + p.differentiate().differentiate());

		p.divides(q);
	}
	/**
	 * 
	 * @param p
	 * @param rs
	 * @param vals
	 * @return
	 */
	public static PolynomialBI createIX(
			int[] inX, 
			int[] inY,
			String inMod) throws Exception
	{
		BigInteger mod = new BigInteger(inMod);
		LagrangeInterpolationBIFP tmp = new LagrangeInterpolationBIFP(mod);
		BigInteger []p = tmp.interpolate(inX, inY, mod, true);
		PolynomialBI rval = PolynomialBI.createFrom(p); 
		return rval;
	}
	/**
	 * 
	 * @param p
	 * @param rs
	 * @param vals
	 * @return
	 */
	public static PolynomialBI createZX(
			int[] inX,
			String inMod)
	{
		PolynomialBI rval = null;
		for (int i = 0; i < inX.length; i++) {		
			int z = inX[i];
			
			int []tmp = {-z, 1};
			
			PolynomialBI tmpp = PolynomialBI.createFrom(tmp);
			if (rval == null) {
				rval = tmpp;
			} else {
				rval = rval.times(tmpp);
			}
		}
		return rval;
	}
}
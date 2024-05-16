package io.nh.poly;

import io.nh.U;

//see http://introcs.cs.princeton.edu/java/92symbolic/Polynomial.java.html 

public class Polynomial {
	private int[] coef; // coefficients
	private int deg; // degree of polynomial (0 for the zero polynomial)

	// a * x^b
	public Polynomial(int a, int b) {
		coef = new int[b + 1];
		coef[b] = a;
		deg = degree();
	}
	/**
	 * 
	 */
	public Polynomial()
	{
		coef = null;
		deg = -1;
		
	}
	public Polynomial(Polynomial p) {
		coef = new int[p.coef.length];
		for (int i = 0; i < p.coef.length; i++) {
			coef[i] = p.coef[i];
		} // end for
		deg = p.degree();
	}

	// return the degree of this polynomial (0 for the zero polynomial)
	public int degree()
	{
		int d = 0;
		for (int i = 0; i < coef.length; i++)
			if (coef[i] != 0)
				d = i;
		return d;
	}
	private void _o_()
	{
		for (int i = 0; i < this.coef.length;  i++) {
			U.pf(" c[%d] [%d]\n", i, coef[i]);
		}
	}
	// return c = a + b
	public Polynomial plus(Polynomial b)
	{
		Polynomial a = this;
		Polynomial c = new Polynomial(0, Math.max(a.deg, b.deg));
		for (int i = 0; i <= a.deg; i++) {
			c.coef[i] += a.coef[i];
			//c._o_();
		}
		for (int i = 0; i <= b.deg; i++) {
			c.coef[i] += b.coef[i];
			//c._o_();
		}
		c.deg = c.degree();
		//U.pf("\t\t\tc2 [%s]\n", c);
		return c;
	}

	// return (a - b)
	public Polynomial minus(Polynomial b)
	{
		Polynomial a = this;
		Polynomial c = new Polynomial(0, Math.max(a.deg, b.deg));
		for (int i = 0; i <= a.deg; i++)
			c.coef[i] += a.coef[i];
		for (int i = 0; i <= b.deg; i++)
			c.coef[i] -= b.coef[i];
		c.deg = c.degree();
		return c;
	}

	// return (a * b)
	public Polynomial times(Polynomial b)
	{
		Polynomial a = this;
		Polynomial c = new Polynomial(0, a.deg + b.deg);
		for (int i = 0; i <= a.deg; i++)
			for (int j = 0; j <= b.deg; j++)
				c.coef[i + j] += (a.coef[i] * b.coef[j]);
		c.deg = c.degree();
		return c;
	}

	// get the coefficient for the highest degree
	public int coeff()
	{
		return coeff(degree());
	}

	// get the coefficient for degree d
	public int coeff(int degree)
	{
		if (degree > this.degree())
			throw new RuntimeException("bad degree");
		return coef[degree];
	}

	/*
	 * Implement the division as described in wikipedia function n / d: require d ≠
	 * 0 q ← 0 r ← n # At each step n = d × q + r while r ≠ 0 AND degree(r) ≥
	 * degree(d): t ← lead(r)/lead(d) # Divide the leading terms q ← q + t r ← r − t
	 * * d return (q, r)
	 */
	public Polynomial[] divides(Polynomial b) throws Exception
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
	public Polynomial[] divides(
			Polynomial b, 
			boolean inNoRemainderCheck) throws Exception
	{
		Polynomial q = new Polynomial(0, 0);
		Polynomial r = new Polynomial(this);
		//U.pf("divide R0 [%s]\n", r);
		int dr = r.degree();
		//U.pf("divide R deg:%d\n", dr);
		while (!r.isZero() && r.degree() >= b.degree()) {
			int coef = r.coeff() / b.coeff();
			int deg = r.degree() - b.degree();
			
			//U.pf("divide coef:%d [%d / %d] \n", coef, r.coeff(), b.coeff());
			//U.pf("divide deg:%d [%d - %d] \n", deg, r.degree(), b.degree());
			
			Polynomial t = new Polynomial(coef, deg);
			q = q.plus(t);
			//U.pf("divide q [%s]\n", q);
			Polynomial tmpp = t.times(b);
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
			int []remainC = r.coefs();
			for (int i = 0; i < remainC.length; i++) {
				if (remainC[i] != 0) {
					String m = U.f("remaider[%d] degree wrong:%d \n", i, remainC[i]);
					throw new Exception(m);
				}
			}
		}	
		//System.out.printf("(%s) / (%s): %s, %s", this, b, q, r);
		return new Polynomial[] { q, r };
	}

	// return a(b(x)) - compute using Horner's method
	public Polynomial compose(Polynomial b)
	{
		Polynomial a = this;
		Polynomial c = new Polynomial(0, 0);
		for (int i = a.deg; i >= 0; i--) {
			Polynomial term = new Polynomial(a.coef[i], 0);
			c = term.plus(b.times(c));
		}
		return c;
	}

	// do a and b represent the same polynomial?
	public boolean eq(Polynomial b)
	{
		Polynomial a = this;
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
		for (int i : coef) {
			if (i != 0)
				return false;
		} // end for
		return true;
	}
	// use Horner's method to compute and return the polynomial evaluated at x
	public int[] evaluate(int []x)
	{
		int []rval = new int[x.length];
		for (int i = 0; i < x.length; i++)
			rval[i] = evaluate(x[i]);
		return rval;
	}
	
	// use Horner's method to compute and return the polynomial evaluated at x
	public int evaluate(int x)
	{
		int p = 0;
		for (int i = deg; i >= 0; i--)
			p = coef[i] + (x * p);
		return p;
	}

	// differentiate this polynomial and return it
	public Polynomial differentiate()
	{
		if (deg == 0)
			return new Polynomial(0, 0);
		Polynomial deriv = new Polynomial(0, deg - 1);
		deriv.deg = deg - 1;
		for (int i = 0; i < deg; i++)
			deriv.coef[i] = (i + 1) * coef[i + 1];
		return deriv;
	}

	// convert to string representation
	public String toString()
	{
		if (deg == 0)
			return "" + coef[0];
		if (deg == 1)
			return coef[1] + "x + " + coef[0];
		String s = coef[deg] + "x^" + deg;
		for (int i = deg - 1; i >= 0; i--) {
			if (coef[i] == 0) {
				continue;
			} else if (coef[i] > 0) {
				s = s + " + " + (coef[i]);
			} else if (coef[i] < 0)
				s = s + " - " + (-coef[i]);
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
	public int[] coefs()
	{
		return this.coef;
	}
	/**
	 * 
	 * @param inP
	 * @return
	 */
	public static Polynomial createFrom(int[] inP)
	{
		Polynomial rval = null;

		for (int i = 0; i < inP.length; i++) {
			Polynomial t = new Polynomial(inP[i], i);
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
		Polynomial nh = Polynomial.createFrom(pp);
		System.out.println("nh(x) =     " + nh);

		int []coefs = nh.coefs();
		
		int test = 67;
	
		int []f0 = { 0, 3, 1 };
		
	}
	/**
	 * 
	 * @param inPx
	 * @param inR
	 * @return
	 */
	public static Polynomial createQx(
			int []inPx, 
			int inR) throws Exception
	{		
		Polynomial fX = Polynomial.createFrom(inPx);	
		//System.out.println("createQx fX(x) =     " + fX);
		int eval = fX.evaluate(inR);
		
		//U.pf("createQx eval:%d\n", eval);
		
		int []pp = inPx.clone();
		pp[0] -= eval; 
		
//		for (int i = 0; i < pp.length; i++) {
//			U.pf("createQx pp:%d %d\n", i, pp[i]);
//		}
		
		Polynomial pX = Polynomial.createFrom(pp);
		//System.out.println("createQx pX(x) =     " + pX);

		int[] qp = { -inR, 1 };
		Polynomial qpx = Polynomial.createFrom(qp);
		//System.out.println("createQx qpx(x) =     " + qpx);
		
		int pxd = pX.degree();
		//U.pf("createQx px degree:%d\n", pxd);
		
		Polynomial[] qx = pX.divides(qpx);
		System.out.println("createQx qx0(x) =     " + qx[0]);
		System.out.println("createQx qx1(x) =     " + qx[1]);
		
		// check no remainder
		int []remainC = qx[1].coefs();
		for (int i = 0; i < remainC.length; i++) {
			if (remainC[i] != 0) {
				String m = U.f("remaider[%d] degree wrong:%d \n", i, remainC[i]);
				throw new Exception(m);
			}
		}
		Polynomial rval = qx[0];
		return rval;
	}

	private static void toto() throws Exception
	{
		Polynomial zero = new Polynomial(0, 0);

		Polynomial p1 = new Polynomial(1, 3);
		Polynomial p2 = new Polynomial(2, 2);
		Polynomial p3 = new Polynomial(4, 0);
		Polynomial p4 = new Polynomial(0, 1);
		Polynomial p = p1.plus(p2).plus(p3).plus(p4); // 4x^3 + 3x^2 + 1

		Polynomial q1 = new Polynomial(1, 1);
		Polynomial q2 = new Polynomial(3, 0);
		Polynomial q = q1.plus(q2); // 3x^2 + 5

		Polynomial r = p.plus(q);
		Polynomial s = p.times(q);
		Polynomial t = p.compose(q);

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
	public static Polynomial createIX(
			int[] inX, 
			int[] inY) throws Exception
	{
		LagrangeInterpolation tmp = new LagrangeInterpolation();
		int []p = tmp.interpolate(inX, inY, -1, true);
		Polynomial rval = Polynomial.createFrom(p); 
		return rval;
	}
	/**
	 * 
	 * @param p
	 * @param rs
	 * @param vals
	 * @return
	 */
	public static Polynomial createZX(int[] inX)
	{
		Polynomial rval = null;
		for (int i = 0; i < inX.length; i++) {		
			int z = inX[i];
			
			int []tmp = {-z, 1};
			
			Polynomial tmpp = Polynomial.createFrom(tmp);
			if (rval == null) {
				rval = tmpp;
			} else {
				rval = rval.times(tmpp);
			}
		}
		return rval;
	}
}
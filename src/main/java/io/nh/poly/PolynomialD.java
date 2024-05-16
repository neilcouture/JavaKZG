package io.nh.poly;

import io.nh.U;

//see http://introcs.cs.princeton.edu/java/92symbolic/Polynomial.java.html 

public class PolynomialD {
	private double[] coef; // coefficients
	private int deg; // degree of polynomial (0 for the zero polynomial)

	// a * x^b
	public PolynomialD(double a, int b) {
		coef = new double[b + 1];
		coef[b] = a;
		deg = degree();
	}

	public PolynomialD(PolynomialD p) {
		coef = new double[p.coef.length];
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

	// return c = a + b
	public PolynomialD plus(PolynomialD b)
	{
		PolynomialD a = this;
		PolynomialD c = new PolynomialD(0, Math.max(a.deg, b.deg));
		for (int i = 0; i <= a.deg; i++)
			c.coef[i] += a.coef[i];
		for (int i = 0; i <= b.deg; i++)
			c.coef[i] += b.coef[i];
		c.deg = c.degree();
		return c;
	}

	// return (a - b)
	public PolynomialD minus(PolynomialD b)
	{
		PolynomialD a = this;
		PolynomialD c = new PolynomialD(0, Math.max(a.deg, b.deg));
		for (int i = 0; i <= a.deg; i++)
			c.coef[i] += a.coef[i];
		for (int i = 0; i <= b.deg; i++)
			c.coef[i] -= b.coef[i];
		c.deg = c.degree();
		return c;
	}

	// return (a * b)
	public PolynomialD times(PolynomialD b)
	{
		PolynomialD a = this;
		PolynomialD c = new PolynomialD(0, a.deg + b.deg);
		for (int i = 0; i <= a.deg; i++)
			for (int j = 0; j <= b.deg; j++)
				c.coef[i + j] += (a.coef[i] * b.coef[j]);
		c.deg = c.degree();
		return c;
	}

	// get the coefficient for the highest degree
	public double coeff()
	{
		return coeff(degree());
	}

	// get the coefficient for degree d
	public double coeff(int degree)
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
	public PolynomialD[] divides(PolynomialD b)
	{
		PolynomialD q = new PolynomialD(0, 0);
		PolynomialD r = new PolynomialD(this);
		while (!r.isZero() && r.degree() >= b.degree()) {
			double coef = r.coeff() / b.coeff();
			int deg = r.degree() - b.degree();
			PolynomialD t = new PolynomialD(coef, deg);
			q = q.plus(t);
			r = r.minus(t.times(b));
		} // end while

		//System.out.printf("(%s) / (%s): %s, %s", this, b, q, r);
		return new PolynomialD[] { q, r };
	}

	// return a(b(x)) - compute using Horner's method
	public PolynomialD compose(PolynomialD b)
	{
		PolynomialD a = this;
		PolynomialD c = new PolynomialD(0, 0);
		for (int i = a.deg; i >= 0; i--) {
			PolynomialD term = new PolynomialD(a.coef[i], 0);
			c = term.plus(b.times(c));
		}
		return c;
	}

	// do a and b represent the same polynomial?
	public boolean eq(PolynomialD b)
	{
		PolynomialD a = this;
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
		for (double i : coef) {
			if (i != 0)
				return false;
		} // end for
		return true;
	}

	// use Horner's method to compute and return the polynomial evaluated at x
	public double evaluate(int x)
	{
		double p = 0;
		for (int i = deg; i >= 0; i--)
			p = coef[i] + (x * p);
		return p;
	}

	// differentiate this polynomial and return it
	public PolynomialD differentiate()
	{
		if (deg == 0)
			return new PolynomialD(0, 0);
		PolynomialD deriv = new PolynomialD(0, deg - 1);
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
		
		double f = coef[deg];
		String s;
		if (f == 1.0) {
			s = "x^" + deg;
		} else {
			s = coef[deg] + "x^" + deg;
		}
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
	public double[] coefs()
	{
		return this.coef;
	}
	/**
	 * 
	 * @param inP
	 * @return
	 */
	public static PolynomialD createFrom(double[] inP)
	{
		PolynomialD rval = null;

		for (int i = 0; i < inP.length; i++) {
			PolynomialD t = new PolynomialD(inP[i], i);
			if (rval == null) {
				rval = t;
			} else {
				rval = rval.plus(t);
			}
		}
		return rval;
	}

	// test client
	public static void main(String[] args)
	{
		double[] pp = { 4d, 2d, 6d, 4d, 0d, 17d };
		PolynomialD nh = PolynomialD.createFrom(pp);
		System.out.println("nh(x) =     " + nh);

		double []coefs = nh.coefs();
		
		int test = 67;
	
		int []f0 = { 0, 3, 1 };
		
	}
	/**
	 * 
	 * @param inPx
	 * @param inR
	 * @return
	 */
	public static PolynomialD createQx(
			double []inPx, 
			int inR) throws Exception
	{		
		PolynomialD fX = PolynomialD.createFrom(inPx);	
		double eval = fX.evaluate(inR);
		
		double []pp = inPx.clone();
		pp[0] -= eval; 
		PolynomialD pX = PolynomialD.createFrom(pp);
		System.out.println("pX(x) =     " + pX);

		double[] qp = { -inR, 1d };
		PolynomialD qpx = PolynomialD.createFrom(qp);
		System.out.println("qpx(x) =     " + qpx);
				
		PolynomialD[] qx = pX.divides(qpx);
		System.out.println("qx0(x) =     " + qx[0]);
		System.out.println("qx1(x) =     " + qx[1]);
		
		// check no remainder
		double []remainC = qx[1].coefs();
		for (int i = 0; i < remainC.length; i++) {
			if (remainC[i] != 0) {
				String m = U.f("remaider[%d] degree wrong:%d \n", i, remainC[i]);
				throw new Exception(m);
			}
		}
		PolynomialD rval = qx[0];
		return rval;
	}

	private static void toto()
	{
		PolynomialD zero = new PolynomialD(0, 0);

		PolynomialD p1 = new PolynomialD(1, 3);
		PolynomialD p2 = new PolynomialD(2, 2);
		PolynomialD p3 = new PolynomialD(4, 0);
		PolynomialD p4 = new PolynomialD(0, 1);
		PolynomialD p = p1.plus(p2).plus(p3).plus(p4); // 4x^3 + 3x^2 + 1

		PolynomialD q1 = new PolynomialD(1, 1);
		PolynomialD q2 = new PolynomialD(3, 0);
		PolynomialD q = q1.plus(q2); // 3x^2 + 5

		PolynomialD r = p.plus(q);
		PolynomialD s = p.times(q);
		PolynomialD t = p.compose(q);

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

}
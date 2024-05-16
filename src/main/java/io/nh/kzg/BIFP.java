package io.nh.kzg;

import java.math.BigInteger;

/**
 * BigInteger with Finite Field Support
 * 
 * @author neil
 *
 */
public class BIFP extends BigInteger {

	private static final long serialVersionUID = 1L;

	private String mModString = null;
	private BigInteger mMod = null;
	private String mNumString = null;
	
	private BigInteger mPm2 = null;
	
	/**
	 * 
	 * @param inS
	 */
	public BIFP(String inS, BigInteger inMod)
	{
		super(inS);
		mMod = new BigInteger(inMod.toString());
		mModString = inMod.toString();
		mNumString = inS;
		
		mPm2 = mMod.subtract(BigInteger.TWO);
	}
	/**
	 * 
	 */
	@Override 
	public BIFP clone()
	{
		BIFP rval = new BIFP(mNumString, mMod);
		return rval;
	}
	/**
	 * 
	 * @return
	 */
	public String getModString()
	{
		return mModString;
	}
	/*
	 * 
	 */
	@Override 
	public BigInteger add(BigInteger b)
	{
		BigInteger tmp = super.add(b);
		tmp = tmp.mod(mMod);
		BIFP rval = new BIFP(tmp.toString(), this.mMod);
		return rval;
	}
	/*
	 * 
	 */
	@Override 
	public BigInteger subtract(BigInteger b)
	{
		BigInteger tmp = super.subtract(b);
		tmp = tmp.mod(mMod);
		BIFP rval = new BIFP(tmp.toString(), this.mMod);
		return rval;
	}
	/*
	 * 
	 */
	@Override 
	public BigInteger multiply(BigInteger b)
	{
		BigInteger tmp = super.multiply(b);
		tmp = tmp.mod(mMod);
		BIFP rval = new BIFP(tmp.toString(), this.mMod);
		return rval;
	}
	/**
	 * Division in a finite field:
	 * 
	 * 		m / n mod p := m * n^(p-2) mod p
	 */
	@Override 
	public BigInteger divide(BigInteger b)
	{
		// m / n mod p = m * n^(p-2) mod p
		
		BigInteger tmp = b.modPow(mPm2, mMod);
		
		BigInteger tmp2 = this.multiply(tmp);
		
		BIFP rval = new BIFP(tmp2.toString(), this.mMod);
		return rval;
	}
}

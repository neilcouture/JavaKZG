package io.nh;

import org.apache.milagro.amcl.RAND;

public class U {
	private static RAND sRAND = null;
	/**
	 * 
	 * @param f
	 * @param args
	 */
	public static void err(String f) 
	{
		System.err.println(f);
	}
	/**
	 * 
	 * @param f
	 * @param args
	 */
	public static void pf(String f, Object ... args) 
	{
		System.out.printf(f, args);
	}
	/**
	 * 
	 * @param f
	 * @param args
	 */
	public static String f(String f, Object ... args) 
	{
		String rval = String.format(f, args);
		return rval;
	}
	/**
	 * 
	 * @return
	 */
	synchronized 
	public static RAND getRAND()
	{
		if (sRAND == null) {
			int rawlen = 128;
			byte []b = new byte[rawlen];
			for (int i = 0; i < rawlen; i++) {
				b[i] = (byte)i;
			}
			sRAND = new RAND();
			sRAND.seed(rawlen, b);
		}
		return sRAND;
	}
}

package test.old;
import org.apache.milagro.amcl.BLS381.BIG;

import io.nh.U;

public class TestBIGDivideMod {
	
	/**
	 * 
	 * @param inA
	 */
	public static void main(String []inA)
	{
		_shouldbe_();
		_shouldbe8_t2_();
	}
	/**
	 * 
	 */
	private static void _shouldbe8_t2_()
	{
		BIG b4 = new BIG(4);
		BIG b11 = new BIG(11);
		BIG b6 = new BIG(6);
		
		
		U.pf("b4 [%s]\n", b4.toString());
		U.pf("b6 [%s]\n", b6.toString());
		U.pf("b11 [%s]\n", b11.toString());
		
		//BIG res = b4.dividemod(b6, b11);		
		//U.pf("res [%s]\n", res.toString());
		
		
		BIG tmp1 = new BIG(b6);
		BIG tmp2 = new BIG(b4);
		tmp1.invmodp(b11); //.dividemod(b6, b11);
		
		BIG res2 = BIG.modmul(tmp2, tmp1, b11);
		U.pf("res2 [%s]\n", res2.toString());
		
		
	}
	/**
	 * 
	 */
	private static void _shouldbe_()
	{
		BIG b4 = new BIG(4);
		BIG b6 = new BIG(6);
		
		
		U.pf("b4 [%s]\n", b4.toString());
		U.pf("b6 [%s]\n", b6.toString());
		
		BIG res = new BIG(b4);
		res.div(b6);		
		U.pf("4/6 [%s]\n", res.toString());
		b6.div(b4);
		U.pf("4/6 [%s]\n", b6.toString());
	}
}

package io.nh.kzg.blst;

import io.nh.U;
import supranational.blst.P1;
import supranational.blst.P2;
import supranational.blst.Scalar;

import java.math.BigInteger;

public class BLSTUtil {

    /**
     *
     * @param inS
     * @return
     */
    public static String ss(Scalar inS)
    {
        byte []b = inS.to_bendian();
        BigInteger bi = new BigInteger(b);

        return U.f("%s",bi.toString());
    }

    /**
     *
     * @param inS
     * @return
     */
    public static String ss16(Scalar inS)
    {
        byte []b = inS.to_bendian();
        BigInteger bi = new BigInteger(b);

        return U.f("%s",bi.toString(16));
    }

    /**
     *
     * @param inb
     * @param inCtx
     */
    public static void outP1(byte []inb, String inCtx)
    {
        byte []b = inb;

        byte []bufx = new byte[48];
        byte []bufy = new byte[48];

        for (int i = 0; i < 48; i++) {
            bufx[i] = (byte)b[i];
            bufy[i] = (byte)b[48 + i];
        }

        BigInteger p1x = new BigInteger(bufx);
        BigInteger p1y = new BigInteger(bufy);

        U.pf("%s [%s, %s]\n",
                inCtx,
                p1x.toString(16),
                p1y.toString(16));
    }

    /**
     *
     * @param inb
     * @param inCtx
     */
    public static void outP2(byte []inb, String inCtx)
    {
        byte []b = inb;

        byte []bufx = new byte[48];
        byte []bufy = new byte[48];

        for (int i = 0; i < 48; i++) {
            bufx[i] = (byte)b[i];
            bufy[i] = (byte)b[48 + i];
        }

        BigInteger p1x = new BigInteger(bufx);
        BigInteger p1y = new BigInteger(bufy);

        U.pf("%s [%s, %s]\n",
                inCtx,
                p1x.toString(16),
                p1y.toString(16));
    }

    /**
     *
     * @param inA
     * @param inB
     * @return
     */
    final public static P1 sub(P1 inA, P1 inB)
    {
        P1 tmp = inB.dup().cneg(true);
        P1 rval = inA.add(tmp);
        return rval;
    }

    /**
     *
     * @param inA
     * @param inB
     * @return
     */
    final public static P2 sub(P2 inA, P2 inB)
    {
        P2 tmp = inB.dup().cneg(true);
        P2 rval = inA.add(tmp);
        return rval;
    }
}

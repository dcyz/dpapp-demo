package rappor;

import java.util.Date;
import java.util.Random;

public class Rappor {
    private static double F = 0.5, P = 0.75, Q = 0.75;
    private byte[] data = null;
    private Random rand = null;

    public Rappor(int loc, int bitlen) {
        rand = new Random();
        rand.setSeed(new Date().getTime());
        rr(loc, bitlen);
    }

    public void rr(int loc, int bitlen) {
        int bytelen = (int) (Math.ceil(1.0 * bitlen / 8));
        data = new byte[bytelen];
        BitOps.SetBit1(data, loc);
        double r, p1 = 0.5 * F, p2 = F;
        boolean bt = false;
        for (int i = 0; i < bitlen; i++) {
            r = rand.nextDouble();
            if (r < p1) {
                bt = BitOps.SetBit1(data, i);
            } else if (r < p2) {
                bt = BitOps.SetBit0(data, i);
            }
            r = rand.nextDouble();
            if (bt) {
                if (r > Q) {
                    BitOps.SetBit0(data, i);
                } else {
                    if (r < P) {
                        BitOps.SetBit1(data, i);
                    }
                }
            }
        }
    }

    public static void setParams(double f, double p, double q) {
        Rappor.F = f;
        Rappor.P = p;
        Rappor.Q = q;
    }

    public byte[] getData() {
        return this.data;
    }

    public String[] getDataBinaryString() {
        return BitOps.bytesToBinaryString(this.data);
    }
}


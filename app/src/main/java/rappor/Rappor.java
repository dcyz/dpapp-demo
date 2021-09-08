package rappor;

import java.util.Date;
import java.util.Random;

public class Rappor {
    private static double F = 0.5, P = 0.75, Q = 0.75;
    private byte[] data = null;
    private Random rand = null;
    private int bitLen;

    public Rappor(int bitLen) {
        rand = new Random();
        rand.setSeed(new Date().getTime());
        int bytelen = (int) (Math.ceil(1.0 * bitLen / 8));
        data = new byte[bytelen];
    }

    public void setBit(int loc) {
        BitOps.SetBit1(data, loc);
    }

    public void rr() {
        double r, p1 = 0.5 * F, p2 = F;
        boolean bt = false;
        for (int i = 0; i < bitLen; i++) {
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


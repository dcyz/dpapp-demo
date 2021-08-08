package rappor;

public class BitOps {
    public static boolean SetBit1(byte[] b, int pos) {
        int outer = pos >> 3, inner = pos & 0x07;
        int mask = (byte) (1 << (7 - inner));
        int bt = b[outer] & mask;
        b[outer] |= mask;
        return !(bt == 0);
    }

    public static boolean SetBit0(byte[] b, int pos) {
        int outer = pos >> 3, inner = pos & 0x07;
        int mask = (byte) (1 << (7 - inner));
        int bt = b[outer] & mask;
        b[outer] &= (mask ^ 0xff);
        return !(bt == 0);
    }

    public static String[] bytesToBinaryString(byte[] data) {
        String[] str = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            str[i] = Integer.toBinaryString((int) data[i] & 0xff);
            int len = str[i].length();
            for (int j = 0; j < 8 - len; j++) {
                str[i] = "0" + str[i];
            }
        }
        return str;
    }
}
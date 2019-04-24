import java.util.Arrays;

public class CircularSuffixArray {
    private final int[] indexes;

    private static final class Suffix {

        private final int shift;

        public Suffix(int shift) {
            this.shift = shift;
        }


    }
    /** circular suffix array of s */
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        char[] chars = s.toCharArray();

        Suffix[] suffixes = new Suffix[chars.length];
        for (int i = 0; i < chars.length; i++) {
            suffixes[i] = new Suffix(i);
        }
        Arrays.parallelSort(suffixes, (s1, s2) -> {
            int offset1 = s1.shift;
            int offset2 = s2.shift;
            for (int i = 0; i < chars.length; i++) {
                int comp = Integer.compare(chars[offset1], chars[offset2]);
                if (comp != 0) {
                    return comp;
                }
                offset1 = (offset1 + 1) % chars.length;
                offset2 = (offset2 + 1) % chars.length;
            }
            return 0;
        });
        indexes = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            indexes[i] = suffixes[i].shift;
        }

    }

    /** length of s */
    public int length() {
        return indexes.length;
    }

    /** returns index of ith sorted suffix */
    public int index(int i) {
        try {
            return indexes[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** unit testing (required) */
    public static void main(String[] args) {
        CircularSuffixArray array = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < array.length(); i++) {
            System.out.println("index[" + i + "] = " + array.index(i));
        }
    }
}
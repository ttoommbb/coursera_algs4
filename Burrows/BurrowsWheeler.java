import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;

import java.util.Arrays;

public class BurrowsWheeler {
    /** apply Burrows-Wheeler transform, reading from standard input and writing to standard output */

    public static void transform() {
        transform(new SystemPipeline());
    }

    /**
     * apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard
     * output
     */

    public static void inverseTransform() {
        inverseTransform(new SystemPipeline());
    }

    private static void transform(IPipeline pipeline) {
        try {
            if (pipeline.isEmpty()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            do {
                sb.append(pipeline.readChar());
            } while (!pipeline.isEmpty());
            String string = sb.toString();
            CircularSuffixArray circularSuffixArray = new CircularSuffixArray(string);
            int length = circularSuffixArray.length();
            char[] endChars = new char[length];
            for (int n = 0; n < length; n++) {
                int indexN = circularSuffixArray.index(n);
                if (indexN == 0) {
                    pipeline.write(n);
                }
                int endIndex = (indexN + length - 1) % length;
                endChars[n] = string.charAt(endIndex);
                // pipeline.write(string.charAt(endIndex));
            }
            for (char endChar : endChars) {
                pipeline.write(endChar);
            }
        }
        finally {
            pipeline.close();
        }
    }

    private static void inverseTransform(IPipeline pipeline) {
        try {
            if (pipeline.isEmpty()) {
                return;
            }
            int next0 = pipeline.readInt();
            if (pipeline.isEmpty()) {
                return;
            }
            // charArrayWay(pipeline, next0);
            // treemapWay(pipeline, next0);
            int offset = 0;
            long[] values = new long[1024];
            do {
                if (offset == values.length) {
                    values = Arrays.copyOf(values, values.length << 1);
                }
                char ch = pipeline.readChar();

                // 0~255, 2^8 , signum 1, shift 64-8-1
                values[offset] = ((long) ch << 32) + offset++;
            } while (!pipeline.isEmpty());
            values = Arrays.copyOf(values, offset);
            Arrays.parallelSort(values);
            int nextN = next0;
            // long mask = (1 << 32) - 1;
            for (int i = 0; i < offset; i++) {
                long value = values[nextN];
                pipeline.write((char) (value >> 32));
                nextN = (int) value;
            }
        }
        finally {
            pipeline.close();
        }
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }
        String opr = args[0];
        switch (opr) {
            case "+":
                inverseTransform();
                return;
            case "-":
                transform();
        }
    }

    private static class BWTest {
        public static void main(String[] args) {
            StringPipeline str = new StringPipeline("ABRACADABRA!");
            transform(str);
            System.out.println("str.toString() = " + str.toString());
            for (char c : str.toString().toCharArray()) {
                System.out.println("c = " + c + ", idx =" + Integer.toHexString((int) c));
            }

            str = new StringPipeline(str.num, str.toString());
            inverseTransform(str);
            System.out.println("str.toString() = " + str.toString());
        }

    }

    private static class InverseTransformTest {
        public static void main(String[] args) {
            BinaryIn in = new BinaryIn("us.gif.bwt");
            BinaryOut out = new BinaryOut("us-out.gif");
            inverseTransform(new InOutPipeline(in, out));
            // diff us.gif us-out.gif should returns empty.
        }

    }

    private static class InOutPipeline implements IPipeline {
        private final BinaryIn in;
        private final BinaryOut out;

        public InOutPipeline(BinaryIn in, BinaryOut out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public char readChar() {
            return in.readChar();
        }

        @Override
        public int readInt() {
            return in.readInt();
        }

        @Override
        public void write(char ch) {
            out.write(ch);
        }

        @Override
        public void write(int num) {
            out.write(num);
        }

        @Override
        public boolean isEmpty() {
            return in.isEmpty();
        }

        @Override
        public void close() {
            out.close();
        }
    }
}
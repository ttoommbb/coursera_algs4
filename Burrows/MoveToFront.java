public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        encode(new SystemPipeline());
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    // 41 42 52 02 44 01 45 01 04 04 02 26 to
    // A  B  R  A  C  A  D  A  B  R  A  !
    public static void decode() {
        decode(new SystemPipeline());
    }

    private static void encode(IPipeline pipeline) {
        try {
            if (pipeline.isEmpty()) {
                return;
            }
            char firstChar = pipeline.readChar();
            pipeline.write(firstChar);
            if (pipeline.isEmpty()) {
                return;
            }
            char[] order = new char[256];
            for (char i = 0; i < order.length; i++) {
                order[i] = i;
            }
            System.arraycopy(order, 0, order, 1, firstChar);
            order[0] = firstChar;
            do {
                char nChar = pipeline.readChar();
                for (int i = 0; i < order.length; i++) {
                    if (order[i] == nChar) {
                        System.arraycopy(order, 0, order, 1, i);
                        order[0] = nChar;
                        pipeline.write((char) i);
                        break;
                    }
                }
            } while (!pipeline.isEmpty());
        } finally {
            pipeline.close();
        }
    }


    private static void decode(IPipeline pipeline) {
        try {
            if (pipeline.isEmpty()) {
                return;
            }
            char firstChar = pipeline.readChar();
            pipeline.write(firstChar);
            if (pipeline.isEmpty()) {
                return;
            }
            char[] order = new char[256];
            for (char i = 0; i < order.length; i++) {
                order[i] = i;
            }
            System.arraycopy(order, 0, order, 1, firstChar);
            order[0] = firstChar;
            do {
                char seq = pipeline.readChar();
                char nChar = order[seq];
                // if (order[nChar] == null) {
                //     pipeline.write(nChar);
                //     System.arraycopy(order, 0, order, 1, order.length - 1);
                //     // order[0] = nChar;
                //     // continue;
                // } else {
                pipeline.write(nChar);
                System.arraycopy(order, 0, order, 1, seq);
                // }
                order[0] = nChar;
            } while (!pipeline.isEmpty());
        } finally {
            pipeline.close();
        }
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }
        String opr = args[0];
        switch (opr) {
            case "+":
                decode();
                return;
            case "-":
                encode();
        }
    }

    private static class MoveToFontTest {
        public static void main(String[] args) {
            StringPipeline str = new StringPipeline("ABRACADABRA!");
            encode(str);
            System.out.println("str.toString() = " + str.toString());
            for (char c : str.toString().toCharArray()) {
                System.out.println("c = " + c +", idx =" + Integer.toHexString((int)c));
            }

            str = new StringPipeline(str.toString());
            decode(str);
            System.out.println("str.toString() = " + str.toString());
        }
    }
}

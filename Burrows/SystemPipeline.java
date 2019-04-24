/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class SystemPipeline implements IPipeline {
    @Override
    public char readChar() {
        return BinaryStdIn.readChar();
    }

    @Override
    public int readInt() {
        return BinaryStdIn.readInt();
    }

    @Override
    public void write(char ch) {
        BinaryStdOut.write(ch);
    }

    @Override
    public void write(int num) {
        BinaryStdOut.write(num);
    }

    @Override
    public boolean isEmpty() {
        boolean empty = BinaryStdIn.isEmpty();
        return empty;
    }

    @Override
    public void close() {
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

}

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

class StringPipeline implements IPipeline {

    private final String s;
    private int offset;
    private StringBuilder sb = new StringBuilder();
    int num;

    public StringPipeline(String s) {
        this.s = s;
    }

    public StringPipeline(int num, String string) {
        s= string;
        this.num = num;
    }

    @Override
    public char readChar() {
        return s.charAt(offset++);
    }

    @Override
    public int readInt() {
        return num;
    }

    @Override
    public void write(char ch) {
        sb.append(ch);
    }

    @Override
    public void write(int num) {
        this.num = num;
    }

    @Override
    public boolean isEmpty() {
        return s.length() == offset;
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }
}

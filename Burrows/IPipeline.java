/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public interface IPipeline {

    char readChar();
    int readInt();
    void write(char ch);
    void write(int num);
    boolean isEmpty();
    void close();
}

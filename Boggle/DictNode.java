/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.List;

/**
 * Special TrieSet.
 */
public class DictNode {

    private static final int R = 27;
    private static final int OFFSET_MASK = 0B11111; // A -> 1, Z -> 26.  0 reseved for word end.

    private DictNode[] next;
    // private final char[] chars;
    private final String string;
    private final int charOffset;

    public DictNode(String string, int charOffset) {
        this.string = string;
        this.charOffset = charOffset;
    }

    public void add(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        if (word.length() < 3) {
            return;
        }
        // char[] wordChars = word.toCharArray();
        DictNode node = this;
        for (int i = 0; i < word.length(); i++) {
            if (node.next == null) {
                node.next = new DictNode[R];
            }
            int nextOffset = word.charAt(i) & OFFSET_MASK;
            if (node.next[nextOffset] == null) {
                node.next[nextOffset] = new DictNode(word, i + 1);
            }
            node = node.next[nextOffset];
        }
    }

    @Override
    public String toString() {
        if (charOffset == string.length() && !string.isEmpty()) {
            return string + " !";
        }
        return string.substring(0, charOffset);
    }

    private void printWords(boolean detailed, int intent) {

        if (detailed || (charOffset == string.length() && !string.isEmpty())) {
            for (int i = 0; i < intent; i++) {
                System.out.print("  ");
            }
            System.out.println(this);
        }
        if (next != null) {
            for (DictNode dictNode : next) {
                if (dictNode != null) {
                    dictNode.printWords(detailed, intent + 1);
                }
            }
        }
    }

    private static String[] dictFromFile(String dictFile) {
        List<String> dict = new ArrayList<>();
        In in = new In(dictFile);
        String line;
        while ((line = in.readLine()) != null) {
            dict.add(line);
        }
        return dict.toArray(new String[0]);
    }

    /**
     * only works fine on root node.
     * @param word word must not be null and length >= 3
     */
    public boolean contains(String word) {
        DictNode node = this;
        for (char c : word.toCharArray()) {
            node = node.nextNode(c);
            if (node == null) {
                return false;
            }
        }
        return node.isWord();
    }

    public static void main(String[] args) {
        DictNode root = new DictNode("", 0);
        String[] words = dictFromFile("dictionary-algs4.txt");
        for (String word : words) {
            root.add(word);
        }

        root.printWords(false, 0);

        for (String word : words) {
            boolean contains = root.contains(word);
            System.out.println("word = " + word + ", contains = " + contains);
        }
    }

    public boolean isWord() {
        return charOffset == string.length();
    }

    public String getWord() {
        return string;
    }

    public DictNode nextNode(char ch) {
        if (next == null) {
            return null;
        }
        return next[ch & OFFSET_MASK];
    }
}

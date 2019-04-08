import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet)         // constructor takes a WordNet object
    {

        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {
        if (nouns == null) {
            throw new IllegalArgumentException();
        }
        int[] distSum = new int[nouns.length];
        for (int i = 0; i < nouns.length - 1; i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                int dist = wordnet.distance(nouns[i], nouns[j]);
                distSum[i] += dist;
                distSum[j] += dist;
            }
        }
        int maxIdx = -1;
        int maxDist = -1;
        for (int i = 0; i < distSum.length; i++) {
            if (distSum[i] > maxDist) {
                maxDist = distSum[i];
                maxIdx = i;
            }
        }

        return nouns[maxIdx];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

}
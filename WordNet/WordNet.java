import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.PatriciaST;
import edu.princeton.cs.algs4.TopologicalX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class WordNet {

    private final PatriciaST<Integer[]> st;
    private final String[] synsets;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        st = new PatriciaST<>();
        In synsetsIn = new In(synsets);

        int lastIdx = -1;
        try {
            ArrayList<String> synsetsList = new ArrayList<>();
            String line;
            while ((line = synsetsIn.readLine()) != null) {
                // System.out.println("line = " + line);
                String[] splits = line.split(",", 3);
                if (splits.length == 3) {

                    int idx = Integer.parseInt(splits[0]);
                    String synonym = splits[1];
                    // String gloss = splits[2];
                    addToSt(idx, synonym);

                    synsetsList.add(synonym);
                    lastIdx = idx;
                }
            }
            this.synsets = synsetsList.toArray(new String[synsetsList.size()]);
        }
        finally {
            synsetsIn.close();
        }

        In hypernymsIn = new In(hypernyms);
        Digraph digraph;
        try {

            digraph = new Digraph(lastIdx + 1);
            String line;

            while ((line = hypernymsIn.readLine()) != null) {
                String[] splits = line.split(",");
                if (splits.length >= 2) {
                    int id = Integer.parseInt(splits[0]);
                    for (int i = 1; i < splits.length; i++) {
                        int hypernymId = Integer.parseInt(splits[i]);
                        digraph.addEdge(id, hypernymId);
                    }
                }
            }

        }
        finally {
            hypernymsIn.close();
        }

        sap = new SAP(digraph);

        if (!new TopologicalX(digraph).hasOrder()) {
            throw new IllegalArgumentException("G has loop");
        }
        int countZeroOut = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) {
                countZeroOut++;
            }
        }
        if (countZeroOut != 1) {
            throw new IllegalArgumentException("G should have only 1 root");
        }
    }

    private void addToSt(int idx, String synonym) {
        if (!synonym.contains(" ")) {
            addToStEach(idx, synonym);
        }
        else {
            String[] splits = synonym.split(" ");
            for (String split : splits) {
                addToStEach(idx, split);
            }
        }
    }

    private void addToStEach(int idx, String synonym) {
        Integer[] existIds = st.get(synonym);
        if (existIds == null) {
            // st.put(synonym, idx);
            st.put(synonym, new Integer[] { idx });
        }
        else {

            Integer[] newArr = Arrays.copyOf(existIds,
                                             existIds.length + 1);
            newArr[newArr.length - 1] = idx;
            st.put(synonym, newArr);
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return st.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return st.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        // if (!isNoun(nounA) || !isNoun(nounB)) {
        //     throw new IllegalArgumentException();
        // }
        Integer[] keyA = st.get(nounA);
        Integer[] keyB = st.get(nounB);
        if (keyA == null || keyB == null) {
            throw new IllegalArgumentException();
        }

        // SAP sap = new SAP(digraph);
        return sap.length(toList(keyA), toList(keyB));
    }

    private Iterable<Integer> toList(Integer[] key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return Arrays.stream(key).collect(Collectors.toList());
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        // SAP sap = new SAP(digraph);
        int ancestor = sap.ancestor(toList(st.get(nounA)), toList(st.get(nounB)));
        if (ancestor == -1) {
            return null;
        }
        return synsets[ancestor];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        test1();
        // test2();
        // testFile("synsets6.txt", "hypernyms6TwoAncestors.txt", "a", "c", 4);
        //
        // testFile("synsets11.txt", "hypernyms11AmbiguousAncestor.txt", "a", "d", 4);
        //
        // // testFile("synsets8.txt", "hypernyms8ModTree.txt", "b", "c", 2);
        //
        // testFile("synsets8.txt", "hypernyms8WrongBFS.txt", "a", "d", 2);
        //
        // testFile("synsets11.txt", "hypernyms11ManyPathsOneAncestor.txt", "a", "g", 3);
        //
        // testFile("synsets8.txt", "hypernyms8ManyAncestors.txt", "a", "c", 2);
        testFile("synsets100-subgraph.txt", "hypernyms100-subgraph.txt",
                 "glutamic_oxaloacetic_transaminase", "NGF", 6);

    }

    private static void testFile(String f1, String f2, String n1, String n2, int expectDist) {
        WordNet wordnet = new WordNet(f1, f2);
        int distance = wordnet
                .distance(n1, n2);

        if (distance != expectDist) {
            System.out.println(
                    "f1 = [" + f1 + "], f2 = [" + f2 + "], n1 = [" + n1 + "], n2 = [" + n2
                            + "], expectDist = [" + expectDist + "]");
            System.out.println("distance = " + distance);
            // System.out.println("expectDist = " + expectDist);
        }
    }

    private static void test2() {
        WordNet wordnet = new WordNet("synsets15.txt", "hypernyms15Tree.txt");
        System.out.println("wordnet = " + wordnet);
        int distance = wordnet
                .distance("b", "c");
        System.out.println("distance = " + distance);
    }

    private static void test1() {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.println("wordnet = " + wordnet);
        int distance = wordnet
                .distance("genus_Liparis", "shore_boulder");
        System.out.println("distance = " + distance);
    }
}
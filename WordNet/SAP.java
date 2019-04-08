import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SAP {

    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("G is null");
        }


        // int countZeroOut = 0;
        this.digraph = new Digraph(G.V());
        for (int v = 0; v < G.V(); v++) {
            if (G.outdegree(v) == 0) {
                // countZeroOut++;
            }
            for (int w : G.adj(v)) {
                this.digraph.addEdge(v, w);
            }
        }
        // if (countZeroOut != 1) {
        //     throw new IllegalArgumentException("G should have only 1 root");
        // }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return length(Collections.singleton(v), Collections.singleton(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(Collections.singleton(v), Collections.singleton(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return computeMinData(v, w).minLengh;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return computeMinData(v, w).ancestor;
    }


    private MinData computeMinData(Iterable<Integer> v, Iterable<Integer> w) {
        Map<Integer, Integer> layerMapV = new HashMap<>();
        Map<Integer, Integer> layerMapW = new HashMap<>();

        Queue<Integer> curLayerV = initLayer0(v, layerMapV);
        Queue<Integer> curLayerW = initLayer0(w, layerMapW);
        return computeMinData(layerMapV, layerMapW, curLayerV, curLayerW);
    }

    private Queue<Integer> initLayer0(Iterable<Integer> v, Map<Integer, Integer> layerMapV) {
        if (v == null) {
            throw new IllegalArgumentException("v or w is null");
        }
        Queue<Integer> layer0 = new Queue<>();
        for (Integer numV : v) {
            if (numV == null) {
                throw new IllegalArgumentException("some value of V(or W) is null.");
            }
            layerMapV.put(numV, 0);
            layer0.enqueue(numV);
        }
        return layer0;
    }

    private MinData computeMinData(Map<Integer, Integer> layerMapV,
                                   Map<Integer, Integer> layerMapW,
                                   Queue<Integer> curLayerV, Queue<Integer> curLayerW) {

        // Queue<Integer> nextLayerV = curLayerV;
        // Queue<Integer> nextLayerW = curLayerW;
        int layer = 0;

        MinData data = new MinData(-1, -1);

        while (true) {
            if (curLayerV.isEmpty() && curLayerW.isEmpty()) {
                return data;
            }
            data = updateMinDataForLayer(layerMapV, layerMapW, curLayerV, data);
            data = updateMinDataForLayer(layerMapW, layerMapV, curLayerW, data);

            if (data.minLengh != -1 && layer >= data.minLengh) {
                // no need comput next layer, already got minLength.
                return data;
            }

            layer++;
            curLayerV = computNextLayer(layerMapV, curLayerV, layer);
            curLayerW = computNextLayer(layerMapW, curLayerW, layer);
        }
    }

    private MinData updateMinDataForLayer(Map<Integer, Integer> layerMapV,
                                          Map<Integer, Integer> layerMapW,
                                          Queue<Integer> curLayerV,
                                          MinData data) {
        for (int eachV : curLayerV) {
            Integer lengthLayerW = layerMapW.get(eachV);
            if (lengthLayerW != null) {
                int lengthLayerV = layerMapV.get(eachV);
                int length = lengthLayerW + lengthLayerV;
                if (data.minLengh == -1 || length < data.minLengh) {
                    // foundMinLength = length;
                    // foundMinAncestor = eachV;
                    data = new MinData(length, eachV);
                }
            }
        }
        return data;
    }

    private Queue<Integer> computNextLayer(Map<Integer, Integer> numLayerV,
                                           Queue<Integer> curLayerV, int layer) {
        Queue<Integer> nextLayerV = new Queue<>();
        for (int curV : curLayerV) {
            for (int adj : digraph.adj(curV)) {
                Integer existLayer = numLayerV.get(adj);
                if (existLayer == null) {
                    numLayerV.put(adj, layer);
                    nextLayerV.enqueue(adj);
                }
            }
        }
        return nextLayerV;
    }

    private static class MinData {

        private final int minLengh;
        private final int ancestor;

        private MinData(int minLengh, int ancestor) {
            this.minLengh = minLengh;
            this.ancestor = ancestor;
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In("digraph5.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

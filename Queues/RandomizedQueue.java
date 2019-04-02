import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private static final int INIT_SIZE = 12;
    private static final int MAX_ARRAY_SIZE = 65536;
    private Item[][] items;
    private int size = 0;


    /** construct an empty randomized queue */
    public RandomizedQueue() {
        items = (Item[][]) new Object[1][INIT_SIZE];
    }

    /** is the randomized queue empty? */
    public boolean isEmpty() {
        return size == 0;
    }

    /** return the number of items on the randomized queue */
    public int size() {
        return size;
    }

    /** add the item */
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (size == items[items.length - 1].length + (items.length - 1) * MAX_ARRAY_SIZE) {
            if (items[items.length - 1].length == MAX_ARRAY_SIZE) {
                items = Arrays.copyOf(items, items.length + 1);
                items[items.length - 1] = (Item[]) new Object[INIT_SIZE];
            }
            else {
                items[items.length - 1] = Arrays.copyOf(items[items.length - 1],
                                                        Math.min(MAX_ARRAY_SIZE,
                                                                 items[items.length - 1].length
                                                                         << 1));
            }
            // items = Arrays.copyOf(items, items.length << 1);
        }
        items[size / MAX_ARRAY_SIZE][size % MAX_ARRAY_SIZE] = item;
        size++;
    }

    /** remove and return a random item */
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int index = StdRandom.uniform(size);
        int lastIndex = size - 1;

        Item removed = items[index / MAX_ARRAY_SIZE][index % MAX_ARRAY_SIZE];
        items[index / MAX_ARRAY_SIZE][index % MAX_ARRAY_SIZE] =
                items[lastIndex / MAX_ARRAY_SIZE][lastIndex % MAX_ARRAY_SIZE];
        items[lastIndex / MAX_ARRAY_SIZE][lastIndex % MAX_ARRAY_SIZE] = null;
        size--;

        if (items.length > 1) {
            if (size < MAX_ARRAY_SIZE * (items.length - 1) - INIT_SIZE) {
                items = Arrays.copyOf(items, items.length - 1);
            }
        }
        else {
            if (size * 3 < items[0].length && items[0].length > INIT_SIZE) {
                items[0] = Arrays.copyOf(items[0], items[0].length >> 1);
            }
        }
        // if (size * 3 < items.length) {
        //     items = Arrays.copyOf(items, items.length >> 1);
        // }
        return removed;
    }

    /** return a random item (but do not remove it) */
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int offset = StdRandom.uniform(size);
        return items[offset / MAX_ARRAY_SIZE][offset % MAX_ARRAY_SIZE];
    }

    /** return an independent iterator over items in random order */
    public java.util.Iterator<Item> iterator() {
        return new Iterator();
    }

    private class Iterator implements java.util.Iterator<Item> {
        private int[] offsets;

        {
            offsets = new int[size];
            for (int i = 0; i < size; i++) {
                offsets[i] = i;
            }
            StdRandom.shuffle(offsets);
        }

        private int visited = 0;

        @Override
        public boolean hasNext() {
            return visited != size;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int offset = offsets[visited++];
            return items[offset / MAX_ARRAY_SIZE][offset % MAX_ARRAY_SIZE];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> randomizedQueue = new RandomizedQueue<>();
        for (int i = 0; i < 3; i++) {
            randomizedQueue.enqueue(i);
        }
        int[] counts = new int[10];
        for (int i = 0; i < 10000; i++) {
            int num = randomizedQueue.dequeue();
            randomizedQueue.enqueue(num);
            counts[num]++;
        }
        System.out.println("Arrays.toString(counts) = " + Arrays.toString(counts));


        // for (int num : randomizedQueue) {
        //     System.out.println("num = " + num);
        // }
        // System.out.println("randomizedQueue = " + randomizedQueue);
        // for (int num : randomizedQueue) {
        //     System.out.println("num = " + num);
        // }
    }
}

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Permutation {
    public static void main(String[] args) {
        int count = Integer.parseInt(args[0]);
        Iterable<String> iterable = runWith(count, new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return !StdIn.isEmpty();
            }

            @Override
            public String next() {
                try {
                    return StdIn.readString();
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("no next in iterable");
                }
            }
        });

        for (String s : iterable) {
            System.out.println(s);
        }
    }

    private static <T> Iterable<T> runWith(int k, Iterator<T> items) {
        RandomizedQueue<T> queue = new RandomizedQueue<>();
        if (k == 0) {
            return queue;
        }
        int nth = 1;
        while (items.hasNext()) {
            T item = items.next();
            int overflowCount = nth - k;
            if (overflowCount <= 0) {
                queue.enqueue(item);
            }
            else {
                //eg: k=3, nth=5,[ABD]C E?,ram[01234],[012] replace
                if (StdRandom.uniform(nth) < k) {
                    queue.dequeue();
                    queue.enqueue(item);
                }
            }
            nth++;
        }
        return queue;
    }
}

//1,2,3,4,5 6 1/6
//1,2,3,4,5,6 1/6 missing everyone.
// 1/7 missing 7. 6/7 replace 1~6, means 1/7replace 1. 1/7 replace 6.

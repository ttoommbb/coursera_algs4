/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Throw a java.lang.IllegalArgumentException if the client calls either addFirst() or addLast()
 * with a null argument. Throw a java.util.NoSuchElementException if the client calls either
 * removeFirst() or removeLast when the deque is empty. Throw a java.util.NoSuchElementException if
 * the client calls the next() method in the iterator when there are no more items to return. Throw
 * a java.lang.UnsupportedOperationException if the client calls the remove() method in the
 * iterator.
 *
 * @param <Item>
 */
public class Deque<Item> implements Iterable<Item> {


    private static final int INIT_SIZE = 12;
    private static final int MAX_ARRAY_SIZE = 65536;
    private Item[][] items;
    private int front = INIT_SIZE / 2;
    // point to first null offset[012345^789abc] front from 0 to c.
    private int size = 0;
    private int totalLength = INIT_SIZE; // size of all cells.
    /** space mor thant it do narrow for totalLength >= 65536*4. */
    private final static int BIG_NARROW_SPACE = MAX_ARRAY_SIZE * 2 - 1;
    private final static int MEDIAN_NARROW_SPACE = MAX_ARRAY_SIZE + INIT_SIZE;
    // font <= end !

    /** construct an empty deque */
    public Deque() {
        items = (Item[][]) new Object[1][INIT_SIZE];
    }

    /** is the deque empty? */
    public boolean isEmpty() {
        return size == 0;
    }

    /** return the number of items on the deque */
    public int size() {
        return size;
    }

    /**
     * add the item to the front
     * <pre>
     * 01234567
     * xxxxx___ f-1=-1,
     * ^
     * f
     * 01234567
     * xx___xxx  f+size=10, %total=2.
     *      ^
     *      f
     *
     * 01234567
     * xxxx___x  f+size=12, %total=4.
     *        ^f
     *           89abcdef
     * 01234567  01234567
     * xxxxxxxx  x___xxxx
     *               ^f = 8+4=12, size=13, f+size%total = 25%16=9.
     *
     * </pre>
     */
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        ensureCapacity();

        front--;
        if (front < 0) {
            front += totalLength;
        }
        items[front / MAX_ARRAY_SIZE][front % MAX_ARRAY_SIZE] = item;
        size++;
    }

    private void ensureCapacity() {
        if (!isFull()) {
            return;
        }

        if (totalLength < MAX_ARRAY_SIZE) {
            /*
             * 01234567 length=8
             * 4567_123 total-f = 8-5=3
             *      ^f
             * 56781234
             *     ^f=4
             *
             * 0123456789ab l=12
             * 12345678____
             * ^f
             * */

            int afterLength = Math.min(totalLength + INIT_SIZE, MAX_ARRAY_SIZE);
            // int deltaLength = afterLength - totalLength;

            Item[] newItems0 = (Item[]) new Object[afterLength];

            int endPartLength = totalLength - front;
            System.arraycopy(items[0], front, newItems0, 0, endPartLength);
            System.arraycopy(items[0], 0, newItems0, endPartLength, front);
            items[0] = newItems0;
            front = 0;
            totalLength = afterLength;
        }
        else {

            int subArrayIdx = front / MAX_ARRAY_SIZE;
            int offsetInSubArray = front % MAX_ARRAY_SIZE;

            Item[] startArray = items[subArrayIdx];
            Item[] insertArray = (Item[]) new Object[MAX_ARRAY_SIZE];

            if (offsetInSubArray == 0) {
                // append a new array
                // xxxx      xxxx
                // xxxx  to  xxxx
                // fxxx      ____ <-- inserted
                // xxxx      fxxx <-- original
                //           xxxx

            }
            else {
                // split the font array to two arrays.
                // eg, arrayF, f=2,2. array length=4
                // xxxx      xxxx
                // xxxx  to  xxxx
                // xxfx      xx__ <-- inserted
                // xxxx      __fx <-- original
                //           xxxx
                System.arraycopy(startArray, 0, insertArray, 0, offsetInSubArray);
                Arrays.fill(startArray, 0, offsetInSubArray, null);
            }

            Item[][] newItems = (Item[][]) new Object[items.length + 1][];
            if (subArrayIdx > 0) {
                System.arraycopy(items, 0, newItems, 0, subArrayIdx);
            }
            newItems[subArrayIdx] = insertArray;
            System.arraycopy(items, subArrayIdx, newItems, subArrayIdx + 1,
                             items.length - subArrayIdx);

            items = newItems;
            front += MAX_ARRAY_SIZE;
            totalLength += MAX_ARRAY_SIZE;
        }

    }

    /**
     * 1)remove one empty arr size of MAX_ARRAY_SIZE.
     * <pre>
     * xxxx or  xxxx    or  __fx
     * x___     ____        xxxx
     * ____     ___f        xx__
     * fxxx     xxxx        ____
     * xxxx     xxxx
     * remove the arr before f
     *
     * 2)combine two to one for the last two:
     *
     * x__  or  ___ or  _fx_
     * _fx      fx_     ____
     *
     * 3)narrow the only one:
     * xx____xx or  __xxxxx__
     *       ^p       ^p
     * </pre>
     */
    private void tryNarrowCapacity() {
        if (items.length >= 3) {
            if (size <= totalLength - BIG_NARROW_SPACE) {
                int removeRowIdx = front / MAX_ARRAY_SIZE - 1;
                if (removeRowIdx < 0) {
                    //remove last row
                    items = Arrays.copyOf(items, items.length - 1);
                }
                else {
                    Item[][] newItems = (Item[][]) new Object[items.length - 1][];
                    System.arraycopy(items, 0, newItems, 0, removeRowIdx);
                    System.arraycopy(items, removeRowIdx + 1, newItems, removeRowIdx,
                                     items.length - removeRowIdx - 1);
                    items = newItems;
                    front -= MAX_ARRAY_SIZE;
                }
                totalLength -= MAX_ARRAY_SIZE;
            }
        }

        else {
            int endExclusive = front + size;
            if (endExclusive > totalLength) {
                endExclusive -= totalLength;
            }
            if (items.length == 2) {
                if (size <= totalLength - MEDIAN_NARROW_SPACE) {
                    int frontRowIdx = front / MAX_ARRAY_SIZE;
                    int endOffsetInclusive = endExclusive - 1; // point to pos of last item.
                    // if (endOffsetInclusive >= totalLength) {
                    //     endOffsetInclusive -= totalLength;
                    // }
                    int lastRowIdx = endOffsetInclusive / MAX_ARRAY_SIZE;
                    if (frontRowIdx != lastRowIdx) {
                        // data lay on two arrays, merge them.
                        System.arraycopy(items[lastRowIdx], 0, items[frontRowIdx], 0,
                                         endOffsetInclusive % MAX_ARRAY_SIZE + 1);

                    }

                    items = Arrays.copyOfRange(items, frontRowIdx, frontRowIdx + 1);
                    if (frontRowIdx == 1) {
                        front -= MAX_ARRAY_SIZE;
                    }
                }
            }

            else /* if (items.length == 1)*/ {
                int smallNarrowSpace = totalLength >> 1;
                if (size >= 8 && size < smallNarrowSpace) {
                    /* narrow to half.
                     * <pre>
                     * 01234567
                     * xxx___fx f=6, size=5, ee=3
                     *    ^
                     *    ee
                     * </pre>
                     */
                    Item[] newItems = (Item[]) new Object[smallNarrowSpace];
                    if (endExclusive < front) {
                        System.arraycopy(items[0], front,
                                         newItems, 0,
                                         totalLength - front);
                        System.arraycopy(items[0], 0,
                                         newItems, totalLength - front,
                                         endExclusive);
                    } else {
                        // only one part, __fxx__
                        System.arraycopy(items[0], front,
                                         newItems, 0,
                                         size);
                    }
                    items[0] = newItems;
                    front = 0;
                    totalLength = smallNarrowSpace;
                }
            }
        }
    }

    private boolean isFull() {
        return size == totalLength;
    }

    /**
     * add the item to the end
     * <p>
     * <pre>
     * 01234567
     * xxxxx___ f+size=5, insert at 5.
     * ^
     * f
     * 01234567
     * xx___xxx  f+size=10, %total=2.
     *      ^
     *      f
     *
     * 01234567
     * xxxx___x  f+size=12, %total=4.
     *        ^f
     *           89abcdef
     * 01234567  01234567
     * xxxxxxxx  x___xxxx
     *               ^f = 8+4=12, size=13, f+size%total = 25%16=9.
     *
     * </pre>
     */
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        ensureCapacity();
        int fillIndex = front + size;

        if (fillIndex >= totalLength) {
            fillIndex -= totalLength;
        }
        items[fillIndex / MAX_ARRAY_SIZE][fillIndex % MAX_ARRAY_SIZE] = item;
        size++;
    }

    /**
     * remove and return the item from the front
     * <p>
     * impl: remove f, f++, size--, (narrow)
     */
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("it's empty");
        }
        // return (Item) item;
        int offX = front / MAX_ARRAY_SIZE;
        int offY = front % MAX_ARRAY_SIZE;
        Item item = items[offX][offY];
        items[offX][offY] = null;
        front++;
        if (front == totalLength) {
            front = 0;
        }
        size--;
        tryNarrowCapacity();
        return item;
    }

    /** remove and return the item from the end */
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("it's empty");
        }
        int removeIndex = front + size - 1;
        if (removeIndex >= totalLength) {
            removeIndex -= totalLength;
        }
        int offX = removeIndex / MAX_ARRAY_SIZE;
        int offY = removeIndex % MAX_ARRAY_SIZE;

        Item toRemove = items[offX][offY];
        items[offX][offY] = null;
        size--;
        tryNarrowCapacity();
        return toRemove;
    }

    /** return an iterator over items in order from front to end */
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private int offset = 0;

        @Override
        public boolean hasNext() {
            return size > 0
                    && offset < size;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // return (Item) item;
            int itemIdx = offset + front;
            if (itemIdx >= totalLength) {
                itemIdx -= totalLength;
            }
            Item item = items[itemIdx/ MAX_ARRAY_SIZE][itemIdx % MAX_ARRAY_SIZE];
            offset++;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        for (int i = 0; i < 256000; i++) {
            deque.addFirst(i);
        }
        for (int i = 0; i < 256000; i++) {
            deque.removeFirst();
        }
        // for (int i = 0; i < 30; i++) {
        //     deque.addFirst(i);
        //     deque.addLast(i * 2);
        // }
        // while (deque.size > 0) {
        //     Integer integer = deque.removeLast();
        //     System.out.println("integer = " + integer);
        // }
        // for (int num : deque) {
        //     System.out.println("num = " + num);
        // }
    }
}
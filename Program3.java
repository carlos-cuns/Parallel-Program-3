import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Program3 {
    final static int NUM_PRESENTS = 500_000;

    // each present is represented by its id, which is just an integer
    public static ConcurrentLinkedQueue<Integer> createRandomBag() {
        // use parallel stream to generate the list
        List<Integer> list = IntStream.rangeClosed(1, NUM_PRESENTS)
                .parallel()
                .boxed()
                .collect(Collectors.toList());

        // shuffle the list
        Collections.shuffle(list);
        // make the list thread-safe using a data structure that has easy removal
        return new ConcurrentLinkedQueue<>(list);
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Start timing
        // Problem 1
        // - create unordered bag
        ConcurrentLinkedQueue<Integer> unorderedBag = createRandomBag();
        // - create chain
        Chain chain = new Chain();
        // - get the servants (Threads)
        Thread[] servants = new Thread[4];
        for (int i = 0; i < 4; i++) {
            servants[i] = new Thread(new Actions(unorderedBag, chain));
            servants[i].start();
        }
        System.out.println("Started servants!");
        for (int i = 0; i < 4; i++) {
            try {
                servants[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Finished Thank You's!");
        System.out.println("Gifts: " + chain.inserts);
        System.out.println("Thanks: " + chain.thankyous);
        System.out.println("Total execution time: " + totalTime + " ms");
        // -----------------------------------------------------------------------
    }
}

// - Do four actions
// 1. Add to chain in an order (pop and add to new list)
// 2. Remove from chain [Thank you] (remove from new list)
// 3. Scan the chain for id (search in new list)
class Actions implements Runnable {
    final int NUM_PRESENTS = 500_000;

    private Chain chain;
    private ConcurrentLinkedQueue<Integer> unorderedBag;

    public Actions(ConcurrentLinkedQueue<Integer> unorderedBag, Chain chain) {
        this.unorderedBag = unorderedBag;
        this.chain = chain;
    }

    @Override
    public void run() {
        while (!unorderedBag.isEmpty() || !chain.isEmpty()) {
            int action = (int) (Math.random() * 3) + 1;
            switch (action) {
                case 1:
                    int minoSearch = (int) (Math.random() * NUM_PRESENTS) + 1;
                    chain.search(minoSearch);
                    break;
                case 2:
                    chain.thanks();
                    break;
                case 3:
                    try {
                        int present = unorderedBag.poll();
                        chain.insert(present);
                    } catch (Exception e) {
                        // System.out.println("Servant tried to grab from empty bag");
                    }
                    break;
                default:
                    System.out.println("???");
                    break;

            }
        }
    }

}

class Node {
    int data;
    Node next;

    Node(int n) {
        data = n;
        next = null;
    }
}

class Chain {
    Node head;
    volatile int inserts = 0;
    volatile int thankyous = 0;

    public synchronized void insert(int n) {
        inserts++;
        if (head == null) {
            head = new Node(n);
        } else if (head.data > n) {
            Node temp = new Node(n);
            temp.next = head;
            head = temp;
        } else {
            Node current = head;
            while (current.next != null) {
                if (current.data < n && current.next.data > n) {
                    Node newNode = new Node(n);
                    Node temp = current.next;
                    current.next = newNode;
                    newNode.next = temp;
                    // System.out.println("added: " + n);
                    return;
                }
                current = current.next;
            }
            current.next = new Node(n);
        }
        // System.out.println("added: " + n);
        return;
    }

    public synchronized boolean search(int n) {
        Node current = head;
        while (current != null) {
            if (current.data > n) {
                return false;
            }
            if (current.data == n) {
                // System.out.println("Minotaur: SEARCH FOR GIFT " + n + "!" + "\nServants: " +
                // n + " found!");
                return true;
            }
            current = current.next;
        }
        // System.out.println("Minotaur: SEARCH FOR GIFT " + n + "!" + "\nServants: " +
        // n + " not found!");

        return false;
    }

    public synchronized void print() {
        Node current = head;
        System.out.print("Chain: ");
        while (current != null) {
            System.out.print(current.data + ((current.next != null) ? " -> " : ""));
            current = current.next;
        }
        System.out.println();
    }

    public synchronized void thanks() {
        if (head == null) {
            return;
        }
        thankyous++;
        // use for returns if getting value removed
        // Integer id = head.data;
        head = head.next;
        // System.out.println("Minotaur: Thank you for gift " + id + ".");
    }

    public synchronized boolean isEmpty() {
        return head == null;
    }
}
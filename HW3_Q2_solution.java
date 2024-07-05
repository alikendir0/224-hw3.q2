import java.util.*;

public class HW3_Q2_solution {
  public static void main(String[] args) {
    FileRead txt = new FileRead("HW3_Q2.txt");
    Valuefinder k = new Valuefinder();
    int V = Integer.parseInt(txt.Read());
    System.out.println("V=" + V);
    int E = Integer.parseInt(txt.Read());
    System.out.println("E=" + E);

    EdgeWeightedDigraph G = new EdgeWeightedDigraph(V);
    for (int i = 0; i < E; i++) {
      int[] temp = k.value(txt.Read());
      int v = temp[0];
      int w = temp[1];
      int weight = temp[2];
      DirectedEdge e = new DirectedEdge(v, w, weight);
      G.addEdge(e);
    }
    G.printGraph();
    System.out.println();
    System.out.println("The result");
    DijkstraSP sp = new DijkstraSP(G, 0);

    for (int i = 1; i < V; i++) {
      sp.pathTo(i);
    }
  }

  static public class DijkstraSP {
    private DirectedEdge[] edgeTo;
    private double[] distTo;
    private IndexMinPQ<Double> pq;

    public DijkstraSP(EdgeWeightedDigraph G, int s) {
      edgeTo = new DirectedEdge[G.V()];
      distTo = new double[G.V()];
      pq = new IndexMinPQ<Double>(G.V());
      for (int v = 0; v < G.V(); v++)
        distTo[v] = Double.POSITIVE_INFINITY;
      distTo[s] = 0.0;
      pq.insert(s, 0.0);
      while (!pq.isEmpty())
        relax(G, pq.delMin());
    }

    private void relax(EdgeWeightedDigraph G, int v) {
      for (DirectedEdge e : G.adj(v)) {
        int w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
          distTo[w] = distTo[v] + e.weight();
          edgeTo[w] = e;
          if (pq.contains(w))
            pq.change(w, distTo[w]);
          else
            pq.insert(w, distTo[w]);
        }
      }
    }

    public double distTo(int v) {
      return distTo[v];
    }

    public boolean hasPathTo(int v) {
      return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public void pathTo(int v) {
      String path = v + " ";
      int weight = 0;
      if (!hasPathTo(v)) {
        return;
      }

      for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {

        path = e.from() + " " + path;
        weight += e.weight();
      }
      path += weight;
      System.out.println(path);
    }
  }

  // GRAPH
  static public class EdgeWeightedDigraph {

    private final int V;
    private int E;
    private Bag<DirectedEdge>[] adj;

    public EdgeWeightedDigraph(int V) {
      this.V = V;
      this.E = 0;
      adj = (Bag<DirectedEdge>[]) new Bag[V];
      for (int v = 0; v < V; v++)
        adj[v] = new Bag<DirectedEdge>();
    }

    public int V() {
      return V;
    }

    public int E() {
      return E;
    }

    public void addEdge(DirectedEdge e) {
      adj[e.from()].add(e);
      E++;
    }

    public Iterable<DirectedEdge> adj(int v) {
      return adj[v];
    }

    public void printGraph() {
      for (int i = 0; i < V; i++) {
        for (DirectedEdge e : adj[i]) {
          System.out.println(e);
        }

      }
    }
  }

  static public class DirectedEdge {
    private final int v;
    private final int w;
    private final int weight;

    public DirectedEdge(int v, int w, int weight) {
      this.v = v;
      this.w = w;
      this.weight = weight;
    }

    public int weight() {
      return weight;
    }

    public int from() {
      return v;
    }

    public int to() {
      return w;
    }

    public String toString() {
      return String.format("%d %d %d", v, w, weight);
    }
  }

  static public class Bag<Item> implements Iterable<Item> {
    private LinkedList<Item> items;

    public Bag() {
      items = new LinkedList<>();
    }

    public void add(Item item) {
      items.add(item);
    }

    @Override
    public Iterator<Item> iterator() {
      return items.iterator();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (Item item : items) {
        sb.append(item).append(" ");
      }
      return sb.toString();
    }
  }

  static public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int N; // number of elements on PQ
    private int[] pq; // binary heap using 1-based indexing
    private int[] qp; // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys; // keys[i] = priority of i

    public IndexMinPQ(int NMAX) {
      keys = (Key[]) new Comparable[NMAX + 1]; // make this of length NMAX??
      pq = new int[NMAX + 1];
      qp = new int[NMAX + 1]; // make this of length NMAX??
      for (int i = 0; i <= NMAX; i++)
        qp[i] = -1;
    }

    // is the priority queue empty?
    public boolean isEmpty() {
      return N == 0;
    }

    // is k an index on the priority queue?
    public boolean contains(int k) {
      return qp[k] != -1;
    }

    // number of keys in the priority queue
    public int size() {
      return N;
    }

    // associate key with index k
    public void insert(int k, Key key) {
      if (contains(k))
        throw new RuntimeException("item is already in pq");
      N++;
      qp[k] = N;
      pq[N] = k;
      keys[k] = key;
      swim(N);
    }

    // return the index associated with a minimal key
    public int min() {
      if (N == 0)
        throw new RuntimeException("Priority queue underflow");
      return pq[1];
    }

    // return a minimal key
    public Key minKey() {
      if (N == 0)
        throw new RuntimeException("Priority queue underflow");
      return keys[pq[1]];
    }

    // delete a minimal key and returns its associated index
    public int delMin() {
      if (N == 0)
        throw new RuntimeException("Priority queue underflow");
      int min = pq[1];
      exch(1, N--);
      sink(1);
      qp[min] = -1; // delete
      keys[pq[N + 1]] = null; // to help with garbage collection
      pq[N + 1] = -1; // not needed
      return min;
    }

    /*
     * // change key associated with index k; insert if index k is not present
     * public void put(int k, Key key) {
     * if (!contains(k)) insert(k, key);
     * else changeKey(k, key);
     * }
     * 
     * // return key associated with index k
     * public Key get(int k) {
     * if (!contains(k)) throw new RuntimeException("item is not in pq");
     * else return keys[pq[k]];
     * }
     */

    // change the key associated with index k
    public void change(int k, Key key) {
      if (!contains(k))
        throw new RuntimeException("item is not in pq");
      keys[k] = key;
      swim(qp[k]);
      sink(qp[k]);
    }

    // decrease the key associated with index k
    public void decrease(int k, Key key) {
      if (!contains(k))
        throw new RuntimeException("item is not in pq");
      if (keys[k].compareTo(key) <= 0)
        throw new RuntimeException("illegal decrease");
      keys[k] = key;
      swim(qp[k]);
    }

    // increase the key associated with index k
    public void increase(int k, Key key) {
      if (!contains(k))
        throw new RuntimeException("item is not in pq");
      if (keys[k].compareTo(key) >= 0)
        throw new RuntimeException("illegal decrease");
      keys[k] = key;
      sink(qp[k]);
    }

    /**************************************************************
     * General helper functions
     **************************************************************/
    private boolean greater(int i, int j) {
      return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
      int swap = pq[i];
      pq[i] = pq[j];
      pq[j] = swap;
      qp[pq[i]] = i;
      qp[pq[j]] = j;
    }

    /**************************************************************
     * Heap helper functions
     **************************************************************/
    private void swim(int k) {
      while (k > 1 && greater(k / 2, k)) {
        exch(k, k / 2);
        k = k / 2;
      }
    }

    private void sink(int k) {
      while (2 * k <= N) {
        int j = 2 * k;
        if (j < N && greater(j, j + 1))
          j++;
        if (!greater(k, j))
          break;
        exch(k, j);
        k = j;
      }
    }

    /***********************************************************************
     * Iterators
     **********************************************************************/

    /**
     * Return an iterator that iterates over all of the elements on the
     * priority queue in ascending order.
     * <p>
     * The iterator doesn't implement <tt>remove()</tt> since it's optional.
     */
    public Iterator<Integer> iterator() {
      return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Integer> {
      // create a new pq
      private IndexMinPQ<Key> copy;

      // add all elements to copy of heap
      // takes linear time since already in heap order so no keys move
      public HeapIterator() {
        copy = new IndexMinPQ<Key>(pq.length - 1);
        for (int i = 1; i <= N; i++)
          copy.insert(pq[i], keys[pq[i]]);
      }

      public boolean hasNext() {
        return !copy.isEmpty();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public Integer next() {
        if (!hasNext())
          throw new NoSuchElementException();
        return copy.delMin();
      }
    }
  }
}

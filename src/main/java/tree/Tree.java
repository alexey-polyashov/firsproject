package tree;

public interface Tree<E extends  Comparable<? super E>> {

    enum TraverseMode{
        IN_ORDER,
        PRE_ORDER,
        POST_ORDER
    }

    boolean add(E value);

    boolean contains(E value);

    boolean remove(E value);

    boolean isEmpty();

    int size();

    Node getRoot();

    void display();

    void traverse(TraverseMode mode);

}

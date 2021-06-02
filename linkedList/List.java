package linkedList;

public interface List<E> extends Iterable<E>{

    void insertFirst(E value);
    E removeFirst();
    boolean remove(E value);
    boolean contains(E value);

    E getFirst();

    int size();
    boolean isEmpty();

    void display();


}

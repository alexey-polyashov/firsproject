package linkedQueue;

public interface Queue<E> {
    boolean isFull();
    boolean isEmpty();
    int size();
    boolean insert(E value);
    E remove();
    E peekFront();
    E peekRear();
    void display();
}

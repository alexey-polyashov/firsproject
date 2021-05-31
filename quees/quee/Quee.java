package quees.quee;

public interface Quee<E> {
    boolean isFull();
    boolean isEmpty();
    int size();
    boolean insert(E value);
    E remove();
    E peekFront();
    E peekRear();
    void display();
}

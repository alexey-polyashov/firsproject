package stack;

public interface Stack<E> {

    void push(E value);

    E pop();

    E peek();

    void display();

    int size();

    boolean isEmpty();

    boolean isFull();

}

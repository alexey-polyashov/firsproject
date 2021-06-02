package linkedStack;

import linkedList.LinkedList;

public class LinkedStack<E> implements Stack<E>{

    private final LinkedList<E> data;

    public LinkedStack(LinkedList<E> data) {
        this.data = data;
    }
    public LinkedStack() {
        this.data = new LinkedList<>();
    }

    @Override
    public void push(E value) {
        data.insertFirst(value);
    }

    @Override
    public E pop() {
        return data.removeFirst();
    }

    @Override
    public E peek() {
        return data.getFirst();
    }

    @Override
    public void display() {
        data.display();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean isFull() {
        return false;
    }
}

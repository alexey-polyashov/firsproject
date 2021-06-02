package linkedQueue;

import twoSideList.TwoSideLinkedList;

public class LinkedQueue<E> implements Queue<E> {

    private final TwoSideLinkedList<E> data;

    public LinkedQueue() {
        this.data = new TwoSideLinkedList<>();
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean insert(E value) {
        data.insertLast(value);
        return true;
    }

    @Override
    public E remove() {
        return data.removeFirst();
    }

    @Override
    public E peekFront() {
        return data.getFirst();
    }

    @Override
    public E peekRear() {
        return data.getLast();
    }

    @Override
    public void display() {
        data.display();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}

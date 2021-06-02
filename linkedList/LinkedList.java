package linkedList;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class LinkedList<E> implements List<E>{

    protected int size;
    protected Node<E> first;

    protected class Node<E>{
        public Node<E> next;
        public E data;

        public Node(Node<E> next, E data) {
            this.next = next;
            this.data = data;
        }
    }

    class ListIterator<E> implements Iterator<E>{

        Node<E> itr;

        @Override
        public boolean hasNext() {
            return itr!=null;
        }

        @Override
        public E next(){
            if(itr==null){
                throw new NoSuchElementException();
            }
            E data = itr.data;
            itr = itr.next;
            return data;
        }

        public ListIterator(Node<E> frst) {
            this.itr = frst;
        }
    }

    ListIterator<E> Itr;

    @Override
    public Iterator<E> iterator() {
        return new ListIterator<E>(first);
    }

    @Override
    public void insertFirst(E value) {
        first = new Node<>(first, value);
        size++;
    }

    @Override
    public E removeFirst() {
        if(isEmpty()){
            return null;
        }
        Node<E> delEl = first;
        first = first.next;
        delEl.next=null;
        size--;
        return delEl.data;
    }

    @Override
    public boolean remove(E value) {
        Node<E> curEl = first;
        Node<E> prev = null;
        while(curEl!=null){
            if(curEl.data.equals(value)){
                break;
            }
            prev = curEl;
            curEl = curEl.next;
        }
        if(curEl==null){
            return false;
        }else if(curEl==first){
            removeFirst();
            return true;
        }else{
            prev.next = curEl.next;
            curEl.next = null;
            size--;
            return true;
        }
    }

    @Override
    public boolean contains(E value) {
        Node<E> curEl = first;
        while(curEl!=null){
            if(curEl.data.equals(value)){
                return true;
            }
            curEl = curEl.next;
        }
        return false;
    }

    @Override
    public E getFirst() {
        return getValue(first);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void display() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        Node<E> curEl = first;
        while(curEl!=null){
            str.append(curEl.data);
            if(curEl.next!=null){
                str.append(" -> ");
            }
            curEl = curEl.next;
        }
        str.append("]");
        return str.toString();
    }

    protected E getValue(Node<E> n){
        return n==null ? null : n.data;
    }


}

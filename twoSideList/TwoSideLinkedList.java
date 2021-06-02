package twoSideList;

import linkedList.LinkedList;

public class TwoSideLinkedList<E> extends LinkedList<E> implements TwoSideList<E> {

    private Node<E> last;

    @Override
    public void insertLast(E value) {
        Node<E> newEl = new Node<E>(null, value);
        if(isEmpty()){
            first = newEl;
        }else{
            last.next = newEl;
        }
        last = newEl;
        size++;
    }

    @Override
    public void insertFirst(E value) {
        super.insertFirst(value);
        if(size==1){
            last = first;
        }
    }

    @Override
    public E removeFirst() {
        E remEl = super.removeFirst();
        if(isEmpty()){
            last = null;
        }
        return remEl;
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
        }else if(curEl==last){
            prev.next = null;
            last = prev;
        }else{
            prev.next = curEl.next;
        }
        curEl.next = null;
        size--;
        return true;
    }

    @Override
    public E getLast() {
        return getValue(last);
    }


}

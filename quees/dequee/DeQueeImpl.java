package quees.dequee;

import quees.quee.QueeImpl;

public class DeQueeImpl<E> extends QueeImpl<E> implements DeQuee<E>{

    public DeQueeImpl(int size) {
        super(size);
    }

    @Override
    public boolean insertRight(E value) {
        return insert(value);
    }

    @Override
    public boolean insertLeft(E value) {
        if(isFull()){
            return false;
        }
        if(front <= 0){
            front = data.length;
        }
        data[--front] = value;
        size++;
        return true;
    }

    @Override
    public E removeRight() {
        if(isEmpty()){
            return null;
        }
        if(rear == data.length){
            rear = 0;
        }
        E value = data[rear++];
        size--;
        return value;
    }

    @Override
    public E removeLeft() {
        return remove();
    }
}

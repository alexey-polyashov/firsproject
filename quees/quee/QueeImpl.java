package quees.quee;

public class QueeImpl<E> implements Quee<E>{

    protected int rear=0;
    protected int front=0;
    protected int size=0;
    final protected E[] data;

    public QueeImpl(int size) {
        this.data = (E[]) new Object[size];
        this.front =0;
        this.rear = -1;
    }

    @Override
    public boolean isFull() {
        return size == data.length;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean insert(E value) {
        if(isFull()){
            return false;
        }
        if(rear > data.length){
            rear = -1;
        }
        data[++rear] = value;
        size++;
        return true;
    }

    @Override
    public E remove() {
        if(isEmpty()){
            return null;
        }
        if(front == data.length){
            front = 0;
        }
        E value = data[front++];
        size--;
        return value;
    }

    @Override
    public E peekFront() {
        if(isEmpty()){
            return null;
        }
        E value = data[front];
        return value;
    }

    @Override
    public E peekRear() {
        if(isEmpty()){
            return null;
        }
        E value = data[rear];
        return value;
    }

    @Override
    public void display(){
        System.out.print("[");
        if(!isEmpty()){
            int marker = front;
            for (int i = 0; i < size-1; i++) {
                System.out.print(data[marker++] + ",");
                if(marker>=data.length){
                    marker=0;
                }
            }
            System.out.print(data[marker]);
        }
        System.out.println("]");
    }
}

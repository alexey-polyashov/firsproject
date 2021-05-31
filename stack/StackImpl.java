package stack;

public class StackImpl<E> implements Stack<E>{

    private final E[] data;
    int size = 0;

    public StackImpl(int size) {
        this.data = (E[])new Object[size];
    }

    @Override
    public void push(E value) {
        data[size++] = value;
    }

    @Override
    public E pop() {
        E value = peek();
        data[--size] = null;
        return value;
    }

    @Override
    public E peek() {
        if(isEmpty()){
            return null;
        }
        return data[size-1];
    }

    @Override
    public void display() {
        System.out.print("[");
        if(!isEmpty()){
            for (int i = 0; i < size-1; i++) {
                System.out.print(data[i] + ",");
            }
            System.out.print(data[size-1]);
        }
        System.out.println("]");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isFull() {
        return size == data.length;
    }

}

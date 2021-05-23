import java.util.Arrays;

public class ArrayContainer<E extends Comparable<? super E>> implements Array<E>{

    private static final int DEFAULT_CAPACITY = 8;

    private E[] data;
    private int size;

    public E[] getData(){
        return data;
    }

    public ArrayContainer(E[] data) {
        this.data = Arrays.copyOf(data, data.length);
        this.size = data.length;
    }

    public ArrayContainer(int size) {
        this.data = (E[])new Comparable[size] ;
        this.size = size;
    }

    public ArrayContainer() {
        this(DEFAULT_CAPACITY);
    }

    @Override
    public void add(E value) {
        if(data.length == size){
            data = Arrays.copyOf(data, calcNewLength());
        }
        data[size++] = value;
    }

    @Override
    public E delete(int index) {

        checkIndex(index);
        E re = data[index];

        System.arraycopy(data, index+1, data, index, size-1-index);
        size--;
        return re;
    }

    @Override
    public boolean delete(E value) {
        int index = indexOf(value);
        return index !=-1 && delete(index) != null;
    }

    @Override
    public void set(int index, E value) {
        checkIndex(index);
        data[index] = value;
    }

    @Override
    public int indexOf(E value) {
        for (int i = 0; i < size; i++) {
            if(value.equals(data[i])){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(E value) {
        return indexOf(value) !=-1;
    }

    @Override
    public void sortBuble() {
        for (int i = 0; i < size-1; i++) {
            for (int j = 0; j < size-1-i; j++) {
                if(data[j].compareTo(data[j+1])>0){
                    swap(j, j+1);
                }
            }
        }
    }

    @Override
    public void sortSelect() {
        for (int i = 0; i < size-1; i++) {
            int minIndex = i;
            for (int j = i+1; j < size; j++) {
                if(data[j].compareTo(data[minIndex])<0){
                    minIndex = j;
                }
            }
            swap(i, minIndex);
        }
    }

    @Override
    public void sortInsert() {
        for (int i = 1; i < size; i++) {
            E temp = data[i];
            int in = i;
            while (in > 0 && data[in - 1].compareTo(temp) >= 0) {
                data[in] = data[in - 1];
                in--;
            }
            data[in] = temp;
        }
    }


    private void swap(int a, int b){
        E d=data[b];
        data[b]=data[a];
        data[a]=d;
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        return data[index];
    }

    private void checkIndex(int index){
        if(index>=size || index<0){
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds [0, " + (size-1) + "]");
        }
    }

    private int calcNewLength() {
        return size == 0 ? DEFAULT_CAPACITY : size * 2;
    }

    @Override
    public void display(){
        System.out.print("{");
        for (int i = 0; i < size; i++) {
            System.out.print(data[i]);
            if(i<size-1){
                System.out.print(",");
            }
        }
        System.out.println("}");
    }

}

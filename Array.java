public interface Array<E> {

    void add(E value);

    E delete(int index);

    boolean delete(E value);

    void set(int index, E value);

    E get(int Index);

    void display();

    int indexOf(E value);

    boolean contains(E value);

    void sortBuble();

    void sortSelect();

    void sortInsert();

}


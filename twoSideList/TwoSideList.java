package twoSideList;

import linkedList.List;

public interface TwoSideList<E> extends List<E> {

    void insertLast(E value);
    E getLast();

}

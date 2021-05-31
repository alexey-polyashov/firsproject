package quees.dequee;

import quees.quee.Quee;

public interface DeQuee<E> extends Quee<E> {
    boolean insertLeft(E value);
    boolean insertRight(E value);
    E removeLeft();
    E removeRight();
}

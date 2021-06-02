package linkedQueue;

import twoSideList.TwoSideLinkedList;

public class LinkedQueueTest {

    public static void main(String[] args) {

        LinkedQueue<Integer> lq = new LinkedQueue<>();

        lq.insert(Integer.valueOf(1));
        lq.insert(Integer.valueOf(2));
        lq.insert(Integer.valueOf(3));
        lq.insert(Integer.valueOf(4));
        lq.insert(Integer.valueOf(5));
        lq.insert(Integer.valueOf(6));

        lq.display();

        System.out.println("Peek rear - " + lq.peekRear());
        System.out.println("Peek front - " + lq.peekFront());
        System.out.println("Is full - " + lq.isFull());

        lq.remove();

        lq.display();

    }

}

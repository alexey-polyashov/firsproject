package linkedStack;

import linkedList.LinkedList;

public class LinkedStackTest {

    public static void main(String[] args) {
        LinkedStack<Integer> lstk = new LinkedStack<>();

        lstk.push(Integer.valueOf(1));
        lstk.push(Integer.valueOf(2));
        lstk.push(Integer.valueOf(3));
        lstk.push(Integer.valueOf(4));
        lstk.push(Integer.valueOf(5));
        lstk.push(Integer.valueOf(6));

        lstk.display();

        System.out.println("Peek - " + lstk.peek());

        System.out.println("Pop - " + lstk.pop());

        lstk.display();

    }

}

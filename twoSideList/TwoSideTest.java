package twoSideList;

public class TwoSideTest {

    public static void main(String[] args) {

        TwoSideLinkedList<Integer> twll = new TwoSideLinkedList<>();

        twll.insertFirst(Integer.valueOf(1));
        twll.insertLast(Integer.valueOf(2));
        twll.insertFirst(Integer.valueOf(3));
        twll.insertLast(Integer.valueOf(4));
        twll.insertFirst(Integer.valueOf(5));
        twll.insertLast(Integer.valueOf(6));

        twll.display();

        System.out.println("Contains 2 - " + twll.contains(2));
        System.out.println("Contains 9 - " + twll.contains(9));

        twll.remove(Integer.valueOf(4));
        twll.display();

    }

}

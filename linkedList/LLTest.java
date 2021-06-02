package linkedList;

public class LLTest {
    public static void main(String[] args) {
        LinkedList<Integer> ll = new LinkedList<>();

        ll.insertFirst(Integer.valueOf(1));
        ll.insertFirst(Integer.valueOf(2));
        ll.insertFirst(Integer.valueOf(3));
        ll.insertFirst(Integer.valueOf(4));
        ll.insertFirst(Integer.valueOf(5));
        ll.insertFirst(Integer.valueOf(6));

        ll.display();

        System.out.println("Contains 2 - " + ll.contains(2));
        System.out.println("Contains 9 - " + ll.contains(9));

        ll.remove(Integer.valueOf(4));
        ll.display();

        System.out.println("ForEach:");
        for (Integer el: ll) {
            System.out.println(el);
        }

        System.out.println("List:");
        ll.display();

    }
}

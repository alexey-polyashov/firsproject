package quees.quee;

public class QueeTest {

    public static void main(String[] args) {

        Quee<Integer> quee = new QueeImpl<Integer>(5);

        quee.insert(5);
        quee.display();
        quee.insert(4);
        quee.display();
        quee.insert(3);
        quee.display();
        quee.insert(2);
        quee.display();
        quee.insert(1);
        quee.display();

        System.out.println(quee.remove());
        System.out.println(quee.remove());
        System.out.println(quee.remove());
        System.out.println(quee.remove());
        System.out.println(quee.remove());
        quee.display();

    }

}

package quees.dequee;


public class DeQueeTest {

    public static void main(String[] args) {

        DeQuee<Integer> quee = new DeQueeImpl<Integer>(5);

        quee.insertLeft(5);
        quee.display();
        quee.insertRight(4);
        quee.display();
        quee.insertLeft(3);
        quee.display();
        quee.insertRight(2);
        quee.display();
        quee.insertLeft(1);
        quee.display();
        quee.insertLeft(1);
        quee.display();

        System.out.println(quee.removeLeft());
        quee.display();
        System.out.println(quee.removeRight());
        quee.display();
        System.out.println(quee.removeLeft());
        quee.display();
        System.out.println(quee.removeRight());
        quee.display();
        System.out.println(quee.removeLeft());
        quee.display();

    }

}

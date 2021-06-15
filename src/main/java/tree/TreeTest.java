package tree;

public class TreeTest {

    public static void main(String[] args) {

        Tree<Integer> tree = new TreeImp<>();
        tree.add(61);
        tree.add(15);
        tree.add(20);
        tree.add(18);
        tree.add(12);
        tree.add(60);
        tree.add(70);
        tree.add(1);
        tree.add(9);
        tree.add(35);
        tree.add(68);
        tree.add(91);
        tree.add(40);
        tree.add(19);
        tree.add(50);

        System.out.println("Find 68: " + tree.contains(68));
        System.out.println("Find 1000: " + tree.contains(1000));

        tree.traverse(Tree.TraverseMode.IN_ORDER);

        tree.display();

        System.out.println("Remove 50");
        tree.remove(50);
        tree.display();

        System.out.println("Remove 15");
        tree.remove(15);

        tree.display();

    }
}

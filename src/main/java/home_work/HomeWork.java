package home_work;

import tree.Node;
import tree.Tree;
import tree.TreeImp;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;

public class HomeWork {

    static int maxLevel = 4;
    static int minValue = -20;
    static int maxValue = 20;
    static int treeCount = 20;

    public static List<Tree<Integer>> treeGenerator(){

        List<Tree<Integer>> treeList = new ArrayList<>();

        int capasity = (int)(Math.pow(2, maxLevel)-1);

        for (int i = 0; i < treeCount; i++) {
            Tree<Integer> newTree = new TreeImp<>(maxLevel);
            for (int j = 0; j < capasity; j++) {
                int rndValue = (int)(Math.random() * (maxValue-minValue) + minValue);
                newTree.add(rndValue);
            }
            treeList.add(newTree);
        }

        return treeList;
    }

    public static boolean isBalaced(Node node){
        return (node == null) ||
                isBalaced(node.getLeftChild())
                && isBalaced(node.getRightChild())
                && Math.abs(height(node.getLeftChild()) - height(node.getRightChild())) <= 1;
    }

    private static int height(Node node) {
        return node ==null ? 0 : 1 +Math.max(height(node.getRightChild()), height(node.getLeftChild()));
    }

    public static void main(String[] args) {

        List<Tree<Integer>> treeList = treeGenerator();

        int balancedCount = 0;

        for (Tree tree: treeList) {
            //tree.display();
            if(isBalaced(tree.getRoot())){
                balancedCount++;
                //System.out.println("BALANCED");
            }
        }

        System.out.println("Total numbers of trees: " + treeCount);
        System.out.println("Number of balanced trees: " + balancedCount);
        System.out.println("Number of unbalanced trees: " + (treeCount - balancedCount));
        System.out.println("Percent of unbalanced trees: " + ((double)(treeCount - balancedCount)/treeCount)*100);

    }

}

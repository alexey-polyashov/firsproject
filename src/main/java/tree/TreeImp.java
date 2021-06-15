package tree;

import java.util.Stack;

public class TreeImp<E extends  Comparable<? super E>> implements Tree<E>{

    int maxLevel;

    private class NP{
        Node<E> current;
        Node<E> parrent;
        int level;

        public NP(Node<E> current, Node<E> parrent, int level) {
            this.current = current;
            this.parrent = parrent;
            this.level = level;
        }

    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    private int size;
    private Node<E> root;

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public boolean add(E value) {

        Node<E> node = new Node<>(value);

        NP np = find(value);
        if(np.current != null){
            return false;
        }

        if(np.level>maxLevel){
            return false;
        }

        if(np.parrent==null){
            root = node;
        }else if(np.parrent.isLeftChild(value)){
            np.parrent.setLeftChild(node);
        }else{
            np.parrent.setRightChild(node);
        }

        size++;
        return true;

    }

    @Override
    public boolean contains(E value) {

        NP np = find(value);
        return np.current != null;

    }

    private NP find(E value){

        Node<E> current = root;
        Node<E> prev = null;

        int level = 1;

        while (current != null) {
            if (current.getValue().equals(value)) {
                return new NP(current, prev,level);
            }
            prev = current;
            if (current.isLeftChild(value)) {
                current = current.getLeftChild();
            } else {
                current = current.getRightChild();
            }
            level++;
        }

        return new NP(null, prev, level);

    }

    @Override
    public boolean remove(E value) {

        NP np = find(value);
        Node<E> removeNode = np.current;
        Node<E> parentNode = np.parrent;

        if (removeNode.isLeaf()) {
            removeLeafNode(removeNode, parentNode);
        } else if(removeNode.hasOnlyOneChild()){
            removeNodeWithOneChild(removeNode, parentNode);
        }else{
            removeNodeWithAllChildren(removeNode, parentNode);

        }

        size--;
        return true;
    }

    private void removeNodeWithAllChildren(Node<E> removeNode, Node<E> parentNode) {

        Node<E> suc = getSuccessor(removeNode);
        if(removeNode == root){
            root = suc;
        }else if(parentNode.isLeftChild(removeNode.getValue())){
            parentNode.setLeftChild(suc);
        }else{
            parentNode.setRightChild(suc);
        }

        suc.setLeftChild(removeNode.getLeftChild());
    }

    public TreeImp(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public TreeImp() {
        this.maxLevel = 10;
    }

    private Node<E> getSuccessor(Node<E> removeNode) {

        Node<E> suc = removeNode;
        Node<E> sucParent = null;
        Node<E> cur = removeNode.getRightChild();

        while(cur!=null){
            sucParent = suc;
            suc = cur;
            cur = cur.getLeftChild();
        }

        if(suc != removeNode.getRightChild() && sucParent != null){
            sucParent.setLeftChild(suc.getRightChild());
            suc.setRightChild(removeNode.getRightChild());
        }

        return suc;
    }

    private void removeNodeWithOneChild(Node<E> removeNode, Node<E> parentNode) {
        Node<E> childNode = removeNode.getLeftChild()!=null ? removeNode.getLeftChild() : removeNode.getRightChild();
        if(removeNode ==root){
            root = childNode;
        }else if(parentNode.isLeftChild(removeNode.getValue())){
            parentNode.setLeftChild(childNode);
        }else{
            parentNode.setRightChild(childNode);
        }
    }

    private void removeLeafNode(Node<E> removeNode, Node<E> parentNode) {
        if (removeNode == root) {
            root = null;
        } else if (parentNode.isLeftChild(removeNode.getValue())) {
            parentNode.setLeftChild(null);
        } else {
            parentNode.setRightChild(null);
        }
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void display() {
        Stack<Node<E>> globalStack = new Stack<>();
        globalStack.push(root);
        int nBlanks = 64;

        boolean isRowEmpty = false;
        System.out.println("................................................................");

        while (!isRowEmpty) {
            Stack<Node<E>> localStack = new Stack<>();

            isRowEmpty = true;
            for (int i = 0; i < nBlanks; i++) {
                System.out.print(" ");
            }

            while (!globalStack.isEmpty()) {
                Node<E> tempNode = globalStack.pop();
                if (tempNode != null) {
                    System.out.print(tempNode.getValue());
                    localStack.push(tempNode.getLeftChild());
                    localStack.push(tempNode.getRightChild());
                    if (tempNode.getLeftChild() != null || tempNode.getRightChild() != null) {
                        isRowEmpty = false;
                    }
                } else {
                    System.out.print("--");
                    localStack.push(null);
                    localStack.push(null);
                }
                for (int j = 0; j < nBlanks * 2 - 2; j++) {
                    System.out.print(" ");
                }
            }

            System.out.println();

            while (!localStack.isEmpty()) {
                globalStack.push(localStack.pop());
            }

            nBlanks /= 2;
        }
        System.out.println("................................................................");

    }

    @Override
    public void traverse(TraverseMode mode) {
        switch (mode){
            case IN_ORDER -> inOrder(root);
            case PRE_ORDER -> preOrder(root);
            case POST_ORDER -> postOrder(root);
            default -> throw new IllegalArgumentException("Unknown traverse mode " + mode);
        }
    }

    private void postOrder(Node<E> current) {

        if(current == null){
            return;
        }

        postOrder(current.getLeftChild());
        postOrder(current.getRightChild());
        System.out.println(current.getValue());

    }

    private void preOrder(Node<E> current) {

        if(current == null){
            return;
        }

        System.out.println(current.getValue());
        preOrder(current.getLeftChild());
        preOrder(current.getRightChild());

    }

    private void inOrder(Node<E> current) {

        if(current == null){
            return;
        }

        inOrder(current.getLeftChild());
        System.out.println(current.getValue());
        inOrder(current.getRightChild());

    }

}

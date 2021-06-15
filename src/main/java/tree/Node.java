package tree;

public class Node<T extends Comparable<? super T>> {

    private final T value;
    private Node<T> leftChild;
    private Node<T> rightChild;


    public Node<T> getLeftChild() {
        return leftChild;
    }

    public Node<T> getRightChild() {
        return rightChild;
    }

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setLeftChild(Node<T> leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(Node<T> rightChild) {
        this.rightChild = rightChild;
    }

    public boolean isLeftChild(T v){
        if(v.compareTo(getValue())<0){
            return true;
        }
        return false;
    }

    public boolean isLeaf(){
        return leftChild == null && rightChild==null;
    }

    public boolean hasOnlyOneChild() {
        return (leftChild != null ^ rightChild !=null);
    }
}

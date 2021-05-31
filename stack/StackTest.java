package stack;

public class StackTest{

    public static void main(String[] args) {
        StackImpl<Integer> stk = new StackImpl(5);

        stk.push(5);
        stk.push(4);
        stk.push(3);
        stk.push(2);
        stk.push(1);
        stk.display();

        System.out.println(stk.pop());
        System.out.println(stk.pop());
        System.out.println(stk.pop());
        System.out.println(stk.pop());
        System.out.println(stk.pop());
        stk.display();


    }


}

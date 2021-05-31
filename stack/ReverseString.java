package stack;

public class ReverseString {

    public static void main(String[] args) {
        String sourceString = "Source string";
        StackImpl<Character> str = new StackImpl<>(sourceString.length());
        for (int i = 0; i < sourceString.length(); i++) {
            str.push(sourceString.charAt(i));
        }
        for (int i = 0; i < sourceString.length(); i++) {
            System.out.print((str.pop()));
        }
    }

}

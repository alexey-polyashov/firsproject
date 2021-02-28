import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lyambda {

    static public int search(Integer n, Integer[] list){

        return Arrays.stream(list).filter((i)-> i==n).findFirst().orElse(-1);

    }

    static public String reverse(String s){
        
        Function<String, String> rev = x -> {
            StringBuilder res = new StringBuilder();
            int len = x.length();
            for(int i = 0; i < len; i++) {
                res.append(s.charAt(len - i -1));
            }
            return res.toString();
        };
        return rev.apply(s);

    }

    static Integer maximum(Integer[] list){
        return Arrays.stream(list).max((i1,i2)-> i1 - i2).get();
    }

    static Double average(List<Integer> list){
        if(list.size()==0){
            return 0.0;
        }
        Integer sum = list.stream().reduce((s1, s2) -> s1+s2).orElse(0);
        return (double)sum / list.size();
    }

    static List<String> search(List<String> list){
        return list.stream().filter((s) -> s.charAt(0) == 'a' && s.length()==3).collect(Collectors.toList());
    }


    public static void main(String[] args) {

        Integer[] arr = new Integer[]{1,2,3,4,5,6,7,9,0};

        //тест поиска числа
        System.out.println(search(5, arr));
        System.out.println(search(8, arr));

        //тест реверса
        System.out.println(reverse("1234 56789"));

        //тест поиска максимального элемента
        System.out.println(maximum(new Integer[]{1,2,3,4,5,6,5,4,3,2,1}));

        //тест вычисления среднего элемента
        List<Integer> arr2 = new ArrayList<>();
        arr2.add(1);
        arr2.add(2);
        arr2.add(3);
        arr2.add(4);
        arr2.add(5);
        arr2.add(6);
        arr2.add(5);

        System.out.println(average(arr2));

        //тест поиска строк
        List<String> arr3 = new ArrayList<>();
        arr3.add("asdf");
        arr3.add("asd");
        arr3.add("dfgh");
        arr3.add("erty");
        arr3.add("dfg");
        arr3.add("Aqw");
        arr3.add("sdfg");

        System.out.println(search(arr3));
    }

}

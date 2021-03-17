import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeWork {

    //задание 1
    private static <Т> void replace(Т[] arrRes, int res, int dst){
        if(arrRes.length < res || arrRes.length < dst){
            System.out.println("Index out of bounds.Operation is not available");
        }
        Object buff;
        buff = arrRes[dst];
        arrRes[dst] = arrRes[res];
        arrRes[res] = (Т)buff;
    }

    //задание 2
    private static <T> ArrayList<T> fromArray(T[] arrRes){
        ArrayList<T> arList = new ArrayList<>();
        for (T el: arrRes) {
            arList.add(el);
        }
        return arList;
    }

    public static void main(String[] args) {

        //1.
        Integer[] test1 = new Integer[]{1,2,3,4};
        HomeWork.<Integer>replace(test1, 1,3);
        System.out.println("перемещение элементов");
        for (Integer i: test1) {
            System.out.print(i);
        };
        System.out.println();

        //2.
        System.out.println("преобразование в ArrayList");
        List<Integer> test2 = HomeWork.<Integer>fromArray(new Integer[]{1,2,3,4,5});
        System.out.println(test2);

        //3
        Box<Apple> Box1 = new Box<>();
        Box<Apple> Box2 = new Box<>();
        Box<Orange> Box3 = new Box<>();
        Box<Orange> Box4 = new Box<>();
        Apple apple = new Apple();
        Orange orange = new Orange();
        //10 яблок
        for (int i = 0; i < 10; i++) {
            Box1.add(apple);
        }
        //9 яблок
        for (int i = 0; i < 9; i++) {
            Box2.add(apple);
        }
        //6 апельсинов
        for (int i = 0; i < 6; i++) {
            Box3.add(orange);
        }
        //3 апельсина
        for (int i = 0; i < 3; i++) {
            Box4.add(orange);
        }

        System.out.println("вес коробки 1 - " + Box1.getWeight());
        System.out.println("вес коробки 2 - " + Box2.getWeight());
        System.out.println("вес коробки 3 - " + Box3.getWeight());
        System.out.println("вес коробки 4 - " + Box4.getWeight());
        System.out.println("сравнение коробки 1 с коробкой 2 " + Box1.compare(Box2));
        System.out.println("сравнение коробки 2 с коробкой 3 " + Box1.compare(Box2));
        System.out.println("пересыпали из коробки 4 в коробку 3:");
        Box4.pour(Box3);
        System.out.println("    вес коробки 3 - " + Box3.getWeight());
        System.out.println("    вес коробки 4 - " + Box4.getWeight());

    }

}

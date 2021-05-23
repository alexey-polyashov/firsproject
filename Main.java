import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        //тестирование класса ArrayContainer

        System.out.println("TESTING CLASS");

        Array<Integer> bigArray = new ArrayContainer<>(10);

        for (int i = 0; i < 10; i++) {
            bigArray.set(i, (int)(Math.random()*20));
        }

        System.out.println("\ninition array");
        bigArray.display();

        bigArray.add(11);
        bigArray.add(12);
        bigArray.add(13);
        bigArray.add(14);
        bigArray.add(15);
        bigArray.add(16);
        bigArray.add(17);

        System.out.println("\ntesting method 'add'");
        bigArray.display();

        System.out.println("\ntesting method 'contains(15)'");
        System.out.println(bigArray.contains(15));

        System.out.println("\ntesting method 'delete(3)'");
        bigArray.delete(Integer.valueOf(3));
        bigArray.display();

        System.out.println("\ntesting method 'delete(index(3))'");
        bigArray.delete(3);
        bigArray.display();

        System.out.println("\ntesting method 'indexOf(15)'");
        System.out.println(bigArray.indexOf(15));

        //создание большого массива
        System.out.println("\nTESTING SORT METHODS\n");

        bigArray = new ArrayContainer<>(100000);

        for (int i = 0; i < 100000; i++) {
            bigArray.set(i, (int)(Math.random()*100000));
        }

        Array<Integer> bigArray2 = new ArrayContainer<Integer>(((ArrayContainer<Integer>)bigArray).getData());
        Array<Integer> bigArray3 = new ArrayContainer<Integer>(((ArrayContainer<Integer>)bigArray).getData());

        long startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        bigArray.sortBuble();
        long endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        System.out.println("Buble sort duration: " + (endTime - startTime));

        startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        bigArray2.sortSelect();
        endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        System.out.println("Select sort duration: " + (endTime - startTime));

        startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        bigArray3.sortInsert();
        endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        System.out.println("Insert sort duration: " + (endTime - startTime));


    }

}

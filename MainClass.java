import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MainClass {

    public static final int CARS_COUNT = 4;
    private static CyclicBarrier startFinish = new CyclicBarrier(CARS_COUNT+1);

    public static void main(String[] args) {

        try {

            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
            Race race = new Race(new Road(60), new Tunnel(CARS_COUNT/2), new Road(40));
            Car[] cars = new Car[CARS_COUNT];
            for (int i = 0; i < cars.length; i++) {
                cars[i] = new Car(race, 20 + (int) (Math.random() * 10), startFinish);
            }
            for (int i = 0; i < cars.length; i++) {
                new Thread(cars[i]).start();
            }
            startFinish.await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            startFinish.await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


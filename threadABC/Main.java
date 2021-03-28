package threadABC;

public class Main {

    public static Object monitor = new Object();
    public volatile char leter = 'A';

    public void printA(){
        synchronized (monitor) {
            try {
                while (leter != 'A') {
                    monitor.wait();
                }
                System.out.println(leter);
                leter = 'B';
                monitor.notify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printB(){
        synchronized (monitor) {
            try {
                while (leter != 'B') {
                    monitor.wait();
                }
                System.out.println(leter);
                leter = 'C';
                monitor.notify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printC(){
        synchronized (monitor) {
            try {
                while (leter != 'C') {
                    monitor.wait();
                }
                System.out.println(leter);
                leter = 'A';
                monitor.notify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        Main m = new Main();

        Thread tr1 = new Thread(()->{
            for (int i = 0; i < 5; i++) {
                m.printA();
            }
        });

        Thread tr2 = new Thread(()->{
            for (int i = 0; i < 5; i++) {
                m.printB();
            }
        });

        Thread tr3 = new Thread(()->{
            for (int i = 0; i < 5; i++) {
                m.printC();
            }
        });

        tr1.start();
        tr2.start();
        tr3.start();

    }


}

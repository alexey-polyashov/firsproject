public class Programm {

    static final int size = 10000000;
    static final int h = size / 2;

    static TestArray simpleArr = new TestArray(size);
    static TestArray threadArr = new TestArray(size);

    public static void letSimpleTest(){

        System.out.println("simple test started");

        simpleArr.fillArray(1.0f);
        long a = System.currentTimeMillis();
        simpleArr.calc();
        long b = System.currentTimeMillis();

        System.out.println("simple test time - " + (b - a));

    }

    public static void letThreadTest() throws InterruptedException {

        System.out.println("thread test started");

        threadArr.fillArray(1.0f);
        long a = System.currentTimeMillis();
        TestArray threadArr1 = new TestArray(h);
        TestArray threadArr2 = new TestArray(h);
        threadArr1.load(threadArr.getArray(), 0, h, 0);
        threadArr2.load(threadArr.getArray(), h, h, 0);

        Thread thr1 = new Thread(()->{
            threadArr1.calc();
        });

        Thread thr2 = new Thread(()->{
            threadArr2.calc();
        });

        thr1.start();
        thr2.start();
        thr1.join();
        thr2.join();

        threadArr.load(threadArr1.getArray(),0,h, 0);
        threadArr.load(threadArr1.getArray(),0,h, h);

        long b = System.currentTimeMillis();
        System.out.println("thread test time - " + (b - a));

    }

    public static void main(String[] args) throws InterruptedException {

        letSimpleTest();
        letThreadTest();

    }

}

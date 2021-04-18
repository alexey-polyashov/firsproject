package gb_less_3_7;

import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) {

        Tester tester = new Tester();

        try {
            Tester tester1 = new Tester();
            tester1.start(Testable1.class);
            Tester tester2 = new Tester();
            tester2.start(Testable2.class);
            Tester tester3 = new Tester();
            tester3.start(Testable3.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}

public class Test {

    byte byteVar = 1;
    short shortVar = 2;
    int intVar = 3;
    long longVar = 4l;

    float floatVar = 0.1f;
    double doubleVar = 1.1;

    boolean boolVar = true;

    char charVar = 'a';
    String stringVar = "это строка";

    public static double  mathCalc(float a, float b, float c, float d){

        return a * (b + (c/d));

    }

    public static boolean checkIn10_20(int i){
        if(i>=10 && i<=20){
            return true;
        } else return false;
    }

    public static void isNegative(int i){
        if(i<0){
            System.out.println("" + i + " - отрицательное число");
        } else System.out.println("" + i + " - положительное число");
    }

    public static String printHello(String name){
        return "Привет, " + name + "!";
    }

    public static void isLeapYear(int year){

        String res;
        if(year % 400 == 0){
            res = "високосный";
        } else if(year % 100 == 0){
            res = "не високосный";
        } else if(year % 4 == 0){
            res = "високосный";
        } else res = "не високосный";

        System.out.println("" + year + " " + res);
    }

    public static void main(String[] args){

        System.out.println("a * (b + (c / d))");
        System.out.println(mathCalc(2.0f, 3.0f, 4.0f, 5.0f));
        System.out.println();
        System.out.println("число 15 в интервале 10..20?");
        System.out.println(checkIn10_20(15));
        System.out.println("число 21 в интервале 10..20?");
        System.out.println(checkIn10_20(15));
        System.out.println();
        isNegative(-4);
        isNegative(5);
        System.out.println();
        System.out.println(printHello("Алексей"));
        System.out.println();
        isLeapYear(1984);
        isLeapYear(1985);
        isLeapYear(1100);
        isLeapYear(1200);

    }


}



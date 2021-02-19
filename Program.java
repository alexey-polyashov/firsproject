import java.util.Random;


public class Program {


    static int myMethod(String[][] arr) throws MyArraySizeException, MyArrayDataException {

        int res = 0;

        if(arr.length != 4){
            throw new MyArraySizeException();
        }

        for (int i = 0; i < 4; i++) {
            if(arr[i].length != 4){
                throw new MyArraySizeException();
            }
            for (int j = 0; j < 4; j++) {
                try{
                    res += Integer.parseInt(arr[i][j]);
                }catch (NumberFormatException e){
                    throw new MyArrayDataException("element " + i + "," + j + " is not a number");
                }finally {

                }

            }
        }

        return res;

    }

    public static void main(String[] args) {

        String[][] arr = new String[4][4];
        Random rand = new Random();


        //вызов без исключений
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                arr[i][j] = Integer.toString(rand.nextInt(10));
                System.out.println(arr[i][j]);
            }
        }
        System.out.println();

        try {
            System.out.println("1:Sum = " + myMethod(arr));
        } catch (MyArraySizeException e) {
            e.printStackTrace();
        } catch (MyArrayDataException e) {
            e.printStackTrace();
        }


        //исключение по преобразованию типов
        arr[1][1] = "a";

        try {
            System.out.println("2:Sum = " + myMethod(arr));
        } catch (MyArraySizeException e) {
            e.printStackTrace();
        } catch (MyArrayDataException e) {
            e.printStackTrace();
        }

        //исключение по преобразованию типов
        String[][] arr2 = new String[4][3];

        try {
            System.out.println("3:Sum = " + myMethod(arr2));
        } catch (MyArraySizeException e) {
            e.printStackTrace();
        } catch (MyArrayDataException e) {
            e.printStackTrace();
        }


    }

}

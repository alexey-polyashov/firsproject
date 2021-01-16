
public class Lesson2 {

    //*6e задание
    public static boolean checkBalance(int[] checkArray){
        boolean result = false;
        int totalSum =0;
        for(int i=0; i < checkArray.length; i++){
            totalSum += checkArray[i];//общая сумма всех элементов
        }
        int lefttSum =0;
        for(int i=0; i < checkArray.length; i++){
            totalSum -= checkArray[i];//подсчет суммы элементов правой части
            lefttSum += checkArray[i];//подсчет суммы элементов левой части
            if(lefttSum == totalSum){
                result = true;
                break;
            }
        }
        return result;
    }

    //**7 задание
    public static int[] arrayShift(int[] array, int shift){

        int toInd = -1;
        int clearShift = shift;
        int buffer = 0;
        int buffer2 = 0;
        int startInd = -1;

        if(Math.abs(shift) > array.length) {
            //если сдвиг больше размера массива,
            //то его нужно уменьшить
            clearShift = shift % array.length;
        }

        if(clearShift == 0){
            //если сдвиг равен 0 или кратен размеру массива, то ничего не изменится
            //поэтому возвращаем исходный массив
            return array;
        }

        for(int ind = 0; ind < array.length; ind++){

            //вычисление нового индекса элемента
            if(toInd == startInd){
                startInd++;
                buffer = array[startInd];
                toInd = startInd;
            }
            toInd = toInd - clearShift;

            //если новый индекс выходит за границы массива,
            //то нужно его вернуть в границы массива
            if(toInd < 0){
                toInd = array.length + toInd;
            }
            if(toInd >= array.length){
                toInd = toInd - array.length;
            }

            //замена элемента с новым индексом на текущий элемент
            buffer2 = array[toInd];
            array[toInd] = buffer;
            buffer = buffer2;

        }

        return array;

    }

    public static void main(String[] args){

        //1 задание
        int[] array1 = {1,1,0,0,1,1,0,0,1};
        for(int i = 0; i < array1.length; i++) {
            if (array1[i] == 0) {
                array1[i] = 1;
            } else {
                array1[i] = 0;
            }
        }

        //2 задание
        int[] array2 = new int[8];
        for(int i = 0; i < array2.length; i++) {
            array2[i] = i * 3;
        }

        //3е задание
        int[] array3 = {1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1 };
        for(int i = 0; i < array3.length; i++) {
            if(array3[i] < 6) {
                array3[i] *= 2;
            }
        }

        //4е задание
        int[][] array4 = new int[8][8];
        for(int i = 0; i < array4.length; i++) {
            for(int j = 0; j < array4[i].length; j++) {
                if(i == j){
                    array4[i][j] = 1;
                    array4[array4.length-i-1][j] = 1;
                }
            }
        }

        //5е задание
        int[] array5 = { 1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
        int maxVal = array5[0];
        int minVal = array5[0];
        for(int i = 0; i < array5.length; i++) {
            if(maxVal < array5[i]){
                maxVal = array5[i];
            }
            if(minVal > array5[i]){
                minVal = array5[i];
            }
        }

        //пример работы 6-го задания
        System.out.println("6-е задание");
        int[] arr = {2, 2, 2, 1, 2, 2, 10, 1};
        System.out.println("{2, 2, 2, 1, 2, 2, 10, 1}" + checkBalance(arr));
        int[] arr2 = {1, 1, 1, 2, 1};
        System.out.println("{1, 1, 1, 2, 1}" + checkBalance(arr2));
        int[] arr3 = {3, 3, 3, 1};
        System.out.println("{3, 3, 3, 1}" + checkBalance(arr3));

        //пример работы 7-го задания
        System.out.println();
        System.out.println("7-е задание");
        int[] arr5 = {1, 2, 3, 4, 5, 6, 7, 8};
        arrayShift(arr5, 5);
        for(int i=0;i<arr5.length;i++)System.out.print(""+arr5[i]+", ");
        System.out.println();
        arrayShift(arr5, -5);
        for(int i=0;i<arr5.length;i++)System.out.print(""+arr5[i]+", ");
        System.out.println();
        arrayShift(arr5, 10);
        for(int i=0;i<arr5.length;i++)System.out.print(""+arr5[i]+", ");


    }

}

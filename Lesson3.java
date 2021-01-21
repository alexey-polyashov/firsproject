import java.util.Random;
import java.util.Scanner;

public class Lesson3 {

    public static Scanner scan = new Scanner(System.in);

    public static void guesNumber(){

        int num;
        Random random = new Random();
        int oneMore;
        int userNum = 0;

        //цикл повторения игр
        do {
            System.out.println("Угадайте число от 0 до 9. У вас 3 попытки.");
            num = random.nextInt(9);

            //цикл угадывания
            for (int i = 1; i <= 4; i++) {

                if(i==4){
                    System.out.println("Вы проиграли!");
                    break;
                }

                System.out.println(i + "я Попытка. Введите число:");

                if (scan.hasNextInt()) {

                    userNum = scan.nextInt();
                    if(userNum == num){
                        System.out.println("Поздравляю! Вы угадали");
                        break;
                    } else if(userNum < num) {
                        System.out.println("Ваше число меньше");
                    } else {
                        System.out.println("Ваше число больше");
                    }

                } else {

                    System.out.println("Введенный символ не является числом!");
                    scan.nextLine();

                }
            }

            //получим ответ на вопрос о продолжении игры
            oneMore = -1;
            do {
                System.out.println("Повторить игру еще раз? 1-да / 0-нет:");
                if (scan.hasNextInt()) {
                    oneMore = scan.nextInt();
                    if (oneMore != 1 && oneMore != 0 ) {
                        oneMore = -1;
                    }
                } else {
                    scan.nextLine();
                }
            } while (oneMore == -1);

        } while (oneMore == 1);

    }

    public static void guesWord(){

        String[] words = {"apple", "lemon"," banana", "apricot", "avocado", "roccoli", "carrot", "cherry", "garlic", "melon",
        "leak", "kiwi", "mango", "mushroom", "nut", "olive", "pea", "peanut", "pear", "pepper", "pineapple", "pumpkin", "potato"};

        Random random = new Random();
        int index = random.nextInt(words.length-1);
        String word = "";

        System.out.println("Угадайте слово.");

        boolean isLucky = false;
        String currentWord = words[index];
        int maxIndex = currentWord.length() - 1;

        do {
            System.out.println("Введите слово:");
            word = scan.nextLine();

            int j = 0;
            boolean isPartGues = false;
            boolean isFullGues = maxIndex == (word.length()-1);

            for (j = 0; j <= maxIndex && j < word.length(); j++) {
                if (currentWord.charAt(j) == word.charAt(j)) {
                    isPartGues = true;
                } else {
                    isFullGues = false;
                    break;
                }
            }

            if (isFullGues) {
                isLucky = true;
                System.out.println("Поздравляю! Вы угадали сово.");
            } else if(isPartGues){
                System.out.println("Вы угадали чаcть слова:");
                for (int i = 0; i <= 15; i++) {
                    if(i <= j-1) {
                        System.out.print(currentWord.charAt(i));
                    } else {
                        System.out.print("#");
                    }
                }
                System.out.print("\n");
            } else {
                System.out.println("Вы не угадали. Попробуйте еще раз.");
            }
        }while(isLucky == false);

    }

    public static void main(String[] args) {

        guesNumber();
        scan.nextLine();
        guesWord();
        scan.close();

    }
}

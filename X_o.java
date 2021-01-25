import java.util.Random;
import java.util.Scanner;

public class X_o {

    static final int n = 5; //размер поля
    static final int lineLength = 4; //количество фишек для выигрыша
    static char[][] field = new char[n][n];//игорвое поле
    static final int countRiskCell = 2;//количество ячеек для прогноза выигрыша
    static final char EMPTY_DOT = '•';
    static final char X_DOT = 'Х';
    static final char O_DOT = '0';
    static int xCoord = -1;
    static int yCoord = -1;
    static int lastX = -1;
    static int lastY = -1;
    static int tipX = -1;
    static int tipY = -1;
    static Random rand = new Random();
    static int lastCompX = rand.nextInt(n);
    static int lastCompY = rand.nextInt(n);

    static Scanner scan = new Scanner(System.in);


    private static void drawField(){
        System.out.println("------");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(field[i][j]+ " ");
            }
            System.out.println();
        }
        System.out.println("------");
    }

    private static boolean isValidCell(int x, int y){
        if(x > -1 && x < n && y> -1 && y < n && field[y][x] == EMPTY_DOT){
            return true;
        }
        return false;
    }

    private static void humanTurn(){
        xCoord = -1;
        yCoord = -1;
        do {
            System.out.println("Ваш ход (x пробел y):");
            if (scan.hasNextInt()) {
                xCoord = scan.nextInt();
            }
            if (scan.hasNextInt()) {
                yCoord = scan.nextInt();
            }
            scan.nextLine();
            if(!isValidCell( xCoord, yCoord)){
                System.out.println("Эта ячейка занята");
                xCoord = -1;
                yCoord = -1;
            }
        }while(xCoord == -1 || yCoord == -1);
        field[yCoord][xCoord] = X_DOT;
    }

    private static void computerTurn() {
        int tip[] = findNextTurn(X_DOT, xCoord, yCoord);
        //используем подсказку, если она есть
        if(tip[0]>-1 && tip[1]>-1){
            xCoord=tip[0];
            yCoord=tip[1];
        }else{
            //попытка хода рядом с последним своим ходом
            xCoord = lastCompX + 1 - rand.nextInt(3);
            yCoord = lastCompY + 1 - rand.nextInt(3);
            if (isValidCell(xCoord, yCoord) != true) {
                do {
                    //случайный ход
                    xCoord = rand.nextInt(n);
                    yCoord = rand.nextInt(n);
                } while (isValidCell(xCoord, yCoord) != true);
            }
        }
        //запомним последний ход
        lastCompX = xCoord;
        lastCompY = yCoord;
        field[yCoord][xCoord] = O_DOT;
    }

    private static boolean checkWin(char symbol, int baseX, int baseY){

        //базовая точка отсчета
        boolean isWin = false;
        int nextX = 0;
        int nextY = 0;

        //сканирование области рядом с последним ходом
        for (int incrY = -1; incrY < 2; incrY++) {
            for (int incrX = -1; incrX < 2; incrX++) {
                if(incrY==0 && incrX==0){
                    break;
                }
                isWin = true;
                for(int shift = 0; shift < lineLength; shift++){
                    nextX = shift*incrX + baseX;
                    nextY = shift*incrY + baseY;
                    if(nextX < 0 || nextX >= n || nextY < 0 || nextY >= n ){
                        isWin = false;
                        break;
                    }
                    if(field[nextY][nextX] != symbol){
                        isWin = false;
                        break;
                    }
                }
                if(isWin == true){break;}
            }
            if(isWin == true){break;}
        }

        return isWin;
    }

    private static int[] findNextTurn(char symbol, int baseX, int baseY){

        //базовая точка отсчета
        int result[] = {-1,-1};
        int symbolCount = 0;
        int nextX = 0;
        int nextY = 0;

        for (int incrY = -1; incrY < 2; incrY++) {
            for (int incrX = -1; incrX < 2; incrX++) {
                if(incrY==0 && incrX==0){
                    break;
                }
                symbolCount = 0;
                for(int shift = 0; shift < lineLength; shift++){
                    nextX = shift * incrX + baseX;
                    nextY = shift * incrY + baseY;
                    if(nextX < 0 || nextX >= n || nextY < 0 || nextY >= n ){
                        result[0] = -1;
                        symbolCount =0;
                        break;
                    }
                    if(field[nextY][nextX] != symbol && field[nextY][nextX] != EMPTY_DOT){
                        result[0] = -1;
                        symbolCount =0;
                        break;
                    }
                    if(field[nextY][nextX] != symbol){
                        result[0] = nextX;
                        result[1] = nextY;
                        if(symbolCount == countRiskCell){
                            break;
                        }
                    }else{
                        symbolCount++;
                    }
                }
                if(symbolCount == countRiskCell){break;}
            }
            if(symbolCount == countRiskCell){break;}
        }

        if(symbolCount != countRiskCell) {
            result[0] = -1;
        }

        return result;

    }

    public static void main(String[] args) {

        //заполнение поля
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                field[i][j] = EMPTY_DOT;
            }
        }

        for(int s = 0; s < n*n; s++){

            //вывод поля
            drawField();

            //ожидание хода
            humanTurn();

            //проверка победа
            if (checkWin(X_DOT, xCoord, yCoord) == true) {
                drawField();
                System.out.println("Вы победили!!!");
                break;
            }

            //ход компьютера
            computerTurn();

            if (checkWin(O_DOT, xCoord, yCoord) == true) {
                drawField();
                System.out.println("Компьютер победил :(");
                break;
            }

        };

        scan.close();

    }

}

import java.util.Random;

public class Plate {

    private final int capacity;//емкость
    private int rest; //остаток еды

    public Plate(){
        Random rand = new Random();
        int salt = rand.nextInt(50);
        this.capacity = 200 + salt; //при создании будет случайная емкость
    }

    //наполнить тарелку полностью
    public void fillPlate(){
        rest = capacity;
    }

    //наполнить тарелку определенным количеством порций
    public void fillPlate(int amount){
        rest+= amount;
        if(rest>capacity){
            rest=capacity;
        }
    }

    public int getRest(){
        return rest;
    }

    //покормить
    public void feed(int amount){
        if(rest<amount){
            rest = 0;
        }else{
            rest-=amount;
        }
    }

    public void info(){
        System.out.println("Сейчас в тарелке " + rest + " порций еды");
    }
}

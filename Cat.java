import java.util.Random;

public class Cat {

    private static int numOfInstance;
    private int currentNumber;
    private boolean satiety;
    private final int volume; //прожорливость

    public Cat(){
        this.currentNumber = Cat.numOfInstance;
        Cat.numOfInstance++;
        Random rand = new Random();
        int salt = rand.nextInt(5);
        this.volume = 15 + salt; //при создании кота будет различный уровень прожорливости
    }

    public void eat(Plate pl) {
        if(pl.getRest()>=volume){
            pl.feed(volume);
            satiety = true;
            System.out.println("Кот " + currentNumber + " съел " + volume + " порций, наелся.");
        }else{
            System.out.println("Коту " + currentNumber + " не хватило еды.");
        }
    }

    public void info(){
        if(satiety){
            System.out.println("Кот " + currentNumber + " сыт");
        }else{
            System.out.println("Кот " + currentNumber + " голоден");
        }

    }
}

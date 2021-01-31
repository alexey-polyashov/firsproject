import java.lang.String;
public class Cat extends Animal{

    static int numOfInstance;
    String name;

    Cat(String name){
        this.name = name;
        this.numOfInstance++;
    }

    @Override
    public void swim(int distance){
        System.out.println(name + " не умеет плавать");
    }

    @Override
    public void run(int distance){
        if(distance>200){
            System.out.println(name + " пробежал 200 метров и устал");
        }else {
            System.out.println(name + " пробежал " + distance + " метров");
        }
    }

}

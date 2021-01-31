import java.lang.String;

public class Dog extends Animal {

    static int numOfInstance;
    String name;

    Dog(String name){
        this.name = name;
        this.numOfInstance++;
    }

    @Override
    public void swim(int distance){
        if(die){
            System.out.println(name + " не может плыть потому что умер");
        } else if(distance>10 || die){
            System.out.println(name + " проплыл 10 метров и утонул");
            die = true;
        }else {
            System.out.println(name + " проплыл " + distance + " метров");
        }
    }

    @Override
    public void run(int distance){
        if(die){
            System.out.println(name + " не может бежать потому что умер");
        }else if(distance>500){
            System.out.println(name + " пробежал 500 метров и устал");
        }else {
            System.out.println(name + " пробежал " + distance + " метров");
        }
    }

}

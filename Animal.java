public class Animal {

    static int numOfInstance;
    boolean die;

    Animal(){
        numOfInstance++;
    }

    public void swim(int distance){
        System.out.println("Животное пробежало " + distance + " метров");
    }

    public void run(int distance){
        System.out.println("Животное проплыло " + distance + " метров");
    }

}

import java.lang.String;

public class Main {

    public static void main(String[] args) {

        Cat cat1 = new Cat("Мурзик");
        Cat cat2 = new Cat("Барсик");
        Dog dog1 = new Dog("Бобик");
        Dog dog2 = new Dog("Лайка");
        Dog dog3 = new Dog("Шарик");

        System.out.println("Коты:");
        cat1.swim(15);
        cat2.run(150);
        System.out.println();

        System.out.println("Собаки:");
        dog1.swim(5);
        dog2.run(800);
        dog1.swim(5);
        dog2.swim(20);
        dog2.run(150);
        System.out.println();

        System.out.println("Количество особей:");
        System.out.println("Всего животных " + Animal.numOfInstance);
        System.out.println("Количество котов " + Cat.numOfInstance);
        System.out.println("Количество собак " + Dog.numOfInstance);

    }

}

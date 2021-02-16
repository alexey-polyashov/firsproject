import java.util.Random;

public class Programm {

    public static void main(String[] args) {

        Random rand = new Random();

        Object member[] = new Object[6];
        TrackElement element[] = new TrackElement[10];

        member[0] = new Cat("Барсик");
        member[1] = new Man("Вася");
        member[2] = new Robot("Андроид");
        member[3] = new Cat("Рыжик");
        member[4] = new Man("Петя");
        member[5] = new Robot("Электрон");

        for (int i = 0; i < 5; i++) {
            element[i] = new Track();
            element[i].setLength(1+rand.nextInt(600));
            element[i+1] = new Wall();
            element[i+1].setLength(1+rand.nextInt(6));
        }

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 6; j++) {

                if (element[i] instanceof Wall) {
                    if (member[j] instanceof Robot) {
                        ((Robot) member[j]).jump(element[i]);
                    } else if (member[j] instanceof Man) {
                        ((Man) member[j]).jump(element[i]);
                    } else if (member[j] instanceof Cat) {
                        ((Cat) member[j]).jump(element[i]);
                    }
                }

                if (element[i] instanceof Track) {
                    if (member[j] instanceof Robot) {
                        ((Robot) member[j]).run(element[i]);
                    } else if (member[j] instanceof Man) {
                        ((Man) member[j]).run(element[i]);
                    } else if (member[j] instanceof Cat) {
                        ((Cat) member[j]).run(element[i]);
                    }
                }

            }

            System.out.println();

        }

    }
}

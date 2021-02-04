public class Main {

    static Plate plate = new Plate();
    static int numOfCats = 15;
    static Cat[] cats = new Cat[numOfCats];

    public static void main(String[] args) {

        plate.fillPlate();
        plate.info();
        System.out.println();

        int i;

        for (i = 0; i < numOfCats; i++) {
            cats[i] = new Cat();
        }

        for (i = 0; i < numOfCats; i++) {
            cats[i].eat(plate);
            cats[i].info();
        }

        System.out.println();
        plate.info();

    }

}

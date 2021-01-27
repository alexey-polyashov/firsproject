public class Main {

    public static void main(String[] args) {

        Employee[] employers = new Employee[5];

        employers[0] = new Employee("Сотр1", "должность1", "q@w.ru", "111-22-33", 20000, 30);
        employers[1] = new Employee("Сотр2", "должность2", "w@w.ru", "112-22-33", 40000, 40);
        employers[2] = new Employee("Сотр3", "должность3", "e@w.ru", "113-22-33", 60000, 45);
        employers[3] = new Employee("Сотр4", "должность4", "r@w.ru", "114-22-33", 80000, 42);
        employers[4] = new Employee("Сотр5", "должность5", "t@w.ru", "115-22-33", 30000, 25);

        for (int i = 0; i < employers.length; i++) {
            if (employers[i].getAge() > 40) {
                employers[i].getInfo();
                System.out.println("------------");
            }
        }

    }

}

package gb_less_3_7;

public class Testable1 {

    @BeforeSuite
    public void initTest(){
        System.out.println("Before suite 1");
    }

    @Test(priority = 1)
    public void test1() {
        System.out.println("test 1_1");
    }

    @Test(priority = 2)
    public void test2() {
        System.out.println("test 1_2");
    }

    @Test(priority = 3)
    public void test3() {
        System.out.println("test 1_3");
    }

    @AfterSuite
    public void closeTest(){
        System.out.println("Close test 1");
    }

}

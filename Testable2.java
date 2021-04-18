package gb_less_3_7;

public class Testable2 {


    @BeforeSuite
    public void initTest(){
        System.out.println("Before suite 2");
    }

    @Test(priority = 1)
    public void test1() {
        System.out.println("test 2_1");
    }

    @Test(priority = 1)
    public void test2() {
        System.out.println("test 2_2");
    }

    @Test(priority = 1)
    public void test3() {
        System.out.println("test 2_3");
    }

    @AfterSuite
    public void closeTest(){
        System.out.println("Close test 2");
    }

}

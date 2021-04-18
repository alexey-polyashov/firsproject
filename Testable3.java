package gb_less_3_7;

public class Testable3 {

    @BeforeSuite
    public void initTest(){
        System.out.println("Before suite - initTest");
    }

    @Test(priority = 1)
    public void test1() {
        System.out.println("test 3_1");
    }

    @Test(priority = 1)
    public void test2() {
        System.out.println("test 3_2");
    }

    @Test(priority = 1)
    public void test3() {
        System.out.println("test 3_3");
    }

    @AfterSuite
    public void closeTest(){
        System.out.println("Close test 3");
    }

    @BeforeSuite
    public void initTest2(){
        System.out.println("Before suite - initTest2");
    }

}

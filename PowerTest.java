public class PowerTest {

    public static void main(String[] args) {
        System.out.println(new PowerTest().power(2, 8));
        System.out.println(new PowerTest().power(3, 3));
        System.out.println(new PowerTest().power(10, -2));
    }

    public double power(int base, int exp){
        if(exp==0){
            return 1;
        }
        if(base==1){
            return 1;
        }
        if(base==0){
            return 0;
        }
        if(exp<0) {
            return power(base, exp + 1) / base;
        }
        else{
            return base * power(base, exp - 1);
        }
    }

}

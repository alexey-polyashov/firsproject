import java.util.ArrayList;
import java.util.List;

public class Box<T extends Fruit> {

    private List<T> content = new ArrayList<>();

    public double getWeight(){
        double summ = 0;
        for (T fr: content) {
            summ += fr.getWeight();
        }
        return summ;
    }

    public boolean compare(Box<?> o) {
        if(this.getWeight()==o.getWeight()){
            return true;
        }else{
            return false;
        }
    }

    public void pour(Box<T> dst){
        for (T fr: content) {
            dst.add(fr);
        }
        content.clear();
    }

    public void add(T fr){
        content.add(fr);
    }
}

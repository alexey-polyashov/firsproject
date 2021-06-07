package packpack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BackPackTest {

    final int capacity = 4;

    int optimalCost;
    int calcVal;
    ArrayList<Thing> optimalSet;
    Thing[] things = new Thing[5];

    public int getOptimalCost() {
        return optimalCost;
    }

    public BackPackTest(Thing[] things) {
        this.optimalCost = 0;
        this.calcVal = 0;
        this.things = things;
    }

    public Thing[] getOptimalSet(){

        this.optimalCost = 0;
        this.calcVal = 0;

        for (int i = 0; i < things.length-1; i++) {
            for (int j = 0; j < things.length; j++) {
                getOptimalSet(things.length);
                Thing buf = things[i];
                things[i+1] = things[i];
                things[i] = buf;
            }
        }

        return optimalSet.toArray(new Thing[0]);

    }

    private ArrayList<Thing> getOptimalSet(int ind){

        if(ind<0){
            return new ArrayList<Thing>();
        }else{
            ArrayList<Thing> curSet = getOptimalSet(ind-1);
            if(ind == things.length){
                calcVal = 0;
                int calcCost = 0;
                for (Thing th: curSet) {
                    calcCost+=th.getCost();
                }
                if(optimalCost<calcCost){
                    optimalSet = curSet;
                    optimalCost = calcCost;
                }
            }else if(things[ind].getVal() <= (capacity-calcVal)){
                calcVal+=things[ind].getVal();
                curSet.add(things[ind]);
            }
            return curSet;
        }

    }

    public static void main(String[] args) {


        Thing[] th = new Thing[5];
        th[0] = new Thing(1,5, 200);
        th[1] = new Thing(2,4, 250);
        th[2] = new Thing(3,3, 100);
        th[3] = new Thing(4,2, 50);
        th[4] = new Thing(5,1, 200);

        BackPackTest bp = new BackPackTest(th);
        Thing[] things = bp.getOptimalSet();
        for (Thing thing:things) {
            System.out.println("Thing:" + thing.getNum() + ", weight:" + thing.getVal());
        }
        System.out.println("Total cost: " + bp.getOptimalCost());

    }

}

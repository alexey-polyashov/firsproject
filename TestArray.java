
public class TestArray {

    private float[] arr;
    private int size;

    TestArray(int size){
        this.size = size;
        arr = new float[size];
    }

    public void load(float[] suply, int fromPosition, int size, int toPosition){
        System.arraycopy(suply, fromPosition, arr, toPosition, size);
    }

    public float[] getArray(){
        return arr;
    }

    public int getSize(){
        return size;
    }

    public void fillArray(float number){
        for (int i = 0; i < size; i++) {
            arr[i] = number;
        }
    }

    public void calc(){
        for (int i = 0; i < size; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
    }

}

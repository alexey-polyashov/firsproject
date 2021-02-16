public class Track extends TrackElement{

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public int getLength(){
        return length;
    }

    @Override
    public void setLength(int length){
        this.length = length;
    }

}

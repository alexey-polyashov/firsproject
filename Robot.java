public class Robot {

    private final int maxHeight = 5;
    private final int maxLenght = 500;
    private String name;
    private boolean tired;

    public Robot(String name){
        this.name = name;
    }

    public void run(TrackElement element){
        if(tired){
            System.out.println("Робот " + name + " устал, бежать не может ");
            return;
        }
        if(element instanceof Track){
            if(((Track) element).getLength() <= maxLenght){
                System.out.println("Робот " + name + " пробежал " + ((Track) element).getLength());
            }else{
                tired = true;
                System.out.println("Робот " + name + " не пробежал " + ((Track) element).getLength());
            }
        } else {
            System.out.println("Бежать можно только по беговой дорожке");
        }

    }

    public void jump(TrackElement element){
        if(tired){
            System.out.println("Робот " + name + " устал, прыгать не может ");
            return;
        }
        if(element instanceof Wall){
            if(((Wall) element).getHeight() <= maxLenght){
                System.out.println("Робот " + name + " перепрыгнул " + ((Wall) element).getHeight());
            }else{
                tired = true;
                System.out.println("Робот " + name + " не перепрыгнул " + ((Wall) element).getHeight());
            }
        }
    }

}

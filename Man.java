public class Man {

    private final int maxHeight = 3;
    private final int maxLenght = 300;
    private String name;
    private boolean tired;

    public Man(String name){
        this.name = name;
    }

    public void run(TrackElement element){
        if(tired){
            System.out.println("Человек " + name + " устал, бежать не может ");
            return;
        }
        if(element instanceof Track){
            if(((Track) element).getLength() <= maxLenght){
                System.out.println("Человек " + name + " пробежал " + ((Track) element).getLength());
            }else{
                tired = true;
                System.out.println("Человек " + name + " не пробежал " + ((Track) element).getLength());
            }
        } else {
            System.out.println("Бежать можно только по беговой дорожке");
        }

    }

    public void jump(TrackElement element){
        if(tired){
            System.out.println("Человек " + name + " устал, прыгать не может ");
            return;
        }
        if(element instanceof Wall){
            if(((Wall) element).getHeight() <= maxLenght){
                System.out.println("Человек " + name + " перепрыгнул " + ((Wall) element).getHeight());
            }else{
                tired = true;
                System.out.println("Человек " + name + " не перепрыгнул " + ((Wall) element).getHeight());
            }
        }
    }

}

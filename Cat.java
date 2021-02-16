public class Cat {

    private final int maxHeight = 1;
    private final int maxLenght = 200;
    private String name;
    private boolean tired;

    public Cat(String name){
        this.name = name;
    }

    public void run(TrackElement element){
        if(tired){
            System.out.println("Кот " + name + " устал, бежать не может ");
            return;
        }
        if(element instanceof Track){
            if(((Track) element).getLength() <= maxLenght){
                System.out.println("Кот " + name + " пробежал " + ((Track) element).getLength());
            }else{
                tired = true;
                System.out.println("Кот " + name + " не пробежал " + ((Track) element).getLength());
            }
        } else {
            System.out.println("Бежать можно только по беговой дорожке");
        }

    }

    public void jump(TrackElement element){
        if(tired){
            System.out.println("Кот " + name + " устал, прыгать не может ");
            return;
        }
        if(element instanceof Wall){
            if(((Wall) element).getHeight() <= maxLenght){
                System.out.println("Кот " + name + " перепрыгнул " + ((Wall) element).getHeight());
            }else{
                tired = true;
                System.out.println("Кот " + name + " не перепрыгнул " + ((Wall) element).getHeight());
            }
        }
    }

}

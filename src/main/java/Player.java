public class Player extends GameObject{

    private String shape;

    public Player(int x, int y,int oldX,int oldY, int sizeWidth, int sizeHeight, String shape) {
        super(x, y, oldX, oldY, sizeWidth, sizeHeight);
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}

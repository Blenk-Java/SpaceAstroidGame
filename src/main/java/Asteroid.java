public class Asteroid extends GameObject {

    private char shape;
    public Asteroid(int x, int y,int oldX,int oldY, int sizeWidth, int sizeHeight, char shape) {
        super(x, y, oldX, oldY, sizeWidth, sizeHeight);
        this.shape = shape;
    }

    public char getShape() {
        return shape;
    }

    public void setShape(char shape) {
        this.shape = shape;
    }
}


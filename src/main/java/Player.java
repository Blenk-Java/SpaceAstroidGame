public class Player extends GameObject{

    private char[][] shape = {{' ',' ',' ','\\','\\', ' '},
            {'#','#','[','=','=', '>'},
            {' ', ' ',' ','/','/', ' '}};
    private int health;

    public Player(int x, int y,int oldX,int oldY, int sizeWidth, int sizeHeight, int health) {
        super(x, y, oldX, oldY, sizeWidth, sizeHeight);
        this.health = health;
    }

    public char[][] getShape() {
        return shape;
    }

    public void setShape(char[][] shape) {
        this.shape = shape;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}

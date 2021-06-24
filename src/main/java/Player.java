public class Player extends GameObject{

    private char[][] shape = {{' ',' ',' ','\\','\\', ' '},
            {'#','#','[','=','=', '>'},
            {' ', ' ',' ','/','/', ' '}};

    public Player(int x, int y,int oldX,int oldY, int sizeWidth, int sizeHeight) {
        super(x, y, oldX, oldY, sizeWidth, sizeHeight);
    }

    public char[][] getShape() {
        return shape;
    }

    public void setShape(char[][] shape) {
        this.shape = shape;
    }
}

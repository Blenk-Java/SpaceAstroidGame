public abstract class GameObject {

    protected int x;
    protected int y;
    protected int oldX;
    protected int oldY;
    protected int sizeWidth;
    protected int sizeHeight;
    //protected char shape;

    public GameObject(int x, int y,int oldX, int oldY, int sizeWidth, int sizeHeight) {
        this.x = x;
        this.y = y;
        this.oldY = oldY;
        this.oldX = oldX;

        this.sizeWidth = sizeWidth;
        this.sizeHeight = sizeHeight;

    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSizeWidth(int sizeWidth) {
        this.sizeWidth = sizeWidth;
    }

    public void setSizeHeight(int sizeHeight) {
        this.sizeHeight = sizeHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSizeWidth() {
        return sizeWidth;
    }

    public int getSizeHeight() {
        return sizeHeight;
    }

    public int getOldX() {
        return oldX;
    }

    public void setOldX(int oldX) {
        this.oldX = oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public void setOldY(int oldY) {
        this.oldY = oldY;
    }
}

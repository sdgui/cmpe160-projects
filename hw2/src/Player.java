// name surname:Ahmet Mete Atay
// student ID:2023400240

public class Player {
    private double x;
    private double y;
    private final double width=20;
    private final double height=20;
    private double velocityY=0;
    private char direction='r';

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void setDirection(char direction) {
        this.direction = direction;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }

    public void draw(){
        String imageToDraw;
        if (direction=='r') imageToDraw ="misc/ElephantRight.png";
        else imageToDraw ="misc/ElephantLeft.png";
        StdDraw.picture(x,y, imageToDraw,width,height);
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public double getVelocityY() {
        return velocityY;
    }
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

}

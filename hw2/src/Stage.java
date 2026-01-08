// name surname:Ahmet Mete Atay
// student ID:2023400240
import java.awt.Color;
import java.util.Random;
public class Stage {
    private int stageNumber;
    private final double gravity;
    private final double velocityX;
    private final double velocityY;
    private final int rightCode;
    private final int leftCode;
    private final int upCode;
    private final int buttonPressCount;
    private final String clue;
    private final String help;
    private final Color color;
    Random r=new Random();
    public Stage(double gravity, double velocityX, double velocityY,int stageNumber, int rightCode, int leftCode, int upCode,int buttonPressCount, String clue, String help) {
        this.stageNumber = stageNumber;
        this.gravity = gravity;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rightCode = rightCode;
        this.leftCode = leftCode;
        this.upCode = upCode;
        this.buttonPressCount = buttonPressCount;
        this.clue = clue;
        this.help = help;
        this.color = new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255));
    }
    public int getStageNumber() {
        return stageNumber;
    }
    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }
    public double getGravity() {
        return gravity;
    }
    public double getVelocityX() {
        return velocityX;
    }
    public double getVelocityY() {
        return velocityY;
    }
    public int[] getKeyCodes() {
        return new int[]{rightCode,leftCode,upCode};
    }
    public String getClue() {
        return clue;
    }
    public String getHelp() {
        return help;
    }
    public Color getColor() {
        return color;
    }
    public int getButtonPressCount(){
        return buttonPressCount;
    }

}

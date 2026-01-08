// name surname:Ahmet Mete Atay
// student ID:2023400240

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.Font;
public class Game {
    private int stageIndex=0;
    private ArrayList<Stage> stages;
    private int deathNumber=0;
    private long startTime=System.nanoTime();
    private boolean mouseAlreadyPressed=false;
    private String timeText;
    public Game(ArrayList<Stage> stages){
        this.stages = stages;

    }

    public void play(){
        Player player=new Player(130,450);
        Map map =new Map(stages.get(stageIndex),player);
        boolean helpPressed=false;
        while(stageIndex<stages.size()){
            //checks for button interactions when mouse is pressed
            if (StdDraw.isMousePressed()) {
                if (!mouseAlreadyPressed) {
                    mouseAlreadyPressed = true;
                    double mouseX = StdDraw.mouseX();
                    double mouseY = StdDraw.mouseY();
                    // restart button
                    if (mouseX >= 510 && mouseX <= 590 && mouseY >= 70 && mouseY <= 100) {
                        map.restartStage();
                    }
                    //help button
                    if (mouseX >= 210 && mouseX <= 290 && mouseY >= 70 && mouseY <= 100) {
                        helpPressed=true;
                    }
                    //reset button
                    if (mouseX >= 320 && mouseX <= 480 && mouseY >= 5 && mouseY <= 35){

                        StdDraw.setPenColor(StdDraw.GREEN);
                        StdDraw.filledRectangle(400, 300, 400, 100);
                        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 44));
                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.text(400,300,"Resetting the game...");
                        StdDraw.setFont();
                        StdDraw.show();
                        StdDraw.pause(2000);
                        resetGame();
                        break;
                    }
                }
            }
            else {
                mouseAlreadyPressed=false;
            }
            handleInput(map);

            long milliseconds=time(startTime)/10000000;
            long seconds=milliseconds/100;
            long minutes=seconds/60;
            timeText=String.format("%02d : %02d : %02d",minutes,seconds%60,milliseconds%100);
            if (map.getIsDead()) deathNumber++;
            map.setIsDead(false);
            map.updatePlayerHeight();
            StdDraw.clear();
            StdDraw.setPenColor(new Color(56, 93, 172)); // Color of the area
            StdDraw.filledRectangle(400, 60, 400, 60); // Drawing bottom part
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(250,85,"Help");
            StdDraw.rectangle(250,85,40,15); // Help button
            StdDraw.text(550,85,"Restart");
            StdDraw.rectangle(550,85,40,15); // Restart button
            StdDraw.text(400,20,"RESET THE GAME");
            StdDraw.rectangle(400,20,80,15); // Reset button
            StdDraw.text(700, 75, "Deaths: "+deathNumber);
            StdDraw.text(700, 50, "Stage: "+(stageIndex+1));
            StdDraw.text(100, 50, timeText);
            StdDraw.text(100,75, "Level: 1");
            if (!helpPressed){
                StdDraw.text(400, 85, "Clue:");
                StdDraw.text(400, 55, getStage().getClue());
            }
            else{
                StdDraw.text(400, 85, "Help:");
                StdDraw.text(400, 55, getStage().getHelp());
            }
            map.draw();
            if (map.changeState()){
                stageIndex++;
                if (stageIndex==stages.size()) break;
                stageChangeText();
                StdDraw.pause(2000);
                break;
            }
            StdDraw.show();
            StdDraw.pause(20);
        }
        if (stageIndex<stages.size()){
            play();
        }
        else {
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.filledRectangle(400, 300, 400, 100);
            StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 34));
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(400,330,"CONGRATULATIONS YOU FINISHED THE LEVEL");
            StdDraw.text(400,270,"PRESS A TO PLAY AGAIN");
            StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 18));
            StdDraw.text(400,235,"You finished with " + deathNumber + " deaths in "+ timeText);
            StdDraw.setFont();
            StdDraw.show();
            while (true){
                if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
                    resetGame();
                    break;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_Q)){
                    System.exit(0);
                }
            }
            play();
        }

    }
    private void handleInput(Map map){
        //gets the input for player movement
        if (StdDraw.isKeyPressed(map.getStage().getKeyCodes()[0])){
            map.movePlayer('r');
        }
        if (StdDraw.isKeyPressed(map.getStage().getKeyCodes()[1])){
            map.movePlayer('l');
        }
        if (StdDraw.isKeyPressed(map.getStage().getKeyCodes()[2]) ||map.getStage().getKeyCodes()[2]==-1){
            map.movePlayer('u');
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)){
            map.getPlayer().setDirection('l');
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)){
            map.getPlayer().setDirection('r');
        }

    }
    //measures the time passed starting for startTime
    private long time(long startTime){
        return System.nanoTime()-startTime;
    }
    public Stage getStage(){
        return stages.get(stageIndex);
    }
    private void stageChangeText(){
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledRectangle(400, 300, 400, 100);
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 34));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(400,330,"You passed the stage");
        StdDraw.text(400,270,"But is the level over?!");
        StdDraw.setFont();
        StdDraw.show();
    }
    private void resetGame(){
        deathNumber=0;
        stageIndex=0;
        startTime = System.nanoTime();
    }
}

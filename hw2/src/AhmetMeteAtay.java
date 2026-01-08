// name surname:Ahmet Mete Atay
// student ID:2023400240

import java.util.ArrayList;
import java.awt.event.KeyEvent;
public class AhmetMeteAtay {
    public static void main(String[] args){



        int nullButton = -1;

        //Given Stages
        Stage s1 = new Stage(-0.45, 3.65,10,0,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_UP,1,"Arrow keys are required","Arrow keys move player ,press button and enter the second pipe"); // normal game
        Stage s2 = new Stage(-0.45, 3.65,10,1,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_UP,1,"Not always straight forward","Right and left buttons reversed"); // Reversed Buttons
        Stage s3 = new Stage(-2.3, 3.65, 24,2,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,nullButton,1,"A bit bouncy here","You jump constantly"); // bouncing
        Stage s4 = new Stage(-0.45, 3.65,10,3,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_UP,5,"Never gonna give you up","Press button 5 times "); //
        // Add a new stage here
        Stage s5 = new Stage(-0.45, 3.65,10,4,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_UP,1,"Do you remember?","Obstacles and spikes are the same, but invisible "); //


        // Add the stages to the arraylist
        ArrayList<Stage> stages = new ArrayList<Stage>();
        stages.add(s1);
        stages.add(s2);
        stages.add(s3);
        stages.add(s4);
        stages.add(s5);



        // Draw the game area
        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(0, 600);
        StdDraw.enableDoubleBuffering();

        Game game = new Game(stages);
        game.play();


    }
}

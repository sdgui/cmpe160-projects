// name surname:Ahmet Mete Atay
// student ID:2023400240

public class Map {
    private final Stage stage;
    private final Player player;
    private int buttonPressNum;
    private boolean isDoorOpen;
    private boolean isOnButton;
    private final int[] initialPos={130,450};
    private boolean isDead=false;
    // Obstacles List (formant is int[] = {xLeftDown , yLeftDown, xRightUp, yRightUp}
    private final int[][] obstacles = {
            new int[]{0, 120, 120, 270}, new int[]{0, 270, 168, 330},
            new int[]{0, 330, 30, 480}, new int[]{0, 480, 180, 600},
            new int[]{180, 570, 680, 600}, new int[]{270, 540, 300, 570},
            new int[]{590, 540, 620, 570}, new int[]{680, 510, 800, 600},
            new int[]{710, 450, 800, 510}, new int[]{740, 420, 800, 450},
            new int[]{770, 300, 800, 420}, new int[]{680, 240, 800, 300},
            new int[]{680, 300, 710, 330}, new int[]{770, 180, 800, 240},
            new int[]{0, 120, 800, 150}, new int[]{560, 150, 800, 180},
            new int[]{530, 180, 590, 210}, new int[]{530, 210, 560, 240},
            new int[]{320, 150, 440, 210}, new int[]{350, 210, 440, 270},
            new int[]{220, 270, 310, 300}, new int[]{360, 360, 480, 390},
            new int[]{530, 310, 590, 340}, new int[]{560, 400, 620, 430}};
    // Button Coordinates
    private final int[] button = new int[]{400, 390, 470, 410};
    // Button Floor Coordinates
    private final int[] buttonFloor = new int[]{400, 390, 470, 400};
    // Start Pipe Coordinates for Drawing
    private final int[][] startPipe = {new int[]{115, 450, 145, 480},
            new int[]{110, 430, 150, 450}};
    // Exit Pipe Coordinates for Drawing
    private final int[][] exitPipe = {new int[]{720, 175, 740, 215},
                                new int[]{740, 180, 770, 210}};
    // Door Coordinates
    private final int doorTopPos=240;
    private int[] door = new int[]{685, 180, 700, 240};
    // Coordinates of spike areas
    private final int[][] spikes = {
            new int[]{30, 333, 50, 423}, new int[]{121, 150, 207, 170},
            new int[]{441, 150, 557, 170}, new int[]{591, 180, 621, 200},
            new int[]{750, 301, 769, 419}, new int[]{680, 490, 710, 510},
            new int[]{401, 550, 521, 570}};
    private final int[] spikeAngles={90,180,180,180,270,0,0};

    public Map(Stage stage, Player player) {
        this.stage = stage;
        this.player = player;
    }
    public void movePlayer(char direction) {
        if (direction != 'u') {
            //moves the player horizontally
            if (direction == 'r') {
                for (int[] spike : spikes) {
                    //ends the game if there is a collision with a spike
                    if (checkCollision(player.getX() + stage.getVelocityX(), player.getY(), spike)) {
                        restartStage();
                    }
                }
                for (int[] obstacle : obstacles) {
                    //prevents the player from moving if there is an obstacle in the way
                    if (checkCollision(player.getX() + stage.getVelocityX(), player.getY(), obstacle)) {
                        return;
                    }
                }
                //checks the collisions with the door
                if (!isDoorOpen && checkCollision(player.getX() + stage.getVelocityX(), player.getY(), door)) {
                    System.out.println("cant move");
                    return;
                }
                //increments the button press count by 1 every time the player gets on it
                //once the player contacts with the button, the variable isOnButton is set to true,and player has to exit the button are to be able to press it again
                if (!isOnButton && checkCollision(player.getX() + stage.getVelocityX(), player.getY(), button)) {
                    System.out.println("button");
                    buttonPressNum +=1;
                    isOnButton=true;
                    if (buttonPressNum ==stage.getButtonPressCount()) isDoorOpen=true;
                }

                if (isOnButton && !checkCollision(player.getX() + stage.getVelocityX(), player.getY(), button)) isOnButton=false;

                player.setX(player.getX() + stage.getVelocityX());
            }
            if (direction == 'l') {
                for (int[] spike : spikes) {
                    if (checkCollision(player.getX() - stage.getVelocityX(), player.getY(), spike)) {
                        System.out.println("game over");
                        restartStage();
                    }
                }
                for (int[] obstacle : obstacles) {
                    //prevents the player from moving if there is an obstacle in the way
                    if (checkCollision(player.getX() - stage.getVelocityX(), player.getY(), obstacle)) {
                        System.out.println("cant move");
                        return;
                    }
                }
                //checks the collisions with the door
                if (!isDoorOpen && checkCollision(player.getX() - stage.getVelocityX(), player.getY(), door)) {
                    System.out.println("cant move");
                    return;
                }
                if (!isOnButton && checkCollision(player.getX() - stage.getVelocityX(), player.getY(), button)) {
                    System.out.println("button");
                    buttonPressNum +=1;
                    isOnButton=true;
                    if (buttonPressNum==stage.getButtonPressCount()) isDoorOpen=true;
                }
                //increments the button press count by 1 every time the player gets on it
                //once the player contacts with the button, the variable isOnButton is set to true,and player has to exit the button are to be able to press it again
                if (isOnButton && !checkCollision(player.getX() - stage.getVelocityX(), player.getY(), button)) isOnButton=false;

                player.setX(player.getX() - stage.getVelocityX());

            }
        }
        else if (isOnSurface(player.getX(), player.getY(), obstacles)){
                player.setVelocityY(stage.getVelocityY());
            }
    }

    private boolean checkCollision(double nextX, double nextY, int[] obstacle){
        if (nextX+player.getWidth()/2>=obstacle[0] && nextX-player.getWidth()/2<=obstacle[2] && nextY+player.getHeight()/2>obstacle[1] && nextY- player.getHeight()/2<obstacle[3]){
            return true;
        }
        else return false;
    }
    private boolean isOnSurface(double x, double y, int[][] obstacles){
        for (int[] obstacle : obstacles) {
            if (x + player.getWidth() / 2 >= obstacle[0] && x - player.getWidth() / 2 <= obstacle[2] && y - player.getHeight() / 2 == obstacle[3]) {
                return true;
            }
        }
        return false;
    }
    //updates the player's height according to gravity and velocityY
    public void updatePlayerHeight() {

        double currentY = player.getY();
        double currentVelocityY = player.getVelocityY();
        double nextY = currentY + currentVelocityY;

        // check for collisions at the predicted next position
        boolean collisionDetected = false;
        double highestCollisionY = -Double.MAX_VALUE; // keep track of the top edge of the highest obstacle we'd hit
        if (!isOnButton && checkCollision(player.getX() , nextY, button)) {
            buttonPressNum +=1;
            isOnButton=true;
            if (buttonPressNum==stage.getButtonPressCount()) isDoorOpen=true;
        }
        if (isOnButton && !checkCollision(player.getX() , nextY, button)) isOnButton=false;
        // check against obstacles
        for (int[] obstacle : obstacles) {
            // use the predicted nextY for collision check
            if (checkCollision(player.getX(), nextY, obstacle)) {
                collisionDetected = true;
                // Assuming obstacle[1] is the top Y-coordinate of the obstacle
                // We want the highest 'top edge' among all colliding obstacles
                if (obstacle[3] > highestCollisionY) {
                    if (player.getY() > obstacle[3]) {
                        highestCollisionY = obstacle[3];//top height
                    }
                    else{
                        highestCollisionY = obstacle[1]-player.getHeight();//bottom height
                    }
                }
                // Optional optimization: if velocity is downward, and we hit something,
                // we probably don't need to check obstacles much further down.
                // However, this simple loop is fine for fewer obstacles.
            }
        }

        // Check against spikes (add game over logic if needed)
        for (int[] spike : spikes) {
            if (checkCollision(player.getX(), nextY, spike)) {
                // Game Over logic when falling onto a spike
                System.out.println("Game over - fell on spike");
                restartStage();
                // Handle game over state here (e.g., call a gameOver() method)
                return; // Stop further processing if game is over
            }
        }


        // 3. Resolve based on collision check result
        if (collisionDetected) {
            // Place player exactly on top of the highest obstacle hit.
            // Assumes player Y is the center, and Y increases downwards.
            // Player's bottom edge should be at highestCollisionY.
            player.setY(highestCollisionY + player.getHeight() / 2.0); // Adjust Y to be just above the obstacle
            player.setVelocityY(0); // Stop vertical movement
        } else {
            // No collision, proceed with normal movement and gravity.
            player.setY(nextY);
            player.setVelocityY(currentVelocityY + stage.getGravity());
        }
    }
    public boolean changeState(){
        return checkCollision(player.getX(), player.getY(), exitPipe[1]);
    }
    public void restartStage(){
        isDead=true;
        player.setX(initialPos[0]);
        player.setY(initialPos[1]);
        player.setVelocityY(0);
        buttonPressNum=0;
        isDoorOpen=false;
        door[3]=doorTopPos;
    }
    public Stage getStage(){
        return stage;
    }
    public Player getPlayer(){
        return player;
    }
    public void draw(){
        StdDraw.setPenColor(stage.getColor());
        //draws the obstacles
        if (stage.getStageNumber()!=4) {
            for (int[] obstacle : obstacles) {
                StdDraw.filledRectangle((obstacle[0] + obstacle[2]) / 2.0, (obstacle[1] + obstacle[3]) / 2.0, (obstacle[2] - obstacle[0]) / 2.0, (obstacle[3] - obstacle[1]) / 2.0);
            }
            //draws the button and the buttonfloor
            if (!isOnButton) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.filledRectangle((button[0] + button[2]) / 2.0, (button[1] + button[3]) / 2.0, (button[2] - button[0]) / 2.0, (button[3] - button[1]) / 2.0);
            }
            StdDraw.setPenColor(StdDraw.GRAY);
            StdDraw.filledRectangle((buttonFloor[0] + buttonFloor[2]) / 2.0, (buttonFloor[1] + buttonFloor[3]) / 2.0, (buttonFloor[2] - buttonFloor[0]) / 2.0, (buttonFloor[3] - buttonFloor[1]) / 2.0);
            //draws the spikes
            for (int i = 0; i < spikes.length; i++) {
                if (spikeAngles[i] % 180 == 0) {
                    StdDraw.picture((spikes[i][0] + spikes[i][2]) / 2.0, (spikes[i][1] + spikes[i][3]) / 2.0, "misc/Spikes.png", (spikes[i][2] - spikes[i][0]), (spikes[i][3] - spikes[i][1]), spikeAngles[i]);
                } else {
                    StdDraw.picture((spikes[i][0] + spikes[i][2]) / 2.0, (spikes[i][1] + spikes[i][3]) / 2.0, "misc/Spikes.png", (spikes[i][3] - spikes[i][1]), (spikes[i][2] - spikes[i][0]), spikeAngles[i]);
                }
            }
            //makes the door shorter every frame for sliding effect if it's opened.
            if (isDoorOpen && door[3] > door[1]) {
                door[3] -= 3;
            }
            //draws the door
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.filledRectangle((door[0] + door[2]) / 2.0, (door[1] + door[3]) / 2.0, (door[2] - door[0]) / 2.0, (door[3] - door[1]) / 2.0);
        }
        //draws the player
        player.draw();
        //draws the start and exitpipes
        StdDraw.setPenColor(255,188,4);
        for (int[] pipe: startPipe){
            StdDraw.filledRectangle((pipe[0]+pipe[2])/2.0,(pipe[1]+pipe[3])/2.0,(pipe[2]-pipe[0])/2.0,(pipe[3]-pipe[1])/2.0);
        }
        for (int[] pipe: exitPipe){
            StdDraw.filledRectangle((pipe[0]+pipe[2])/2.0,(pipe[1]+pipe[3])/2.0,(pipe[2]-pipe[0])/2.0,(pipe[3]-pipe[1])/2.0);
        }

    }
    public boolean getIsDead(){
        return isDead;
    }
    public void setIsDead(boolean isDead){
        this.isDead = isDead;
    }




}

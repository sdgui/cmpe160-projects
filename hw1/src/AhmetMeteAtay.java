import java.awt.Color;
import java.awt.event.KeyEvent;

public class AhmetMeteAtay {
    public static void main(String[] args) {
        // Canvas properties, scale and set the canvas with the given parameters
        double xScale = 800.0, yScale = 400.0;
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setXscale(0.0, xScale);
        StdDraw.setYscale(0.0, yScale);
        // Color array for bricks (first import java.awt.Color )
        Color[] colors = {new Color(255, 0, 0), new Color(220, 20, 60),
                new Color(178, 34, 34), new Color(139, 0, 0),
                new Color(255, 69, 0), new Color(165, 42, 42)
        };
        // Game Components (These can be changed for custom scenarios)
        double ballRadius = 8; // Ball radius
        double ballVelocity = 5; // Magnitude of the ball velocity
        Color ballColor = new Color(15, 82, 186); // Color of the ball
        double[] initialBallPos = {400, 18}; //Initial position of the ball in the format {x, y}
        double[] paddlePos = {400, 5}; // Initial position of the center of the paddle
        double paddleHalfwidth = 60; // Paddle half width
        double paddleHalfheight = 5; // Paddle half height
        double paddleSpeed = 20; // Paddle speed
        Color paddleColor = new Color(128, 128, 128); // Paddle color
        double brickHalfwidth = 50; // Brick half width
        double brickHalfheight = 10; // Brick half height
        // 2D array to store center coordinates of bricks in the format {x, y}
        double[][] brick_coordinates = new double[][]{
                {250, 320}, {350, 320}, {450, 320}, {550, 320},
                {150, 300}, {250, 300}, {350, 300}, {450, 300}, {550, 300}, {650, 300},
                {50, 280}, {150, 280}, {250, 280}, {350, 280}, {450, 280}, {550, 280}, {650, 280}, {750, 280},
                {50, 260}, {150, 260}, {250, 260}, {350, 260}, {450, 260}, {550, 260}, {650, 260}, {750, 260},
                {50, 240}, {150, 240}, {250, 240}, {350, 240}, {450, 240}, {550, 240}, {650, 240}, {750, 240},
                {150, 220}, {250, 220}, {350, 220}, {450, 220}, {550, 220}, {650, 220},
                {250, 200}, {350, 200}, {450, 200}, {550, 200}};
        // Brick colors
        Color[] brick_colors = new Color[]{
                colors[0], colors[1], colors[2], colors[3],
                colors[2], colors[4], colors[3], colors[0], colors[4], colors[5],
                colors[5], colors[0], colors[1], colors[5], colors[2], colors[3], colors[0], colors[4],
                colors[1], colors[3], colors[2], colors[4], colors[0], colors[5], colors[2], colors[1],
                colors[4], colors[0], colors[5], colors[1], colors[2], colors[3], colors[0], colors[5],
                colors[1], colors[4], colors[0], colors[5], colors[1], colors[2],
                colors[3], colors[2], colors[3], colors[0]};
        StdDraw.enableDoubleBuffering();
        boolean[] isBrickActive = new boolean[brick_coordinates.length];
        for (int i = 0; i < brick_coordinates.length; i++) {
            isBrickActive[i] = true;
        }
        double[] ballPos = initialBallPos.clone();
        boolean gameStarted = false;
        double lineLength = 45;
        double angle = 0.0;
        double radang;
        double[] lineEndPos = new double[2];
        int score = 0;
        boolean gameOver = false;
        boolean cornerJump = false;
        boolean gamePaused = false;
        boolean sameLoop = false;
        final double[][] angleRanges = {{180, 270}, {90, 180}, {270, 360}, {0, 90}};
        double tangent;
        double distancePerpY;
        double distancePerpX;
        double distanceX;
        double distanceY;
        double edgeDistanceX;
        double edgeDistanceY;
        boolean hasCollided=false;
        double distance;
        double[] collisionFix ={0,0};
        int maxScore=440;
        boolean gameWon = false;
        double dist;
        while (true) {
            StdDraw.clear();
            sameLoop = false;
            //Draws the bricks
            for (int i = 0; i < brick_coordinates.length; i++) {
                if (isBrickActive[i]) {
                    StdDraw.setPenColor(brick_colors[i]);
                    StdDraw.filledRectangle(brick_coordinates[i][0], brick_coordinates[i][1], brickHalfwidth, brickHalfheight);
                }
            }

            //Moves the paddle
            if (!gameWon && gameStarted && !gameOver && !gamePaused) {
                radang = Math.toRadians(angle);
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    gamePaused = true;
                    sameLoop = true;
                    StdDraw.pause(200);
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && paddlePos[0] < 800 - paddleHalfwidth) {
                    paddlePos[0] += paddleSpeed;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && paddlePos[0] > paddleHalfwidth) {
                    paddlePos[0] -= paddleSpeed;
                }
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.textRight(780, 370, "Score: " + score);
                //updates the ball position
                radang=Math.toRadians(angle);
                ballPos[0] += ballVelocity * Math.cos(radang);
                ballPos[1] += ballVelocity * Math.sin(radang);
                if (ballPos[0] > xScale - ballRadius || ballPos[0] < ballRadius) {
                    angle = 180 - angle;
                }
                if (ballPos[1] > yScale - ballRadius) {
                    angle = -angle;
                }
                //ends the game if the ball reaches the floor
                else if (ballPos[1] < ballRadius) {
                    gameOver = true;
                }
                //checks the collisions between the ball and paddle

                if (ballPos[0] - ballRadius < paddlePos[0] + paddleHalfwidth && ballPos[0] + ballRadius > paddlePos[0] - paddleHalfwidth && ballPos[1] - ballRadius < paddleHalfheight + paddlePos[1] && ballPos[1] + ballRadius > paddlePos[1] - paddleHalfheight) {
                    //measures the minimum distance between the ball and any edge to determine which collision is going to happen
                    if (ballPos[0] < paddlePos[0] + paddleHalfwidth && ballPos[0] > paddlePos[0] - paddleHalfwidth) {
                        distancePerpY = 0;
                    } else {
                        distancePerpY = Math.min(Math.abs(ballPos[0] - paddlePos[0] + paddleHalfwidth), Math.abs(ballPos[0] - paddlePos[0] - paddleHalfwidth));
                    }
                    if (ballPos[1] < paddlePos[1] + paddleHalfheight && ballPos[1] > paddlePos[1] - paddleHalfheight) {
                        distancePerpX = 0;
                    } else {
                        distancePerpX = Math.min(Math.abs(ballPos[1] - paddlePos[1] + paddleHalfheight), Math.abs(ballPos[1] - paddlePos[1] - paddleHalfheight));
                    }
                    distanceX = Math.min(Math.abs(ballPos[0] - paddlePos[0] + paddleHalfwidth), Math.abs(ballPos[0] - paddlePos[0] - paddleHalfwidth));
                    distanceY = Math.min(Math.abs(ballPos[1] - paddlePos[1] + paddleHalfheight), Math.abs(ballPos[1] - paddlePos[1] - paddleHalfheight));
                    distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                    //if the distance is greater than zero,decreases the distance by radius to get the distance with the surface of the ball
                    //I assigned these to a different variable since I want to use the distance of ball's center later when calculating corner collisions
                    edgeDistanceY = Math.sqrt(distanceY * distanceY + distancePerpY * distancePerpY) - ballRadius;
                    edgeDistanceX = Math.sqrt(distanceX * distanceX + distancePerpX * distancePerpX) - ballRadius;
                    if (edgeDistanceY < 0) edgeDistanceY = 0;
                    if (edgeDistanceX < 0) edgeDistanceX = 0;
                    if (edgeDistanceY < edgeDistanceX) {
                        angle = -angle;
                        //takes the part of the ball that is inside the brick back to make collisions more accurate
                        //I made the ball step back according to non-updated radang first in order to prevent detecting
                        //when the ball is already inside the paddle
                        ballPos[0] += (distanceY - ballRadius) * Math.cos(radang);
                        ballPos[1] += (distanceY - ballRadius) * Math.sin(radang);
                        radang = Math.toRadians(angle);
                        collisionFix[0] -= (distanceY - ballRadius) * Math.cos(radang);
                        collisionFix[1] -= (distanceY - ballRadius) * Math.sin(radang);
                    } else if (edgeDistanceX < edgeDistanceY) {
                        angle = 180 - angle;
                        ballPos[0] += (distanceX - ballRadius) * Math.cos(radang);
                        ballPos[1] += (distanceX - ballRadius) * Math.sin(radang);
                        radang = Math.toRadians(angle);
                        ballPos[0] -= (distanceX - ballRadius) * Math.cos(radang);
                        ballPos[1] -= (distanceX - ballRadius) * Math.sin(radang);
                    } else {
                        System.out.println("edgedistx " + edgeDistanceX + " edgedisty " + edgeDistanceY);
                        System.out.println("corner collision with paddle");

                        ballPos[0] += (distance - ballRadius) * Math.cos(radang);
                        ballPos[1] += (distance - ballRadius) * Math.sin(radang);
                        double[][] paddleCorners = {
                                {paddlePos[0] + paddleHalfwidth, paddlePos[1] + paddleHalfheight},
                                {paddlePos[0] + paddleHalfwidth, paddlePos[1] - paddleHalfheight},
                                {paddlePos[0] - paddleHalfwidth, paddlePos[1] + paddleHalfheight},
                                {paddlePos[0] - paddleHalfwidth, paddlePos[1] - paddleHalfheight}
                        };
                        //finds the closest corner to determine collision
                        double min = Double.MAX_VALUE;
                        int index = 0;
                        for (int j = 0; j < 4; j++) {
                            dist = Math.sqrt(Math.pow(ballPos[0] - paddleCorners[j][0], 2) + Math.pow(ballPos[1] - paddleCorners[j][1], 2));
                            if (dist < min) {
                                min = dist;
                                index = j;
                            }
                        }
                        double[] corner = paddleCorners[index];
                        tangent = (ballPos[1] - corner[1]) / (ballPos[0] - corner[0]);
                        System.out.println("before angle" + angle + " " + tangent);

                        angle = 180 + 2 * Math.toDegrees(Math.atan(tangent)) - angle;
                        System.out.println("after angle" + angle);
                        radang = Math.toRadians(angle);
                        ballPos[0] -= (distance - ballRadius) * Math.cos(radang);
                        ballPos[1] -= (distance - ballRadius) * Math.sin(radang);

                    }
                }
                //checks the collisions between ball and the boxes and removes the box if it is hit.

                for (int i = 0; i < brick_coordinates.length; i++) {
                    if (isBrickActive[i]) {
                        //checks if any part of ball inside a brick
                        if (ballPos[0] - ballRadius <= brick_coordinates[i][0] + brickHalfwidth && ballPos[0] + ballRadius >= brick_coordinates[i][0] - brickHalfwidth && ballPos[1] - ballRadius <= brickHalfheight + brick_coordinates[i][1] && ballPos[1] + ballRadius >= brick_coordinates[i][1] - brickHalfheight) {
                            score += 10;
                            if (!hasCollided) {

                                //measures the minimum distance between the ball and any edge to determine which collision is going to happen
                                if (ballPos[0] < brick_coordinates[i][0] + brickHalfwidth && ballPos[0] > brick_coordinates[i][0] - brickHalfwidth) {
                                    distancePerpY = 0;
                                } else {
                                    distancePerpY = Math.min(Math.abs(ballPos[0] - brick_coordinates[i][0] + brickHalfwidth), Math.abs(ballPos[0] - brick_coordinates[i][0] - brickHalfwidth));
                                }
                                if (ballPos[1] < brick_coordinates[i][1] + brickHalfheight && ballPos[1] > brick_coordinates[i][1] - brickHalfheight) {
                                    distancePerpX = 0;
                                } else {
                                    distancePerpX = Math.min(Math.abs(ballPos[1] - brick_coordinates[i][1] + brickHalfheight), Math.abs(ballPos[1] - brick_coordinates[i][1] - brickHalfheight));
                                }
                                distanceX = Math.min(Math.abs(ballPos[0] - brick_coordinates[i][0] + brickHalfwidth), Math.abs(ballPos[0] - brick_coordinates[i][0] - brickHalfwidth));
                                distanceY = Math.min(Math.abs(ballPos[1] - brick_coordinates[i][1] + brickHalfheight), Math.abs(ballPos[1] - brick_coordinates[i][1] - brickHalfheight));
                                distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                                //if the distance is greater than zero,decreases the distance by radius to get the distance with the surface of the ball
                                //I assigned these to a different variable since I want to use the distance of ball's center later when calculating corner collisions
                                edgeDistanceY = Math.sqrt(distanceY * distanceY + distancePerpY * distancePerpY) - ballRadius;
                                edgeDistanceX = Math.sqrt(distanceX * distanceX + distancePerpX * distancePerpX) - ballRadius;


                                if (edgeDistanceY < 0) edgeDistanceY = 0;
                                if (edgeDistanceX < 0) edgeDistanceX = 0;
                                if (edgeDistanceY < edgeDistanceX) {
                                    angle = -angle;
                                    //takes the part of the ball that is inside the brick back to make collisions more accurate
                                    ballPos[0] += (distanceY - ballRadius) * Math.cos(radang);
                                    ballPos[1] += (distanceY - ballRadius) * Math.sin(radang);
                                    radang = Math.toRadians(angle);
                                    collisionFix[0] = -(distanceY - ballRadius) * Math.cos(radang);
                                    collisionFix[1] = -(distanceY - ballRadius) * Math.sin(radang);
                                }
                                else if (edgeDistanceX < edgeDistanceY) {
                                    angle = 180 - angle;
                                    //takes the part of the ball that is inside the brick back to make collisions more accurate
                                    ballPos[0] += (distanceX - ballRadius) * Math.cos(radang);
                                    ballPos[1] += (distanceX - ballRadius) * Math.sin(radang);
                                    radang = Math.toRadians(angle);
                                    collisionFix[0] = -(distanceX - ballRadius) * Math.cos(radang);
                                    collisionFix[1] = -(distanceX - ballRadius) * Math.sin(radang);
                                }
                                else {
                                    //takes the part of the ball that is inside the brick back to make collisions more accurate
                                    ballPos[0] += (distance - ballRadius) * Math.cos(radang);
                                    ballPos[1] += (distance - ballRadius) * Math.sin(radang);
                                    double[][] brickCorners = {
                                            {brick_coordinates[i][0] + brickHalfwidth, brick_coordinates[i][1] + brickHalfheight},
                                            {brick_coordinates[i][0] + brickHalfwidth, brick_coordinates[i][1] - brickHalfheight},
                                            {brick_coordinates[i][0] - brickHalfwidth, brick_coordinates[i][1] + brickHalfheight},
                                            {brick_coordinates[i][0] - brickHalfwidth, brick_coordinates[i][1] - brickHalfheight}
                                    };
                                    //finds the closest corner to determine collision
                                    double min = Double.MAX_VALUE;
                                    int index = 0;
                                    for (int j = 0; j < 4; j++) {
                                        dist = Math.sqrt(Math.pow(ballPos[0] - brickCorners[j][0], 2) + Math.pow(ballPos[1] - brickCorners[j][1], 2));
                                        if (dist < min) {
                                            min = dist;
                                            index = j;
                                        }
                                    }
                                    double[] corner = brickCorners[index];
                                    tangent = (ballPos[1] - corner[1]) / (ballPos[0] - corner[0]);
                                    angle = 180 + 2 * Math.toDegrees(Math.atan(tangent)) - angle;//changes the angle for corner collisions
                                    radang = Math.toRadians(angle);
                                    collisionFix[0] = -(distance - ballRadius) * Math.cos(radang);
                                    collisionFix[1] = -(distance - ballRadius) * Math.sin(radang);
                                }
                                hasCollided = true;
                            }
                            isBrickActive[i] = false;

                        }
                    }

                }
                hasCollided = false;
                ballPos[0]+= collisionFix[0];
                ballPos[1]+= collisionFix[1];
                collisionFix[0]=0;
                collisionFix[1]=0;
            }
            //Draws the shooting direction line
            if (!gameStarted) {

                if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && angle > 0)
                    angle -= 1;
                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && angle < 180)
                    angle += 1;
                radang = Math.toRadians(angle);
                lineEndPos[0] = initialBallPos[0] + lineLength * Math.cos(radang);
                lineEndPos[1] = initialBallPos[1] + lineLength * Math.sin(radang);
                StdDraw.line(initialBallPos[0], initialBallPos[1], lineEndPos[0], lineEndPos[1]);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.textLeft(20, 370, "Angle: " + angle);
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    gameStarted = true;
                    StdDraw.pause(200);
                }
            }
            if (gameOver) {
                //displays the game over text
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(400, 150, "Game Over!");
                StdDraw.text(400, 130, "Score: " + score);
            }
            if (gamePaused && !sameLoop) {
                //displays game paused text
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.textLeft(25, 370, "Game Paused");
                StdDraw.textRight(780, 370, "Score: " + score);
                //continues the game
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    gamePaused = false;
                    StdDraw.pause(200);//pauses to prevent retriggering
                }
            }
            if (score==maxScore) {
                gameWon=true;
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(400, 200, "VICTORY!");
                StdDraw.text(400, 180, "Score: " + score);
            }
            StdDraw.setPenColor(paddleColor);//changes color for paddle
            StdDraw.filledRectangle(paddlePos[0], paddlePos[1], paddleHalfwidth, paddleHalfheight);//draws the paddle

            StdDraw.setPenColor(ballColor);
            StdDraw.filledCircle(ballPos[0], ballPos[1], ballRadius);


            StdDraw.show();
            StdDraw.pause(20);//pauses for 20 ms

        }

    }

}
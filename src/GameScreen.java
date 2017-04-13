import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Random;

public class GameScreen extends JPanel {
    private short flashAlpha = 255;

    //sideLines
    private SideLine leftSide;
    private SideLine rightSide;

    //bubbles
    private ArrayList<Bubble> bubbles = new ArrayList<>();
    private short numOfBubbles = 50;
    private Color bubbleOutlineColor = new Color(255, 0, 33, 60);

    //mouse area detection
    private Point[] sidePoints;
    private int[] sidePointsX;
    private int[] sidePointsY;
    private boolean mouseIsOutPlayArea;

    private double sideLineChangeLock = 0;

    //changing background if mouse is outside
    private Color backgroundColor = new Color(255, 255, 255);
    private Color previousBColor = backgroundColor;
    private double backgroundChangeLock = 0;
    private float  backgroundChangeSpeed = 0.01f;

    private boolean whiteFlash;
    private double whiteFlashLock = 0;
    private float whiteFlashSpeed = 0.001f;

    private byte numSeedsPerDown = 4;
    private ArrayList<FallObj> fallingObjs = new ArrayList<>();

    private Color scoreColor = Color.BLACK;
    private Font scoreFont = new Font("Monospaced", Font.BOLD, 36);
    private Font highScoresFont = new Font("Monospaced", Font.BOLD, 20);

    private boolean invincible;
    private long graceStart;
    private float gracePeriodSeconds = 2f;

    private int bigPointDeduction = 100;
    private boolean drawBigFall;
    private short drawBigFallStartAlpha = 100;
    private short drawBigFallCurrentAlpha = drawBigFallStartAlpha;
    private float drawBigFallStartY = 65;

    private int smallPointDeduction = 3;

    private double pointLock = 0;
    private float pointSpeed= 0.05f;

    private byte numOfLives = 4;
    private byte currentNumOfLives = numOfLives;

    private boolean shootOnes;
    private ArrayList<Float> shootingOnesY = new ArrayList<>();

    public GameScreen(){
        super();
        setBounds(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
        setBackground(Color.WHITE);
        whiteFlash = true;

        leftSide = new SideLine(Game.getScreenWidth(), Game.getScreenHeight(), true);
        rightSide = new SideLine(Game.getScreenWidth(), Game.getScreenHeight(), false);

        sidePointsX = new int[4];
        sidePointsY = new int[4];


    }

    private void updateSidelinePoints(){

        //starting initialization for both sidelines
        sidePoints = new Point[]{
                new Point((int) leftSide.x + leftSide.width, (int) leftSide.y),
                new Point((int) leftSide.x + leftSide.width, (int) leftSide.y + leftSide.length),
                new Point((int) rightSide.x, (int) rightSide.y + rightSide.length),
                new Point((int) rightSide.x, (int) rightSide.y)
        };

        //updating 4 points to determine location
        for(int i = 0; i < 4; i++){
            int newX = (int)(Game.getScreenWidth()/2 + (sidePoints[i].x-Game.getScreenWidth()/2)*Math.cos(SideLine.angleRadians)
                    - (sidePoints[i].y-Game.getScreenHeight()/2)*Math.sin(SideLine.angleRadians));

            int newY = (int)(Game.getScreenHeight()/2 + (sidePoints[i].x-Game.getScreenWidth()/2)*Math.sin(SideLine.angleRadians)
                    + (sidePoints[i].y-Game.getScreenHeight()/2)*Math.cos(SideLine.angleRadians));

            sidePointsX[i] = newX;
            sidePointsY[i] = newY;
        }

        //determining if mouse is inside or out play area
        mouseIsOutPlayArea = !Game.polyCollide(4, sidePointsX, sidePointsY, Mouse.X, Mouse.Y);
    }
    private void updateFallObjPoints(){

        for (FallObj value : fallingObjs) {

            value.coordinateYValuesForHitBox();

            for (int i = 0; i < 4; i++) {
                //left rectangle hit-box change
                int leftNewX = (int) (Game.getScreenWidth() / 2 + (value.xLeftStartPts[i] - Game.getScreenWidth() / 2) * Math.cos(SideLine.angleRadians)
                        - (value.yLeftStartPts[i] - Game.getScreenHeight() / 2) * Math.sin(SideLine.angleRadians));
                int leftNewY = (int) (Game.getScreenHeight() / 2 + (value.xLeftStartPts[i] - Game.getScreenWidth() / 2) * Math.sin(SideLine.angleRadians)
                        + (value.yLeftStartPts[i] - Game.getScreenHeight() / 2) * Math.cos(SideLine.angleRadians));

                value.xLeftHitBox[i] = leftNewX;
                value.yLeftHitBox[i] = leftNewY;
            }


            for (int i = 0; i < 4; i++) {
                //right rectangle hit-box change
                int rightNewX = (int) (Game.getScreenWidth() / 2 + (value.xRightStartPts[i] - Game.getScreenWidth() / 2) * Math.cos(SideLine.angleRadians)
                        - (value.yRightStartPts[i] - Game.getScreenHeight() / 2) * Math.sin(SideLine.angleRadians));
                int rightNewY = (int) (Game.getScreenHeight() / 2 + (value.xRightStartPts[i] - Game.getScreenWidth() / 2) * Math.sin(SideLine.angleRadians)
                        + (value.yRightStartPts[i] - Game.getScreenHeight() / 2) * Math.cos(SideLine.angleRadians));

                value.xRightHitBox[i] = rightNewX;
                value.yRightHitBox[i] = rightNewY;
            }

        }

    }
    private void mouseHitConsequence(){
        if (checkIfMouseHitFallObj()) {

            if (!invincible){
                //get current time in milliseconds
                graceStart = System.currentTimeMillis()/1000;

                //deploy flash and subtract score
                whiteFlash = true;
                Score.score = Score.score - bigPointDeduction;

                //draw a big falling minus
                drawBigFall = true;

                //subtract a life
                currentNumOfLives--;
            }

            invincible = true;

            Game.currentMouseFill = Mouse.collisionFill;
            Game.currentMouseOutline = Mouse.collisionOutline;
        }

        //if grace period has passed, go back to normal
        if ((System.currentTimeMillis()/1000) - graceStart >= gracePeriodSeconds && invincible){
            invincible = false;
            drawBigFall = false;
            drawBigFallCurrentAlpha = drawBigFallStartAlpha;
            drawBigFallStartY = 65;
            Game.currentMouseFill = Mouse.Fill;
            Game.currentMouseOutline = Mouse.Outline;
        }
    }

    public void Update(double elapsedTime){

        drawWhiteFlash(elapsedTime);

        //increase score
        pointLock += elapsedTime;
        if (pointLock % 1 >= pointSpeed && !invincible){
            Score.score++;
            pointLock = 0;
        }


        //adds click effect
        if(Mouse.previousDown && Mouse.Up){
            Mouse.previousDown = false;
            Game.clickEffects.add(new ClickEffect(Mouse.X, Mouse.Y));
        }

        updateSidelinePoints();

        //updates depending on game-mode
        for (FallObj value : fallingObjs)
            value.updateFallObjStaticEffect(elapsedTime);

        updateFallObjPoints();

        //updates the rotation of sideline game play (PUT IN METHOD)
        sideLineChangeLock += elapsedTime;
        if (sideLineChangeLock % 1 >= SideLine.speedOfRotation){
            //SideLine.angleRadians += elapsedTime;
            sideLineChangeLock = 0;
        }

        //updates the side bounds
        SideLine.updateSideLineTouchBounds(Game.getScreenWidth(), elapsedTime);

        //draws and updates bubbles when appropriate
        drawBackgroundAndBubbles(elapsedTime);

        //updates existence of all falling objects
        updateAllFallObj();

        //if mouse hits, flash screen, deploy grace period
        mouseHitConsequence();

        //update boarders of left and right sidelines
        rightSide.moveLineBounds(Game.getScreenWidth(), false);
        leftSide.moveLineBounds(Game.getScreenWidth(), true);
    }

    private boolean checkIfMouseHitFallObj(){
        for (FallObj f : fallingObjs)
            if (Game.polyCollideDouble(4, f.xLeftHitBox, f.yLeftHitBox,  Mouse.X, Mouse.Y) ||
                    Game.polyCollideDouble(4, f.xRightHitBox, f.yRightHitBox,  Mouse.X, Mouse.Y))
                return true;

        return false;
    }
    private void updateAllFallObj(){
        for (Iterator<FallObj> iterator = fallingObjs.iterator(); iterator.hasNext();) {
            FallObj value = iterator.next();
            //removes all out of screen obj
            if (value.rightRect.y > rightSide.length && value.leftRect.y > leftSide.length){
                iterator.remove();
            }
        }

        if (fallingObjs.size() <= 0 || fallingObjs.get(fallingObjs.size() - 1).rightRect.y > Game.getScreenHeight() / numSeedsPerDown)
            fallingObjs.add(new FallObj(leftSide));
    }
    private void drawFallingPoint(Graphics2D g2d){
        if (drawBigFall){
            g2d.setFont(new Font("Monospaced", Font.BOLD, 25));
            g2d.setColor(new Color(255, 0, 0, drawBigFallCurrentAlpha));
            g2d.drawString("-" + bigPointDeduction, 185, (int)drawBigFallStartY);

            if (drawBigFallCurrentAlpha -1 >= 0)
                drawBigFallCurrentAlpha--;

            drawBigFallStartY += 0.3f;
        }
    }
    private void drawLifeBoxes(Graphics2D g2d, byte numOfBoxes){
        //x and y for top left first box. Others will be set accordingly
        int xCoor = 30;
        int yCoor = 60;

        //width & height of each box
        int width = 30;
        int height = 30;

        //stroke width
        byte strokeWidth = 2;

        //width between each box
        short spaceBTBoxes = 20;

        //uses score color for box outline color
        g2d.setStroke(new BasicStroke(strokeWidth));

        for (int i = 0; i < numOfBoxes; i++){
            g2d.setColor(new Color(33, 184, 58, 80));
            g2d.fillRect((xCoor*(i+1)) + ((strokeWidth)*(i+1)) + (spaceBTBoxes*i), yCoor + strokeWidth/2, width - strokeWidth/2 +1, height - strokeWidth +1);

            g2d.setColor(scoreColor);
            g2d.drawRect((xCoor*(i+1)) + ((strokeWidth)*(i+1)) + (spaceBTBoxes*i), yCoor, width, height);
        }
    }
    private void drawShootingOnes(Graphics2D g2d){

        //top-left Y position of -1's
        shootingOnesY.add(0f);
        int distanceFromCenter = 300;

        Random gen = new Random();

        //draws one's
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));

        for (int i = 0; i < shootingOnesY.size(); i++){

            int chosenAngle = gen.nextInt(50) - 30;

            shootingOnesY.set(i, shootingOnesY.get(i) + 1);

            if (shootingOnesY.get(i) < 150)
                continue;
            if (gen.nextInt(10) != 0)
                continue;

            AffineTransform old = g2d.getTransform();
            g2d.rotate(-chosenAngle*Math.PI/180, 280, -80); //x y relative to middle of point thing

            //(int)(shootingOnesY.get(i)/distanceFromCenter*(255/2))
            g2d.setColor(new Color(255, 0, 0, (int)(shootingOnesY.get(i)/distanceFromCenter*(255*.75))));//255, 60, 60
            g2d.drawString("-1", 283, shootingOnesY.get(i) -75);
            g2d.setTransform(old);
        }

        for (Iterator<Float> iterator = shootingOnesY.iterator(); iterator.hasNext();) {
            Float f = iterator.next();
            if (f >= distanceFromCenter){
                iterator.remove();
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //formatting paint component
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //does the changing background if mouse is outside play area
        if (mouseIsOutPlayArea)
            setBackground(previousBColor);
        else
            setBackground(Color.WHITE);

        //drawing bubbles
        Game.drawBubbles(g2d, bubbles);

        //drawing middle background panel with rotation
        AffineTransform old = g2d.getTransform();
        g2d.rotate(SideLine.angleRadians, Game.getScreenWidth()/2, Game.getScreenHeight()/2);
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRect((int)leftSide.x + leftSide.width - 10, (int)leftSide.y,
                (int)((rightSide.x - leftSide.x) - leftSide.width + 20), leftSide.length);

        //drawing side lines
        g2d.setColor(Game.currentMouseFill);
        //g2d.setColor(new Color(45, 45, 45));
        g2d.fillRect((int)leftSide.x, (int)leftSide.y, leftSide.width, leftSide.length);
        g2d.fillRect((int)rightSide.x, (int)rightSide.y, rightSide.width, rightSide.length);

        //draw fall objects
        g2d.setColor(new Color(0, 0, 0, 100));
        try{
            for (FallObj f : fallingObjs) {
                g2d.fillRect((int) f.leftRect.x, (int) f.leftRect.y, (int) f.leftRect.width, (int) f.leftRect.height);
                g2d.fillRect((int) f.rightRect.x, (int) f.rightRect.y, (int) f.rightRect.width, (int) f.rightRect.height);
            }
        }catch (ConcurrentModificationException ignore){}
        g2d.setTransform(old);

        //drawing score
        g2d.setColor(scoreColor);
        g2d.setFont(scoreFont);
        g2d.drawString("Score: " + Score.score, 30, 40);

        //drawing current high score
        g2d.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), 80));
        g2d.setFont(highScoresFont);
        g2d.drawString("High Score: " + Score.highScore, 30, 120);

        //drawing all time high score
        g2d.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), 80));
        g2d.setFont(highScoresFont);
        g2d.drawString("All-Time High: " + Score.highestScore, 30, 140);

        //draws falling score thing
        drawFallingPoint(g2d);

        //draws shooting -1's
        if (shootOnes)
            drawShootingOnes(g2d);

        //draws the life-point boxes
        drawLifeBoxes(g2d, currentNumOfLives);

        ///Order is important, this should go second to last. Mouse-
        //drawing mouse tail when appropriate
        if (Mouse.Down || Mouse.mouseTailPoints.size() > 0)
            Game.drawTail(g2d);

        //drawing the mouse
        Game.drawMouse(g2d);

        //drawing effectCircle
        Game.drawClickEffects(g2d);
        ///

        //draw white flash upon entering or mouse hit
        if(whiteFlash){
            g2d.setColor(new Color(255, 255, 255, flashAlpha));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    private void drawWhiteFlash(double elapsedTime){
        if(whiteFlash){
            whiteFlashLock += elapsedTime;
            if (whiteFlashLock % 1 >= whiteFlashSpeed){
                flashAlpha -= 1;
                whiteFlashLock = 0;
            }
            if (flashAlpha <= 0){
                whiteFlash = false;
                flashAlpha = 255;
            }
        }
    }
    private void drawBackgroundAndBubbles(double elapsedTime){
        if(mouseIsOutPlayArea){

            //change score color (inverse)
            scoreColor = new Color(255 - previousBColor.getRed(), 255 - previousBColor.getGreen(),
                    255 - previousBColor.getBlue());

            updateBubblesGS(elapsedTime);

            backgroundChangeLock += elapsedTime;

            if (backgroundChangeLock % 1 >= backgroundChangeSpeed){
                changeColorOfRedBackground();
                Score.score -= smallPointDeduction;
                backgroundChangeLock = 0;
            }

            shootOnes = true;
        } else {
            shootOnes = false;
            scoreColor = Color.BLACK;
            fadeBubbles();
        }
    }

    public void Draw(){
        repaint();
    }

    private void changeColorOfRedBackground(){
        int decrement = 1;
        byte cutoff = 90;
        if (previousBColor.getGreen() - decrement >= 0 && previousBColor.getBlue() - decrement >= 0){
            previousBColor = new Color(previousBColor.getRed(), previousBColor.getGreen() - decrement,
                    previousBColor.getBlue() - decrement);
            if ((previousBColor.getGreen() <= cutoff || previousBColor.getBlue() <= cutoff) && previousBColor.getRed() - decrement >= 0){
                previousBColor = new Color(previousBColor.getRed() - decrement, previousBColor.getGreen(), previousBColor.getBlue());
            }
        }

        if ((previousBColor.getGreen() <= decrement || previousBColor.getBlue() <= decrement) && previousBColor.getRed() - decrement >= 0)
            previousBColor = new Color(previousBColor.getRed() - decrement, previousBColor.getGreen(), previousBColor.getBlue());
    }
    private void fadeBubbles(){
        for (Iterator<Bubble> iterator = bubbles.iterator(); iterator.hasNext();) {

            Bubble value = iterator.next();

            if(value.fillColor.getAlpha() - 1 >= 0)
                value.fillColor = new Color(value.fillColor.getRed(), value.fillColor.getGreen(),
                        value.fillColor.getBlue(), value.fillColor.getAlpha() - 1);

            if(value.outlineColor.getAlpha() - 1 >= 0)
                value.outlineColor = new Color(value.outlineColor.getRed(), value.outlineColor.getGreen(),
                        value.outlineColor.getBlue(), value.outlineColor.getAlpha() - 1);
        }
    }
    private void updateBubblesGS(double elapsedTime){
        for (Iterator<Bubble> iterator = bubbles.iterator(); iterator.hasNext();) {
            Bubble value = iterator.next();
            if (value.shouldRemove) {
                iterator.remove();
            }
        }

        if (bubbles.size() < numOfBubbles)
            bubbles.add(new Bubble(Game.getScreenWidth(), Game.getScreenHeight(), 20, 10, 750, 400, bubbleOutlineColor));

        for(Bubble b : bubbles){
            b.updateBubble(elapsedTime);
            b.fillColor = new Color(b.fillColor.getRed(), b.fillColor.getGreen(),
                    b.fillColor.getBlue(), b.standardFillAlpha);
            b.outlineColor = new Color(b.outlineColor.getRed(), b.outlineColor.getGreen(),
                    b.outlineColor.getBlue(), b.standardOutlineAlpha);
        }
    }

    public void LoadContent(){
    }
}

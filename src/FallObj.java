import java.awt.*;
import java.util.Random;

public class FallObj {

    public DynamicRectangle leftRect = new DynamicRectangle();
    public DynamicRectangle rightRect = new DynamicRectangle();

    //default speed
    public static int speed = 500;

    public static int length = 20;

    public static int seedLow = 40;
    public static int seedHigh = 80;

    private int currentSeedWidth;
    private int seedPlacementX;

    //preset coordinates
    double[] xLeftStartPts;
    double[] xRightStartPts;
    double[] yLeftStartPts;
    double[] yRightStartPts;

    //constantly updated hit-box points
    public double[] xLeftHitBox = new double[4];
    public double[] xRightHitBox = new double[4];
    public double[] yLeftHitBox = new double[4];
    public double[] yRightHitBox = new double[4];

    public FallObj(SideLine leftSide){
        Random gen = new Random();
        currentSeedWidth = gen.nextInt(seedHigh - seedLow) + seedLow;
        seedPlacementX = gen.nextInt((int)SideLine.widthOfPlayArea - currentSeedWidth) + (int)leftSide.x + leftSide.width;

        //create left rectangle
        leftRect.x = (int)leftSide.x + leftSide.width;
        leftRect.y = (int)leftSide.y - length;
        leftRect.width = seedPlacementX - leftRect.x;
        leftRect.height = length;

        //create right rectangle
        rightRect.x = seedPlacementX + currentSeedWidth;
        rightRect.y = (int)leftSide.y - length;
        rightRect.width = (int)SideLine.widthOfPlayArea - currentSeedWidth - leftRect.width;
        rightRect.width = (leftRect.x + (int)SideLine.widthOfPlayArea) - rightRect.x - leftSide.width;
        rightRect.height = length;

        //initializing hit-box arrays
        xLeftStartPts = new double[4];
        xRightStartPts = new double[4];
        yLeftStartPts = new double[4];
        yRightStartPts = new double[4];

        //initialize X left hit-boxes
        xLeftStartPts[0] = leftRect.x;
        xLeftStartPts[1] = xLeftStartPts[0];
        xLeftStartPts[2] = leftRect.x + leftRect.width;
        xLeftStartPts[3] = xLeftStartPts[2];

        //initialize X right hit-boxes
        xRightStartPts[0] = rightRect.x;
        xRightStartPts[1] = xRightStartPts[0];
        xRightStartPts[2] = rightRect.x + rightRect.width;
        xRightStartPts[3] = xRightStartPts[2];

        coordinateYValuesForHitBox();
    }

    public void updateFallObjStaticEffect(double elapsedTime){
        leftRect.y += speed * elapsedTime;
        rightRect.y += speed * elapsedTime;
    }

    public void coordinateYValuesForHitBox(){

        //this aligns the hit-boxes to move aligned with the visual
        yLeftStartPts[0] = leftRect.y;
        yLeftStartPts[1] = leftRect.y + leftRect.height;
        yLeftStartPts[2] = yLeftStartPts[1];
        yLeftStartPts[3] = yLeftStartPts[0];

        yRightStartPts[0] = rightRect.y;
        yRightStartPts[1] = rightRect.y + rightRect.height;
        yRightStartPts[2] = yRightStartPts[1];
        yRightStartPts[3] = yRightStartPts[0];
    }

}

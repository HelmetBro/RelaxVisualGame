import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Random;

public class SuperFormula {
    //for superformula
    private int superArrayLength = 360;
    public int[] superArrayX = new int[superArrayLength];
    public int[] superArrayY = new int[superArrayLength];

    private short superSize = 200;

    private Random gen;
    private float switchBound = 0.001f;
    private int aSuperBound = 2;
    private int bSuperBound = 2;
    private int mSuperBound = 2;
    private int n1SuperBound = 2;
    private int xSuperBound = 2;
    private int ySuperBound = 2;
    private float a;
    private float b;
    private float m;
    private float n1;
    private float xB;
    private float yB;

    public Color fillColor = new Color(255, 118, 135);
    public Color outlineColor = new Color(255, 17, 69);

    //width
    public byte stroke = 12;

    private float speedOfChange = 1f;

    public SuperFormula(){
        gen = new Random();

        a = aSuperBound;
        b = bSuperBound;
        m = mSuperBound;
        n1 = n1SuperBound;
        xB = xSuperBound;
        yB = ySuperBound;
    }

    private float ToRadians(float degree){
        return degree*(float)Math.PI/180;
    }

    public int getSuperArrayLength() {
        return superArrayLength;
    }

    public void createSuperFormula(){
        short superIndex = 0;
        for (short theta = 0; theta < 180; theta++) //increments by 1 degree
        {
            //https://www.youtube.com/watch?v=u6arTXBDYhQ
            float rad = r(ToRadians(theta),
                    a, //a
                    b, //b
                    m, //m
                    n1, //n1
                    xB, //X (not position)
                    yB  //Y (not position)
            );
            superArrayX[superIndex] = (int)(rad * Math.cos(ToRadians(theta)) * superSize + Game.getScreenWidth()/2);
            superArrayY[superIndex] = (int)(rad * Math.sin(ToRadians(theta)) * superSize + Game.getScreenHeight()/2);

            superArrayX[superIndex + 180] = (int)(rad * -Math.cos(ToRadians(theta)) * superSize + Game.getScreenWidth()/2);
            superArrayY[superIndex + 180] = (int)(rad * -Math.sin(ToRadians(theta)) * superSize + Game.getScreenHeight()/2);

            superIndex++;
        }
    }

    public void drawSuperFormula(Graphics2D g2d){
        g2d.setColor(new Color(193, 0, 97, 97));
        g2d.setStroke(new BasicStroke(10));

        for (short i = 1; i < superArrayLength; i++)
            g2d.draw(new Line2D.Float(superArrayX[i-1], superArrayY[i-1], superArrayX[i], superArrayY[i]));

        g2d.draw(new Line2D.Float(superArrayX[0], superArrayY[0], superArrayX[superArrayLength-1], superArrayY[superArrayLength-1]));
    }

    private float r(float theta, float a, float b, float m, float n1, float n2, float n3){
        return (float)Math.pow(Math.pow(Math.abs(Math.cos(m * theta / 4.0) / a), n2) + Math.pow(Math.abs(Math.sin(m * theta / 4.0) / b), n3), -1.0 / n1);
    }

    public void updateTheSuperFormula(double elapsedTime){
        if (Math.abs(a - aSuperBound ) > switchBound)
            a = currentRelativeToBound(a, aSuperBound, speedOfChange * elapsedTime);
        else
            aSuperBound = gen.nextInt(2) + 1;
        if (Math.abs(b - bSuperBound ) > switchBound)
            b = currentRelativeToBound(b, bSuperBound, speedOfChange * elapsedTime);
        else
            bSuperBound = gen.nextInt(1) + 1;
        if (Math.abs(m - mSuperBound ) >= switchBound)
            m = currentRelativeToBound(m, mSuperBound, speedOfChange * elapsedTime);
        else
            mSuperBound = gen.nextInt(20) + 1;
        if (Math.abs(n1 - n1SuperBound ) > switchBound)
            n1 = currentRelativeToBound(n1, n1SuperBound, speedOfChange * elapsedTime);
        else
            n1SuperBound = gen.nextInt(3) + 1;
        if (Math.abs(xB - xSuperBound ) > switchBound)
            xB = currentRelativeToBound(xB, xSuperBound, speedOfChange * elapsedTime);
        else
            xSuperBound = gen.nextInt(3) + 1;
        if (Math.abs(yB - ySuperBound ) > switchBound)
            yB = currentRelativeToBound(yB, ySuperBound, speedOfChange * elapsedTime);
        else
            ySuperBound = gen.nextInt(3) + 1;
    }

    private float currentRelativeToBound(float current, int bound, double increment){
        if (current <= bound)
            return (float)(current + increment);
        else
            return (float)(current - increment);
    }
}

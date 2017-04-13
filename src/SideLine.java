public class SideLine {

    public int length;
    public int width = 10;

    public float x;
    public float y;

    public static float angleRadians = 0;
    public static float speedOfRotation = 0.00001f;

    //50% means half the screen is the game length. 5% or greater for start/min.
    private static float startingPercentSectioned = 20;
    private static float maxPercentSectioned = 0;
    private static float minPercentSectioned = 20;

    //able to change this! Try it.
    public static float widthOfPlayArea;
    //determines is bounds area should shrink or not
    private static boolean isShrinking;
    //the speed in which the bounds move apart from each other, greater is faster.
    private static float speedOfAreaAltering = 20;


    public SideLine(int screenWidth, int screenLength, boolean rightOrLeft){
        //left is true, right is false

        length = (int)Math.ceil(Math.sqrt((screenWidth*screenWidth) + (screenLength*screenLength)));

        while((maxPercentSectioned / 100 * screenWidth) < (screenLength - width * 2)){
            maxPercentSectioned++;
        }

        //default percent is max
        if(startingPercentSectioned > maxPercentSectioned)
            startingPercentSectioned = maxPercentSectioned;

        widthOfPlayArea = (startingPercentSectioned / 100) * screenWidth;

        moveLineBounds(screenWidth, rightOrLeft);

        this.y = -((length - screenLength)/2);
    }

    public void moveLineBounds(int screenWidth, boolean rightOrLeft){
        float widthOfBounds = (screenWidth - widthOfPlayArea) / 2;

        if (rightOrLeft)
            this.x = widthOfBounds - (this.width/2);
        else
            this.x = (screenWidth - widthOfBounds) - (this.width/2);
    }

    public static void updateSideLineTouchBounds(int screenWidth, double elapsedTime){
        if(isShrinking)
            startingPercentSectioned -= speedOfAreaAltering * elapsedTime;
        else
            startingPercentSectioned += speedOfAreaAltering * elapsedTime;

        if (startingPercentSectioned >= maxPercentSectioned || startingPercentSectioned <= minPercentSectioned)
            isShrinking = !isShrinking;

        widthOfPlayArea = (startingPercentSectioned / 100) * screenWidth;
    }
}

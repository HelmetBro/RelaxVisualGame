import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Program {

    public static Game gameWindow;

    public static void main(String[] args) {

        //create instance of the game window
        gameWindow = new Game();

        //option to close when escape key hit.
        gameWindow.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {  //handler
                if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameWindow.dispose();

                    //safely stops the threads
                    gameWindow.update.kill();
                    gameWindow.draw.kill();

                    if (Score.score > Score.highestScore)
                        Score.writeScoreToFile(Score.score);

                    synchronized (Game.lock){
                        Game.lock.notifyAll();
                    }

                    System.out.println("Program closed.");
                }
            }
        });

        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameWindow.setUndecorated(true);
        gameWindow.setVisible(true);

        //load and/or create data from file
        try {
            new Score();
        } catch (IOException e) {
            System.out.print("Game could not load data. Please report this error message: ");
            e.printStackTrace();
        }
    }//main
}//Program

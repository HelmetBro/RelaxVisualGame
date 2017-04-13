class DrawThread extends Thread {

    private Thread t;
    private String threadName;

    private static boolean isRunning = true;

    public DrawThread(String name){
        threadName = name;
    }

    public void run() {
        while(isRunning) {

            synchronized (Game.lock){
                Game.lock.notifyAll();
                try{
                    Game.lock.wait();
                }catch (InterruptedException ignore){}
            }

            if (!Mouse.OnScreen && Game.state == 1){
                Game.pauseOverlay.Draw();
                continue;
            }

            switch (Game.state) {
                case 0:
                    Game.startScreen.Draw();
                    break;
                case 1:
                    Game.gameScreen.Draw();
                    break;
                default:
                    System.out.println("Game state not defined in Draw()!");
                    break;
            }
        }
    }//run method

    public void start() {

        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    public void kill() {
        isRunning = false;
    }

}//class

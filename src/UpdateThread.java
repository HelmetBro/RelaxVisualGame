class UpdateThread extends Thread {

    private Thread t;
    private String threadName;

    private static boolean isRunning = true;

    private long currentTime;
    private long previousTime;
    public double elapsedTime;

    public UpdateThread(String name){
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

            //grabs current time in nano seconds from system computer
            currentTime = System.nanoTime();

            //finds total elapsed time in seconds from current and previous times
            elapsedTime = (currentTime - previousTime) / 1000000000d;

            Game.updateMousePulse(elapsedTime);
            Game.updateMouseTail(elapsedTime);
            Game.updateClickEffects(elapsedTime);

            Thread.yield();

            if (!Mouse.OnScreen && Game.state == 1){
                Game.pauseOverlay.Update(elapsedTime);
                continue;
            }

            switch (Game.state) {
                case 0:
                    Game.startScreen.Update(elapsedTime);
                    break;
                case 1:
                    Game.gameScreen.Update(elapsedTime);
                    break;
                default:
                    System.out.println("Game state not defined in Update()!");
                    break;
            }//switch/case

            previousTime = currentTime;

        }//while
    }//run

    public void start() {

        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }

        previousTime = System.nanoTime();
    }

    public void kill() {
        isRunning = false;
    }

}//class

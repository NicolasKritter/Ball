package app.ball;

import android.graphics.Canvas;

public class GameLoopThread extends Thread
{
    public final static int fps = 50;
    // si on veut X images en 1 seconde, soit en 1000 ms ->on doit en afficher une toutes les (1000 / X) ms.
    private final static int SKIP_TICKS = 1000 / fps;
    private final GameView view; // l'objet SurfaceView que nous verrons plus bas
    private boolean running = false; // état du thread, en cours ou non

    public GameLoopThread(GameView view) {

        this.view = view;
    }

    // défini l'état du thread : true ou false
    public void setRunning(boolean run) {

        running = run;
    }

    // démarrage du thread
    @Override
    public void run()
    {
        // déclaration des temps de départ et de pause
        long startTime;
        long sleepTime;
        Canvas c = null;
        // cf : surfaceDestroyed() dans GameView.java
        while (running)
        {
            // horodatage actuel
            startTime = System.currentTimeMillis();

            // mise à jour du déplacement des ojets dans GameView.update()
            synchronized (view.getHolder()) {
                view.update();
            }

            // Rendu de l'image, tout en vérrouillant l'accès car nous
            // y accédons à partir d'un processus distinct
            c = null;
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.doDraw(c);
                }
            }
            finally
            {
                if (c != null) {view.getHolder().unlockCanvasAndPost(c);}
            }

            //Calcul la pause nécessaire pour avoir le même raffraichissement
            sleepTime = SKIP_TICKS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime >= 0) {sleep(sleepTime);}
            }
            catch (Exception e) {}
        } // boucle while (running)
    } // public void run()

} // class GameLoopThread
package app.ball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


// SurfaceView est une surface de dessin.
// référence : http://developer.android.com/reference/android/view/SurfaceView.html
public class GameView extends SurfaceView implements SurfaceHolder.Callback  {

    // déclaration de l'objet définissant la boucle principale de déplacement et de rendu
    private GameLoopThread gameLoopThread;
    private Balle balle;
    private VelocityTracker mVelocityTracker = null; // avoir la vitesse a laquel on touche la balle;
    private Paint paint =new Paint();
    public static Context gvContext;
    private int level;
    private static List<Target> listTarget;
    private static List<Wall> listWall;
   // private Iterator<Wall> iter = listWall.iterator();
    static int taillex, tailley;
    private boolean[][] placeWall = new boolean[15][15];
    // création de la surface de dessin
    public GameView(Context context) {
        super(context);
        for (int k =0; k<placeWall.length;k++){
            for (int j = 0; j<placeWall[0].length;j++ ){
                placeWall[j][k] = false;
            }
        }
        gvContext = context;
        getHolder().addCallback(this);
        gameLoopThread = new GameLoopThread(this);
        balle = new Balle(this.getContext());
        paint.setTextSize(50);
        paint.setColor(Color.BLACK);
        level =0;
        listTarget = new ArrayList<>();
        listTarget.add( new Target(600,300,0));
        listWall = new ArrayList<>();
        listWall.add( new Wall(800,800,2));

    }

    // Fonction qui "dessine" un écran de jeu
    public void doDraw(Canvas canvas) {
        if(canvas==null) {return;}

        // on efface l'écran, en blanc
        canvas.drawColor(Color.WHITE);

        //Teste si on touche une cible
        for (int k=0;k<listTarget.size();k++){
            Target cible = listTarget.get(k);
            cible.draw(canvas);
            if (balle.isMoving()){
                if (cible.isHit(balle.getX(),balle.getY(), cible.number)){
                    balle.score = balle.score+3*balle.mult;
                    balle.scoreOne = balle.scoreOne + balle.scoreOne+3*balle.mult;


                }}
        }

        for (int k=0;k<listWall.size();k++) {
            Wall wall = listWall.get(k);
            if (wall != null) {
                wall.draw(canvas);
                if (balle.isMoving()) {
                    //Teste si on touche un mur
                    if (balle.checkHitWall(wall.getX(), wall.getY(), wall.w, wall.h)) {
                        wall.vie = wall.vie-1;
                        if(wall.vie<1) {
                            listWall.remove(wall);
                        }
                    }
                }
            }
        }

        // on dessine la balle

        balle.draw(canvas);

        //réaffiche le score

        canvas.drawText("Score: "+balle.score,50,50, paint);
        canvas.drawText("Bonus: x"+balle.mult,500,50, paint);
        canvas.drawText("Nombre: "+listTarget.size(),1000,50, paint);
        canvas.drawText("Lancer unique: "+balle.maxScoreOne,50,100, paint);

    }

    // Fonction appelée par la boucle principale (gameLoopThread)
    // On gère ici le déplacement des objets
    public void update() {
        balle.moveWithCollisionDetection();
    }

    // Fonction obligatoire de l'objet SurfaceView
    // Fonction appelée immédiatement après la création de l'objet SurfaceView
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // création du processus GameLoopThread si cela n'est pas fait
        if(gameLoopThread.getState()==Thread.State.TERMINATED) {
            gameLoopThread=new GameLoopThread(this);
        }
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    // Fonction obligatoire de l'objet SurfaceView
    // Fonction appelée juste avant que l'objet ne soit détruit.
    // on tente ici de stopper le processus de gameLoopThread
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        gameLoopThread.setRunning(false);
        while (retry) {
            try {
                gameLoopThread.join();
                retry = false;
            }
            catch (InterruptedException e) {}
        }
    }

    // Gère les touchés sur l'écran
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentX = (int)event.getX();
        int currentY = (int)event.getY();
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        switch (event.getAction()) {

            // code exécuté lorsque le doigt touche l'écran.
            case MotionEvent.ACTION_DOWN:
                // si le doigt touche la balle :
                if(currentX >= balle.getX() && currentX <= balle.getX()+balle.getBalleW() && currentY >= balle.getY() && currentY <= balle.getY()+balle.getBalleH() ) {
                    // on arrête de déplacer la balle
                    balle.setMove(false);

                    if(mVelocityTracker == null) {
                        // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    else {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker.clear();
                    }
                    // Add a user's movement to the tracker.
                    mVelocityTracker.addMovement(event);
                }


                break;

            // code exécuté lorsque le doight glisse sur l'écran.
            case MotionEvent.ACTION_MOVE:
                // on déplace la balle sous le doigt du joueur
                // si elle est déjà sous son doigt (oui si on a setMove à false)
                if(!balle.isMoving()) {
                    balle.setX(currentX);
                    balle.setY(currentY);
                    if(mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity for each pointer ID.
                        mVelocityTracker.computeCurrentVelocity(1000);
                        // Log velocity of pixels per second
                        // Best practice to use VelocityTrackerCompat where possible.
                    }
                }

                break;

            // lorsque le doigt quitte l'écran
            case MotionEvent.ACTION_UP:
                if (mVelocityTracker!=null && !balle.isMoving()) {
                    balle.setvX(VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId) / 30);
                    balle.setvY(VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId) / 30);
                }
                // on reprend le déplacement de la balle
                balle.setMove(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                break;
        }

        return true;  // On retourne "true" pour indiquer qu'on a géré l'évènement
    }

    // Fonction obligatoire de l'objet SurfaceView
    // Fonction appelée à la CREATION et MODIFICATION et ONRESUME de l'écran
    // nous obtenons ici la largeur/hauteur de l'écran en pixels
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
        balle.resize(w,h); // on définit la taille de la balle selon la taille de l'écran
        for (Target target :listTarget) {
            target.resize(w, h);
        }
        for (Wall wall :listWall) {
            wall.resize(w, h);
        }
        taillex = w;
        tailley = h;

    }

    public static void generateTarget (int n){

        if (listTarget.size()+n<10) {
            Target target =new Target(Math.random()*1000 +100, Math.random()*1000 +100, listTarget.size());
            target.resize(taillex,tailley);
            listTarget.add(target);
        }
    }//TODO ordre de hit
    public static void generatePath(){

    }
    //TODO mur
    public static void generateWall( int n){
        if (listWall.size() +n<10){
            Wall wall  = new Wall(Math.random()*1000 +100,Math.random()*1000 +100,(int) (Math.random()*2+1));
            wall.resize(taillex,tailley);
            listWall.add(wall);
        }

    }

} // class GameView

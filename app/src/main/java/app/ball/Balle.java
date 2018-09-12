package app.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

class Balle
{
    private BitmapDrawable img; // image de la balle
    private BitmapDrawable img2; // image de la balle
    private double x,y; // coordonnées x,y de la balle en pixel
    private int balleW, balleH; // largeur et hauteur de la balle en pixels
    private int wEcran,hEcran; // largeur et hauteur de l'écran en pixels
    private double m;
    private static double g = 9.81;
    private double ax = 0;
    private double ay = g;
    private static double dt = 0.7;
    private static double f = -0.02;
    private static double fx= -0.04;
    private Paint paint =new Paint();
    private static int limit = 2*MainActivity.limit;
    public int score;
    int mult;
    int scoreOne;
    int maxScoreOne;


    private boolean move = true; // 'true' si la balle doit se déplacer automatiquement, 'false' sinon

    // pour déplacer la balle on ajoutera INCREMENT à ses coordonnées x et y

    private double vX;
    private double  vY;


    // contexte de l'application Android
    // il servira à accéder aux ressources, dont l'image de la balle
    private final Context mContext;

    public Balle(final Context c) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        m = 1;
        x=0;
        score = 0;
        y=0;// position de départ
        vX = 1;
        vY = 1;
        mult = 0;
        scoreOne = 0;
        maxScoreOne = 0;
        mContext=c; // sauvegarde du contexte
    }

    // on attribue à l'objet "Balle" l'image passée en paramètre
    // w et h sont sa largeur et hauteur définis en pixels
    public BitmapDrawable setImage(final Context c, final int ressource, final int w, final int h)
    {
        Drawable dr = c.getResources().getDrawable(ressource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
    }


    public boolean isMoving() {
        return move;
    }

    // définit si oui ou non la balle doit se déplacer automatiquement
    // car on la bloque sous le doigt du joueur lorsqu'il la déplace
    public void setMove(boolean move) {
        this.move = move;
    }

    // redimensionnement de l'image selon la largeur/hauteur de l'écran passés en paramètre
    public void resize(int wScreen, int hScreen) {

        wEcran=wScreen;
        hEcran=hScreen-limit;

        // on définit (au choix) la taille de la balle à 1/5ème de la largeur de l'écran
        balleW=(int)(wScreen*0.07);
        balleH=(int)(wScreen*0.07);
        img = setImage(mContext,R.mipmap.ball,balleW,balleH);
        img2 = setImage(mContext,R.mipmap.ball2,balleW,balleH);
    }
    //définit la vitesse en X
    public void setvX(double v){
        this.vX = v;
    }

    //définit la vitesse en Y
    public void setvY(double v){
        this.vY = v;
    }

    // définit la coordonnée X de la balle
    public void setX(int x) {
        this.x = x-balleW/2;//recentre l'image quand on la prend
    }

    // définit la coordonnée Y de la balle
    public void setY(int y) {
        this.y = y-balleH/2;
    }

    // retourne la coordonnée X de la balle
    public double getX() {
        return x;
    }

    // retourne la coordonnée Y de la balle
    public double getY() {
        return y;
    }

    // retourne la largeur de la balle en pixel
    public int getBalleW() {
        return balleW;
    }

    // retourne la hauteur de la balle en pixel
    public int getBalleH() {
        return balleH;
    }

    // déplace la balle en détectant les collisions avec les bords de l'écran
    public void moveWithCollisionDetection()
    {
        // si on ne doit pas déplacer la balle (lorsqu'elle est sous le doigt du joueur)
        // on quitte
        if(!move) {
            calcscoreOne();
            return;
        }
        vY = ay*dt +vY + f*vY/m;
        // on incrémente les coordonnées X et Y
        x  = (ax*Math.pow(dt,2)/2 + vX*dt + x);
        y = (ay*Math.pow(dt,2)/2 + vY*dt + f*vY*dt/m +y);
        if (y>hEcran-balleH-10){
            vX = vX+fx*vX/m;
        }

        // si x dépasse la largeur de l'écran, on inverse le déplacement
        if(x+balleW > wEcran) {
            mult = mult+1;
            x = wEcran-balleW;
            vX=-vX;

        }

        // si y dépasse la hauteur l'écran, on inverse le déplacement
        if(y+balleH > hEcran) {
            y = hEcran-balleH;
            vY=-vY;
            mult  = 0;
            calcscoreOne();

        }

        // si x passe à gauche de l'écran, on inverse le déplacement
        if(x<0) {
            x = 0;
            vX=-vX;
            mult = mult +1;

        }


        // si y passe à dessus de l'écran, on inverse le déplacement
        if(y<0) {
            y = 0;
            vY=-vY;
            score = score+3;
            mult  = mult *2;
        }
       //MainActivity.refreshScore(score);
    }
    public  void calcscoreOne(){
        if (scoreOne>maxScoreOne){
            maxScoreOne = scoreOne;
        }
        scoreOne = 0;
    }

    // on dessine la balle, en x et y
    public void draw(Canvas canvas)
    {
        if(img==null || img2==null) {
            return;
        }
        if(this.y>hEcran-balleH-10) {//au sol
            canvas.drawBitmap(img.getBitmap(), Math.round(x), Math.round(y), null);

        }else {//au dessus du sol (crie)

            canvas.drawBitmap(img2.getBitmap(), Math.round(x), Math.round(y), null);
            if (isMoving()) {
                canvas.drawText("Ouiiiiiiii", (float) x + balleW, (float) y, paint);
            }else{
                canvas.drawText("Lance Moi", (float) x + balleW, (float) y, paint);
            }
        }
    }



    public  boolean checkHitWall(double x, double y, int w, int h){


        if (Math.abs(this.x - x)<(balleW+w)/2 +5) {

            if (Math.abs(this.y - y)<(balleH+h)/2 +5) {
                //Regarde la position précédente pour voir d'où venais la balle et donc de quel côté on touche
                if (Math.abs(this.y-vY - y)>(balleH+h)/2 ) {

                vY = -vY;


                }
                if (Math.abs(this.x-vX - x)>(balleW+w)/2 ){
                    vX = -vX;
                }

                return true;
           }
        }
        return false;
    }


} // public class Balle
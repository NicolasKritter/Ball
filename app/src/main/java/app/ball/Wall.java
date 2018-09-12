package app.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by nicolas on 07/03/2017.
 */

public class Wall {

   private double x,y;
    static BitmapDrawable img;
    int w,h;
    int xTab, yTab;
    int vie;
    private Paint paint =new Paint();
    public Wall(double x, double y, int vie){
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        this.x = x;
        this.y = y;
        this.vie = vie;
    }



    public void draw(Canvas canvas){
        if (img!=null) {

            canvas.drawBitmap(img.getBitmap(), Math.round(x), Math.round(y), null);
            canvas.drawText(Integer.toString(vie), (float) x +w/2, (float) y+w/2, paint);

        }
    }

    public  void resize(int wScreen, int hScreen) {


        // on définit (au choix) la taille de la balle à 1/5ème de la largeur de l'écran
        w=wScreen/8;
        h=wScreen/8;
        img = setImage(GameView.gvContext,R.mipmap.wall,w,h);

    }

    public static BitmapDrawable setImage(final Context c, final int ressource, final int w, final int h)
    {
        Drawable dr = c.getResources().getDrawable(ressource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
    }
    public double getX() {
        return x;
    }

    // retourne la coordonnée Y de la balle
    public double getY() {
        return y;
    }
    //TODO extends et passer img en paramètre ?
}

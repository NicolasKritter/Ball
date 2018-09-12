package app.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class Target {
    double x,y;
    static BitmapDrawable img;
    int w,h;
    int number;
    private Paint paint =new Paint();

    public Target(double x, double y, int nb){
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        this.x = x;
        this.y = y;
        number = nb;
    }

    public void draw(Canvas canvas){
        if (img!=null) {
            canvas.drawBitmap(img.getBitmap(), Math.round(x), Math.round(y), null);
            canvas.drawText(Integer.toString(number), (float) x + w, (float) y, paint);
        }
    }
    public  boolean isHit(double x, double y, int nb){
        //TODO prendre en compte la taille de l'écran

        if (nb!=number){

            return false;
        }
        if ((Math.abs(this.x - x) +Math.abs((this.y -y)))<w) {

            this.x = Math.random()*1000 +100;
            this.y = Math.random()*1000 +100;
            return true;
        }else{
            return false;
        }
    }
    public static BitmapDrawable setImage(final Context c, final int ressource, final int w, final int h)
    {
        Drawable dr = c.getResources().getDrawable(ressource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
    }
     public  void resize(int wScreen, int hScreen) {


        // on définit (au choix) la taille de la balle à 1/5ème de la largeur de l'écran
        w=wScreen/8;
        h=wScreen/8;
        img = setImage(GameView.gvContext,R.mipmap.target,w,h);

    }
}

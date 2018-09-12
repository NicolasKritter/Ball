package app.ball;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private GameView gameView;
    private RelativeLayout game;
    private Button btn;
    private Button btn2;
    public static int limit = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game = (RelativeLayout)findViewById(R.id.gamelayout);
        //btn
        btn = (Button)findViewById(R.id.button);
        btn.setEnabled(true);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GameView.generateTarget(1);
            }
            });
        //btn2
        btn2 = (Button)findViewById(R.id.buttonwall);
        btn2.setEnabled(true);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GameView.generateWall(1);
            }
        });



        // On cré un objet "GameView" qui est le code principal du jeu
        gameView=new GameView(this);

        // et on l'affiche.
        game.addView(gameView);


    }


} // class MainActivity
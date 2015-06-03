package com.shicaigj.otto.imagegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.shicaigj.otto.view.GameRelativeLayout;


public class MainActivity extends Activity {

    private GameRelativeLayout gameRelativeLayout;
    private TextView levelText;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameRelativeLayout = (GameRelativeLayout) findViewById(R.id.game_relativeLayout);
        gameRelativeLayout.setIsTimeEnable(true);
        levelText = (TextView) findViewById(R.id.id_level);
        timeText = (TextView) findViewById(R.id.id_time);


        gameRelativeLayout.setOnGameImageLister(new GameRelativeLayout.GameImageLister() {
            @Override
            public void nextLevel(final int level) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Game Info")
                        .setMessage("LEVEL_UP!!!")
                        .setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gameRelativeLayout.nextLevel();
                        levelText.setText("" + level);
                    }
                }).show();
            }

            @Override
            public void timeChanged(int currenTime) {
                timeText.setText("" + currenTime);
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(MainActivity.this).setTitle("Game Info")
                        .setMessage("GAME OVER!!!")
                        .setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gameRelativeLayout.restartGame();
                            }
                        }).setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                         }).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameRelativeLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameRelativeLayout.resume();
    }
}

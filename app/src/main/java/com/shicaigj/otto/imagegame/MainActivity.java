package com.shicaigj.otto.imagegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.shicaigj.otto.view.GameRelativeLayout;


public class MainActivity extends Activity {

    private GameRelativeLayout gameRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameRelativeLayout = (GameRelativeLayout) findViewById(R.id.game_relativeLayout);
        gameRelativeLayout.setOnGameImageLister(new GameRelativeLayout.GameImageLister() {
            @Override
            public void nextLevel(int level) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Game Info")
                        .setMessage("LEVEL_UP!!!")
                        .setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gameRelativeLayout.nextLevel();
                    }
                }).show();
            }

            @Override
            public void timeChanged(int currenTime) {

            }

            @Override
            public void gameOver() {

            }
        });
    }

}

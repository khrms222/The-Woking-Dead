package com.kekcom.thewokingdead;

import android.app.Activity;
import android.view.MenuItem;
import android.util.DisplayMetrics;

import android.view.Menu;
import android.content.Intent;
import android.view.MenuInflater;

import android.os.Bundle;
import android.content.Context;
import android.view.Window;

/**
 * Created by Marcus on 7/29/2017.
 */

public class Start extends Activity{

    private MainView mainView = null;
    private float objectDensity;
    private DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onPause(){
        super.onPause();
        mainView.getThread().setState(MainView.GAME_PAUSED);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){
        Intent intent = null;
        switch(menuItem.getItemId()){
            case R.id.menuExit:
                finish();
                return true;

            case R.id.menuAbout:
                intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        Context context = getApplicationContext();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        objectDensity = displayMetrics.density;

        mainView = new MainView(context, this, 1, 1, objectDensity);
    }
}

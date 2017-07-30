package com.kekcom.thewokingdead;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class GameActivity extends Activity {
    private MainView mMainView = null;

    private DisplayMetrics mMetrics = new DisplayMetrics();
    private float mScreenDensity;
    private SoundPool spool;
    private int soundID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = getApplicationContext();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundID = spool.load(this, R.raw.menu, 1);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        android.util.Log.v("SOUND", "test");
        //android.util.Log.v("SOUND","["+volume+"]["+spool.play(soundID, volume, volume, 1, 0, 1f)+"]");
        spool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        });
        //spool.play(soundID, volume, volume, 1, 0, 1f);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mScreenDensity = mMetrics.density;

        int stage = 1;
        int level = 1;

        Log.d("Tile Game Example", "Starting game at stage: " + stage + ", level: " + level);
        mMainView = new MainView(mContext, this, stage, level, mScreenDensity);

        setContentView(mMainView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;

        switch (item.getItemId()) {
            case R.id.menuAbout:
                i = new Intent(this, About.class);
                startActivity(i);
                return true;
            case R.id.menuExit:
                finish();
                return true;
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMainView.getThread().setState(MainView.STATE_PAUSED); // pause game when Activity pauses
    }
}
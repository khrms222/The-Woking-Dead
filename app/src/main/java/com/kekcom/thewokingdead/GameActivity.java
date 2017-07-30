package com.kekcom.thewokingdead;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
    private SoundPool mSoundPool;
    private static int[] mSoundIDs;
    int sNumLoaded = 0;
    private boolean canPlay = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = getApplicationContext();

        mSoundIDs = new int[5];
        MainActivity.mediaPlayer = MediaPlayer.create(this, R.raw.play);
        MainActivity.mediaPlayer.start();
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        android.util.Log.v("SOUND", "test");
        //android.util.Log.v("SOUND","["+volume+"]["+mSoundPool.play(soundID, volume, volume, 1, 0, 1f)+"]");
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                sNumLoaded++;
                if (sNumLoaded == mSoundIDs.length){
                    canPlay = true;
                    Log.d("SFX","LOADED");
                }
            }
        });

        // Load sounds
        mSoundIDs[0] = mSoundPool.load(this, R.raw.pain, 1);
        mSoundIDs[1] = mSoundPool.load(this, R.raw.hit, 1);
        mSoundIDs[2] = mSoundPool.load(this, R.raw.zom3, 1);
        mSoundIDs[3] = mSoundPool.load(this, R.raw.zom2, 1);
        mSoundIDs[4] = mSoundPool.load(this, R.raw.zom1, 1);
        //mSoundPool.play(soundID, volume, volume, 1, 0, 1f);

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
        if(MainActivity.mediaPlayer != null || MainActivity.mediaPlayer.isPlaying()){

            MainActivity.mediaPlayer.pause();
        }
        super.onPause();

        mMainView.getThread().setState(MainView.STATE_PAUSED); // pause game when Activity pauses
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.mediaPlayer != null || !MainActivity.mediaPlayer.isPlaying()){

            MainActivity.mediaPlayer.start();
        }
    }
}
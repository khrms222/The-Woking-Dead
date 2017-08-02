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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {
    private static int[] mSoundIDs;
    private static SoundPool mSoundPool;
    private static float volume = 0.50f;
    public boolean canPlay = false;
    int sNumLoaded = 0;
    private MainView mMainView = null;
    private DisplayMetrics mMetrics = new DisplayMetrics();
    private float mScreenDensity;
    private Timer timer;
    private ArrayList<Integer> playlist;
    private int i = 0;

    public static void sfx(int i) {
        mSoundPool.play(mSoundIDs[i], volume, volume, 1, 0, 1f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = getApplicationContext();

        mSoundIDs = new int[7];
        MainActivity.mediaPlayer.release();
        playlist = new ArrayList<>();
        playlist.add(R.raw.nuke);
        playlist.add(R.raw.youstillplaying);
        MainActivity.mediaPlayer = MediaPlayer.create(this, R.raw.play);

        if (MainActivity.toggleMuteHomeButton.isChecked()) {
            MainActivity.mediaPlayer.setVolume(0.50f, 0.50f);
//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

//                    Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT);

//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            //toggleMuteButton.setSoundEffectsEnabled(true);
        } else {

            MainActivity.mediaPlayer.setVolume(0, 0);
//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            //Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT);

//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            //toggleMuteButton.setSoundEffectsEnabled(false);
        }
        MainActivity.mediaPlayer.start();
        timer = new Timer();
        if (playlist.size() > 1) playNext();
//        MainActivity.mediaPlayer.setLooping(true);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
        mSoundIDs[1] = mSoundPool.load(this, R.raw.gonghittrimmed, 1);
        mSoundIDs[2] = mSoundPool.load(this, R.raw.zom3, 1);
        mSoundIDs[3] = mSoundPool.load(this, R.raw.zom2, 1);
        mSoundIDs[4] = mSoundPool.load(this, R.raw.zom1, 1);
        mSoundIDs[5] = mSoundPool.load(this, R.raw.stair, 1);
        mSoundIDs[6] = mSoundPool.load(this, R.raw.asianrifftrimmed, 1);
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

    public void playNext() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.mediaPlayer.reset();
                MainActivity.mediaPlayer = MediaPlayer.create(GameActivity.this, playlist.get(++i));
                MainActivity.mediaPlayer.start();
                MainActivity.mediaPlayer.setVolume(0.50f, 0.50f);
                if (playlist.size() > i + 1) {
                    playNext();
                }
            }
        }, MainActivity.mediaPlayer.getDuration() + 100);
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
//            MainActivity.mediaPlayer.setLooping(false);
        }
        super.onPause();

        mMainView.getThread().setState(MainView.STATE_PAUSED); // pause game when Activity pauses
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.mediaPlayer != null || !MainActivity.mediaPlayer.isPlaying()){

            MainActivity.mediaPlayer.start();
//            MainActivity.mediaPlayer.setLooping(true);
        }
    }

    /*
    @Override
    public void onDestroy() {
        if(MainActivity.mediaPlayer != null) {
            if (MainActivity.mediaPlayer.isPlaying())
                MainActivity.mediaPlayer.stop();
        }
        timer.cancel();
        super.onDestroy();
    }
    */
}
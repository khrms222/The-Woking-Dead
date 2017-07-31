package com.kekcom.thewokingdead;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import android.text.style.ImageSpan;
import android.text.SpannableString;
import android.text.Spanned;

import android.media.AudioManager;

import android.view.View;
import android.widget.Button;
import android.content.Intent;

import android.app.Activity;
import android.view.MenuItem;
import android.util.DisplayMetrics;

import android.view.Menu;
import android.content.Intent;
import android.view.MenuInflater;

import android.os.Bundle;
import android.content.Context;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    private Button button;
    public static MediaPlayer mediaPlayer;
    public ToggleButton toggleMuteHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.unem);
        mediaPlayer.start();

        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.buttonMain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        toggleMuteHomeButton = (ToggleButton) findViewById(R.id.toggleMuteHomeButton);
        ImageSpan imageSpan = new ImageSpan(this, android.R.drawable.ic_lock_silent_mode);
        SpannableString spannableString = new SpannableString("X");
        spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toggleMuteHomeButton.setText(spannableString);
        toggleMuteHomeButton.setTextOn(spannableString);
        toggleMuteHomeButton.setTextOff(spannableString);

        toggleMuteHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //boolean toggleState = toggleMuteButton.isChecked();
                if (toggleMuteHomeButton.isChecked()){

                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

//                    Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT);

//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    //toggleMuteButton.setSoundEffectsEnabled(true);
                }
                else {

                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    //Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT);

//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                    //toggleMuteButton.setSoundEffectsEnabled(false);
                }
            }
        });

        toggleMuteHomeButton.setChecked(true);
    }

    @Override
    protected void onPause(){
        if(mediaPlayer != null || mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onPause();
    }

}

package com.kekcom.thewokingdead;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    public static MediaPlayer mediaPlayer;
    public static ToggleButton toggleMuteHomeButton;
    public static ToggleButton toggleDebug;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.unem);
        mediaPlayer.start();

        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.textView);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/CHINESETAKEAWAY.ttf");
        tv.setTypeface(face);
        button = (Button) findViewById(R.id.buttonMain);
        button.setBackgroundResource(R.drawable.zcbggo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        toggleMuteHomeButton = (ToggleButton) findViewById(R.id.toggleMuteHomeButton);
        ImageSpan imageSpan = new ImageSpan(this, R.drawable.zcbggo);
        toggleMuteHomeButton.setBackgroundResource(R.drawable.zcbggo);
        SpannableString spannableString = new SpannableString("Mute");
        spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toggleMuteHomeButton.setText(spannableString);
        toggleMuteHomeButton.setTextOn(spannableString);
        toggleMuteHomeButton.setTextOff(spannableString);

        toggleDebug = (ToggleButton) findViewById(R.id.toggleDebug);
        ImageSpan imageSpan1 = new ImageSpan(this, R.drawable.zcbggo);
        toggleDebug.setBackgroundResource(R.drawable.zcbggo);
        SpannableString spannableString1 = new SpannableString("Demo");
        spannableString.setSpan(imageSpan1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toggleDebug.setText(spannableString1);
        toggleDebug.setTextOn(spannableString1);
        toggleDebug.setTextOff(spannableString1);

        toggleMuteHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //boolean toggleState = toggleMuteButton.isChecked();
                if (toggleMuteHomeButton.isChecked()){
                    mediaPlayer.setVolume(0.90f, 0.90f);
//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

//                    Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT);

//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    //toggleMuteButton.setSoundEffectsEnabled(true);
                }
                else {

                    mediaPlayer.setVolume(0, 0);
//                    AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
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
            //mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onPause();
    }

}

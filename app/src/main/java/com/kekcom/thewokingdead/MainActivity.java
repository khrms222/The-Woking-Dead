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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    public static MediaPlayer mediaPlayer;
    public static ToggleButton toggleMuteHomeButton;
    public static ToggleButton toggleDebug;
    private Button button;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                button.setBackgroundResource(R.drawable.canvas_bg_01);
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
                    toggleMuteHomeButton.setBackgroundResource(R.drawable.zcbggo);
                    mediaPlayer.setVolume(0.50f, 0.50f);
                }
                else {
                    toggleMuteHomeButton.setBackgroundResource(R.drawable.canvas_bg_01);
                    mediaPlayer.setVolume(0, 0);
                }
            }
        });

        toggleDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //boolean toggleState = toggleMuteButton.isChecked();
                if (!toggleDebug.isChecked()) {
                    toggleDebug.setBackgroundResource(R.drawable.zcbggo);
                } else {
                    toggleDebug.setBackgroundResource(R.drawable.canvas_bg_01);
                }
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.unem);
        mediaPlayer.setVolume(0.50f, 0.50f);
        mediaPlayer.start();

        toggleMuteHomeButton.setChecked(true);

        MobileAds.initialize(this, APP_ID);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

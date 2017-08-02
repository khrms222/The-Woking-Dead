package com.kekcom.thewokingdead;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener{

    private Button backButton;
    private Button playAgainButton;

    private TextView gameOverText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long timed = System.currentTimeMillis() + 1500;
        super.onCreate(savedInstanceState);
        MainActivity.mediaPlayer.release();
        MainActivity.mediaPlayer = MediaPlayer.create(this, R.raw.menu);

        setContentView(R.layout.activity_game_over);

        //setToFullScreen();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.4));

        backButton = (Button) findViewById(R.id.backButton);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        gameOverText = (TextView) findViewById(R.id.gameOverText);

        Typeface uiTypeface = Typeface.createFromAsset(getAssets(), "fonts/CHINESETAKEAWAY.ttf");
        backButton.setTypeface(uiTypeface);
        playAgainButton.setTypeface(uiTypeface);
        gameOverText.setTypeface(uiTypeface);

        Intent i = getIntent();

        backButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        MainActivity.mediaPlayer.start();

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
    }

    @Override
    protected void onPause() {
        if (MainActivity.mediaPlayer != null || MainActivity.mediaPlayer.isPlaying()) {

            MainActivity.mediaPlayer.stop();
            MainActivity.mediaPlayer.release();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //setToFullScreen();
    }

    @Override
    public void onClick(View v) {
        //setToFullScreen();

        switch(v.getId()){
            case R.id.backButton:
                Intent mainMenu = new Intent(GameOverActivity.this, MainActivity.class);

                mainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(mainMenu);

                finish();
                break;
            case R.id.playAgainButton:
                Intent playAgain = new Intent(GameOverActivity.this, GameActivity.class);

                playAgain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(playAgain);

                finish();

                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainMenu = new Intent(this, MainActivity.class);

        mainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(mainMenu);

        finish();
    }

    public void setToFullScreen(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}

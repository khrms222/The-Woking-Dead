package com.kekcom.thewokingdead;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.unem);
        mediaPlayer.start();

        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.buttonMain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                mediaPlayer.pause();
                mediaPlayer.release();
                startActivity(i);
            }
        });
    }
}

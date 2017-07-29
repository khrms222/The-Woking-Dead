package com.kekcom.thewokingdead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    private Button buttonMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMain = (Button) findViewById(R.id.buttonMain);

        buttonMain.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, Start.class);

                startActivity(intent);
            }
        });
    }
}

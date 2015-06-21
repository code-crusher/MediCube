package com.appyware.medicube;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


public class BluetoothActivity extends AppCompatActivity {

    Button sleepButton,tempButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar=(Toolbar) findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        sleepButton = (Button) findViewById(R.id.sleepButton);
        tempButton = (Button) findViewById(R.id.tempButton);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sleepIntent = new Intent(BluetoothActivity.this, SleepActivity.class);
                startActivity(sleepIntent);
            }
        });
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timerIntent = new Intent(BluetoothActivity.this, TimerActivity.class);
                startActivity(timerIntent);
            }
        });
    }
}

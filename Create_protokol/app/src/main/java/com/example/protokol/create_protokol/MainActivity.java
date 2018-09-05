package com.example.protokol.create_protokol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void roomElement (View view) {
        Intent intent = new Intent("android.intent.action.RoomElement");
        startActivity(intent);
    }
}




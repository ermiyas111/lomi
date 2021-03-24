package com.example.hp.lomidemoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ChooseBankActivity extends AppCompatActivity {

    private String[] banks = new String[] {
            "CBE",
            "Bunna",
            "Berhan",
            "COOP",
            "Abay"
    };
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bank);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("on start: ", "main ac started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("on resume: ", "choose bank resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("on pause: ", "choose bank paused");
    }

    public void bankChoosed(View view){
        int bankTag = Integer.parseInt(view.getTag().toString());
        Toast.makeText(getApplicationContext(), banks[bankTag] + " Clicked!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

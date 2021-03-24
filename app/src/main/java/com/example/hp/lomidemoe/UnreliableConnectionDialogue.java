package com.example.hp.lomidemoe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hp on 12/23/2018.
 */

public class UnreliableConnectionDialogue extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public View receivedView;
    public Dialog d;
    public Button try_again, cancel, offline;

    public UnreliableConnectionDialogue(Activity a, View buttonView) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.receivedView = buttonView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offline_dialogue);

        try_again = (Button) findViewById(R.id.btn_again);
        cancel = (Button) findViewById(R.id.btn_cancel);
        offline = (Button) findViewById(R.id.btn_offline);
        try_again.setOnClickListener(this);
        cancel.setOnClickListener(this);
        offline.setOnClickListener(this);

        //make the card buttons available for another click
        BankCredentials.setCardButtonClickedCounter(0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_again:
                dismiss();
                //c.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                //MainActivity.buy(receivedView);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_offline:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}

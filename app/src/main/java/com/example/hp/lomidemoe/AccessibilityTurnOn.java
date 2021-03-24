package com.example.hp.lomidemoe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by hp on 12/25/2018.
 */

public class AccessibilityTurnOn extends Dialog implements
        android.view.View.OnClickListener {

public Activity c;
public Dialog d;
public Button yes;

public AccessibilityTurnOn(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        }

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.accessibility_turn_on);
    yes = (Button) findViewById(R.id.btn_yes);
    yes.setOnClickListener(this);

    //make the card buttons available for another click
    BankCredentials.setCardButtonClickedCounter(0);
    }


@Override
public void onClick(View v) {
    switch (v.getId()) {
    case R.id.btn_yes:
        dismiss();
        c.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        break;
    default:
        break;
        }
    dismiss();
    }
}

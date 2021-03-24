package com.example.hp.lomidemoe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by hp on 12/25/2018.
 */

public class RetryActivity extends AppCompatActivity{
    TextView dialogueText;
    Button retry, cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retry_activity);
        dialogueText = findViewById(R.id.dialogue_text);
        retry = findViewById(R.id.recontinue);
        cancel = findViewById(R.id.btn_cancel);
        dialogueText.setText(RetryActivityParameter.getMessage());
        if(RetryActivityParameter.getContinuable()) {
            retry.setVisibility(View.VISIBLE);
        }else{
            retry.setVisibility(View.GONE);
        }
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankCredentials.setStep(RetryActivityParameter.getLevel());
                makeACall2(RetryActivityParameter.getUSSDNumber());
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //android:theme="@android:style/Theme.Dialog"


    public void makeACall2(String phoneNumber){
        Intent intent = new Intent("android.intent.action.CALL",
                Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //start accessibility service
        BankCredentials.setAccessibilityPortalOpen(true);
    }
}

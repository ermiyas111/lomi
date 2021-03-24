package com.example.hp.lomidemoe.makeSteps.all;

import android.util.Log;

import com.example.hp.lomidemoe.BankCredentials;
import com.example.hp.lomidemoe.parser.error.ParseError;

/**
 * Created by hp on 12/21/2018.
 */

public class ZeroStep {
    public static void handleZeroStep(String receivedUSSDText) {
        if (receivedUSSDText.contains("Amount:") && receivedUSSDText.contains("Reason:") || receivedUSSDText.contains("Please Confirm")) {
            //The amount is displayed
            Log.d("0nd step: ", "Amount Displayed");
            //Toast.makeText(ResponseAccessibilityService.this, "amount displayed", Toast.LENGTH_SHORT).show();
            BankCredentials.setStep(1);
        } else if (ParseError.parse(receivedUSSDText) || ParseError.parseService(receivedUSSDText)) {
            Log.d("0nd step: ", "service not available");
            //Toast.makeText(ResponseAccessibilityService.this, "network failed to check amount", Toast.LENGTH_SHORT).show();
            BankCredentials.setStep(11);
        }


        // BankCredentials.setPreTransactions(response);
        //BankCredentials.incrementStep();
                /*for(Double transaction : BankCredentials.getPreTransactions()){
                    Log.d("onAccessibilityEvent: ", String.format("%.3f", transaction));
                    Toast.makeText(getApplicationContext(), String.format("%.3f", transaction) , Toast.LENGTH_SHORT).show();
                }*/
    }
}

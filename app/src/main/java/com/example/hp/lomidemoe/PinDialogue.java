package com.example.hp.lomidemoe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by hp on 12/23/2018.
 */

public class PinDialogue extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public EditText enteredPin;
    public static TextView showDesc, showDetail;
    public static boolean textVisibility;

    public PinDialogue(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pin_dialogue);

        enteredPin = (EditText) findViewById(R.id.pinEditText);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        showDesc = findViewById(R.id.txt_description);
        showDetail = findViewById(R.id.txt_detail);
        showDetail.setVisibility(View.GONE);
        textVisibility = false;
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        //make the card buttons available for another click
        BankCredentials.setCardButtonClickedCounter(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();

                BankCredentials.setPin(enteredPin.getText().toString());
                BankCredentials.setPINSet(true);
                BankCredentials.setSharedpreferences(c.getSharedPreferences("myPref", Context.MODE_PRIVATE));
                //Check if there is already a stored ID
                //if (BankCredentials.getSharedpreferences().contains(purchaseAmount)) {
                Log.d("onSharedStored: ", "secondTimer");
                Log.d("onPinDialog: ", "*" + BankCredentials.getPin() + "#");
                Log.d("onPinDialog: ", BankCredentials.validatePin());
                //BankCredentials.setStep(2);

                //Check if we are buying for a friend or not
                if(BankCredentials.getBuyingForFriend() && !BankCredentials.getFriendsNumber().equals("")){
                    BankCredentials.setFriendsNumber(FragmentCard.getCardReceiver());
                }

                BankCredentials.setStep(2);
                BankCredentials.setAccessibilityPortalOpen(true);
                SharedPreferences.Editor editor = BankCredentials.getSharedpreferences().edit();
                //editor.putString(getPurchaseAmount(), "FT183563DXD4");
                Log.d("onStoringId: ", "ID stored");
                editor.apply();

                //start the purchase by calling transaction
                c.startService(new Intent(c, ResponseAccessibilityService.class));
                c.startActivity(new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + BankCredentials.getCBETransactionId("1")/*BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount())BankCredentials.validatePin()*/)));
                Log.d("Card Amount F: ", MainActivity.getPurchaseAmount());
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public static boolean getTextVisibility(){
        return textVisibility;
    }
    public static void setTextVisibility(boolean newValue){
        textVisibility = newValue;
    }

    public static TextView getText(){
        return showDetail;
    }
    public static void setTextVisibility( TextView newValue){
        showDetail = newValue;
    }
}

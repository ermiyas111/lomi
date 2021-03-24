package com.example.hp.lomidemoe;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.lomidemoe.parser.error.ParseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseAccessibilityService extends AccessibilityService {
    private String USSDText;
    private String tempUSSDStorage;
    private String latestTransactionAmount;
    private DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
    //private String paymentReceiver="NEBIL SEID YASIN";
    private String paymentReceiver="ERMIAS BITEW MELESSE";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        List<CharSequence> response = event.getText();
        USSDText = processUSSDText(response);

        Log.d("onAccessibilityEvent: ", event.getPackageName().toString() + " " + event.getClassName().toString().toLowerCase());
        Log.d("onAccessibilityEvent: ", USSDText);
        Log.d("onAccessibilityEvent: ", Integer.toString(BankCredentials.getStep()));
        handleExceptionalMessages();

        if(USSDText.equals(tempUSSDStorage)){
            Log.d("ontemp: ", "equals");
        }


        if (event.getPackageName().toString().contains("com.android.phone")
                && event.getClassName().toString().toLowerCase()
                .contains("alert")
                && !USSDText.equals(tempUSSDStorage)
                && BankCredentials.getAccessibilityPortalOpen()) {//make sure same message isn't sent twice and the e
            BankCredentials.setAccessibilityPortalOpen(false);
            //declare progress dialogue which will display concurrently with ussd running
            final ProgressDialog progressDoalog = new ProgressDialog(this);

            //MainActivity.getPurchaseAmount()
            if (BankCredentials.getStep() == 0) { // pre trans action2
                if (USSDText.contains("Welcome to CBE Mobile Banking") && USSDText.contains("My Accounts") && USSDText.contains("Own Account Transfer")) {
                    Log.d("0nd step: ", "first step");
                    Toast.makeText(ResponseAccessibilityService.this, "first step", Toast.LENGTH_SHORT).show();
                    BankCredentials.setStep(6);
                }else if(USSDText.contains("Please enter your PIN to login:") && USSDText.contains("Have you changed your PIN?")){
                    BankCredentials.setStep(0);
                    receiveDialogue(0, "Wrong Pin entered. You only got one more try. Don't input the pin unless you are sure on the next try, or your account will be blocked.","", false);
                }else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                    Log.d("0nd step: ", "service not available");
                    Toast.makeText(ResponseAccessibilityService.this, "network failed to check amount", Toast.LENGTH_SHORT).show();
                    receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                    //resetVars();
                }
                //BankCredentials.setStep(0);
            }else if (BankCredentials.getStep() == 6) { // pre trans action2
                if (USSDText.contains("Amount:") && USSDText.contains("Reason:") && USSDText.contains("Please Confirm")) {
                    Log.d("6nd step: ", "Amount Displayed");
                    Toast.makeText(ResponseAccessibilityService.this, "amount displayed", Toast.LENGTH_SHORT).show();
                    BankCredentials.setStep(1);
                        /*progressDoalog.setMessage("Purchasing Card...");
                        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDoalog.setCancelable(false);
                        progressDoalog.show();*/
                }else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                    Log.d("6nd step: ", "service not available");
                    Toast.makeText(ResponseAccessibilityService.this, "network failed to check amount", Toast.LENGTH_SHORT).show();
                    receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                    //resetVars();
                }
                //BankCredentials.setStep(0);
            } else if (BankCredentials.getStep() == 1) { // transaction
                if (ParseError.parse(USSDText)) {
                    Log.d("1st step: ", "please try again");
                    BankCredentials.setSharedpreferences(getSharedPreferences("myPref", Context.MODE_PRIVATE));
                    if (!BankCredentials.getSharedpreferences().contains(MainActivity.getPurchaseAmount())) {//check if there is saved id
                        Log.d("1st step: ", "first time user");
                        BankCredentials.setStep(7);
                    } else if (BankCredentials.getSharedpreferences().contains(MainActivity.getPurchaseAmount())) {
                        Log.d("1st step: ", "2nd time user");
                        BankCredentials.setStep(2);
                    }
                    //Toast.makeText(ResponseAccessibilityService.this, "network failed So need confirmation", Toast.LENGTH_SHORT).show();
                    /*Map<String, Object> hopperUpdates = new HashMap<>();
                    hopperUpdates.put(MainActivity.getSecretNumber() + "/State", MainActivity.transcribeAmount(MainActivity.getPurchaseAmount()) + "_filled");
                    myRef1.updateChildren(hopperUpdates);
                    BankCredentials.setSharedpreferences(getSharedPreferences("myPref", Context.MODE_PRIVATE));
                    if (BankCredentials.getSharedpreferences().contains("fetchedSerialNumber")) {
                        //Delete the stored shared preference for first timers
                        BankCredentials.getSharedpreferences().edit().remove("fetchedSerialNumber").commit();
                    }*/
                } else if (USSDText.contains("Complete")) {
                    Log.d("1st step: ", "completed message displayed");
                    storeId(parseTransactionID(USSDText));
                    BankCredentials.setStep(11);
                }else if (ParseError.parseService(USSDText)){
                    Log.d("1st step: ", "parse service");
                    receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                    //resetVars();
                }
            } else if (BankCredentials.getStep() == 2) { // check the id of trans action
                if (USSDText.contains("Press any key except 1 and 2 for more") && USSDText.contains(paymentReceiver) && parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) && USSDText.contains("debited from")) {
                    //check the amount, receiver & if it is sent or received then display the remaining message part
                    Log.d("2nd step: ", "further needed");
                    BankCredentials.setStep(5);
                }else if (USSDText.contains("Press any key except 1 and 2 for more")) {
                    //if we are not receivers or amount is not the same as purchase
                    Log.d("2nd step: ", "not receiver press");
                    BankCredentials.setStep(3);
                }else if(USSDText.contains("My Accounts")){
                    //no transaction found
                    receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                    //resetVars();
                }else {
                    Log.d("2nd step: ", "first else");
                    if (checkID(parseTransactionID(USSDText)).equals("ID matches")) {
                        //Toast.makeText(ResponseAccessibilityService.this, "Id matches", Toast.LENGTH_SHORT).show();
                        Log.d("2nd step: ", "Id matches");
                        //step, message, callActivity, continuable
                        receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                        //resetVars();
                    } else if ((!USSDText.contains(paymentReceiver) || !parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) || !USSDText.contains("debited from")) && checkID(parseTransactionID(USSDText)).equals("ID is new") && (USSDText.contains("credited to") || USSDText.contains("debited from"))) {
                        //if ID is new and we are not payment receivers, or we are receivers
                        //Toast.makeText(ResponseAccessibilityService.this, BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(),""), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(ResponseAccessibilityService.this, parseTransactionID(USSDText), Toast.LENGTH_LONG).show();
                        Toast.makeText(ResponseAccessibilityService.this, "Id is new not receivers", Toast.LENGTH_SHORT).show();
                        Log.d("2nd step: ", "Id new not receivers");
                        BankCredentials.setStep(3);
                    } else if (USSDText.contains(paymentReceiver) && parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) && USSDText.contains("debited from") && checkID(parseTransactionID(USSDText)).equals("ID is new")) {
                        Toast.makeText(ResponseAccessibilityService.this, "receive Id is newyghjkl", Toast.LENGTH_SHORT).show();
                        storeId(parseTransactionID(USSDText));
                        Log.d("storedID: ", BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(), "")/*parseTransactionID(USSDText)*/);
                        Log.d("2nd step: ", "Id new yes receivers");
                        BankCredentials.setStep(11);
                    } else if (ParseError.parse(USSDText)) {
                        //Dialogue for trying again 3 times then pausing the purchase and saving the date
                        Log.d("2nd step: ", "please try again");
                        BankCredentials.setStep(11);
                    } else {
                        Log.d("2nd step: ", "else");
                        BankCredentials.setStep(11);
                    }
                }

            } else if (BankCredentials.getStep() == 3) { // charged
                if (USSDText.contains("Available Balance")) {
                    Log.d("3nd step: ", "Gonna parse");
                    if (!BankCredentials.getTransactionListIsParsed()) {
                        Log.d("3nd step: ", "Gonna parse checked");
                        BankCredentials.setPicker(BankCredentials.parseTransactionList(USSDText));
                        Log.d("3nd step: ", BankCredentials.getPicker().toString());
                        BankCredentials.setTransactionListIsParsed(true);
                    }
                    if(BankCredentials.getPicker().size()>0 && BankCredentials.getTransactionListCounter() < BankCredentials.getPicker().size()) {//if the parsed list doesn't return empty list
                        BankCredentials.setStep(4);
                    }else{
                        receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                        //resetVars();
                    }
                } else if (ParseError.parse(USSDText) && ParseError.parseService(USSDText)) {
                    Log.d("3rd step: ", "please try again");
                    //Dialogue for trying again 3 times then pausing the purchase and saving the date
                    BankCredentials.setStep(11);
                }
                //BankCredentials.setStep(0);
            } else if (BankCredentials.getStep() == 4) { // charged
                if (USSDText.contains("Press any key except 1 and 2 for more") && USSDText.contains(paymentReceiver) && parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount())) {
                Log.d("4th step: ", "press any key");
                    BankCredentials.setStep(5);
                }else if (USSDText.contains("Press any key except 1 and 2 for more")) {
                    Log.d("4th step: ", "not receiver press");
                    BankCredentials.setStep(3);
                }else {
                    if (checkID(parseTransactionID(USSDText)).equals("ID matches")) {
                        Log.d("4th step: ", "Id matches");
                        receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                        //resetVars();
                    } else if ((!USSDText.contains(paymentReceiver) || !parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) || !USSDText.contains("debited from")) && checkID(parseTransactionID(USSDText)).equals("ID is new") && (USSDText.contains("credited to") || USSDText.contains("debited from") && (USSDText.contains("credited to") || USSDText.contains("debited from")))) {
                        Log.d("4th step: ", "Id is new not receivers");
                        BankCredentials.setStep(3);
                    } else if (USSDText.contains(paymentReceiver) && parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) && USSDText.contains("debited from") && checkID(parseTransactionID(USSDText)).equals("ID is new")) {
                        Log.d("4th step: ", "Id new yes receivers");
                        storeId(parseTransactionID(USSDText));
                        Log.d("storedID: ", parseTransactionID(USSDText));
                        BankCredentials.setStep(11);
                    } else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                        Log.d("4th step: ", "please try again");
                        //Dialogue for trying again 3 times then pausing the purchase and saving the date
                        BankCredentials.setStep(11);
                    }
                }
            } else if (BankCredentials.getStep() == 5) { // charged
                Log.d("5th step: ", parseTransactionID(USSDText));
                Log.d("5th step: ", BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(), ""));
                if(BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(), "").equals("FT183563DXD4")){
                    Log.d("5th step: ", "equals");
                }
                if (USSDText.contains("via Mobile vide Txn ID:") && checkID(parseTransactionID(USSDText)).equals("ID matches")) {
                    Log.d("5th step: ", "Id matches");
                    receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                    //resetVars();
                }else if (USSDText.contains("via Mobile vide Txn ID:") && checkID(parseTransactionID(USSDText)).equals("ID is new")) {
                    storeId(parseTransactionID(USSDText));
                    Log.d("storedID: ", parseTransactionID(USSDText));
                    Log.d("5th step: ", "Id new yes receivers");
                    BankCredentials.setStep(11);
                }else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                    Log.d("5th step: ", "please try again");
                    //Dialogue for trying again 3 times then pausing the purchase and saving the date
                    BankCredentials.setStep(11);
                }
                //BankCredentials.setStep(0);
            }else if (BankCredentials.getStep() == 7){ // charged
                if (USSDText.contains(paymentReceiver) && parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) && USSDText.contains("debited from")) {
                    //check we are money receivers and amount
                    Log.d("7th step: ", "money is receiver");
                    if (USSDText.contains("Press any key except 1 and 2 for more")) {
                        Log.d("7th step: ", "press any key");
                        BankCredentials.setStep(8);
                    } else {
                        //store id
                        storeId(parseTransactionID(USSDText));
                        BankCredentials.setStep(11);
                    }
                }else if(USSDText.contains("My Accounts")){
                    //no transaction found
                    receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                    //resetVars();
                }else if(!USSDText.contains(paymentReceiver) || !parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) || !USSDText.contains("debited from") && (USSDText.contains("credited to") || USSDText.contains("debited from"))){
                    //This is not the purchase we are looking for
                    BankCredentials.setStep(9);
                }else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                    Log.d("7th step: ", "please try again");
                    BankCredentials.setStep(11);
                }
            }else if (BankCredentials.getStep() == 8){ // charged
                if(USSDText.contains("via Mobile vide Txn ID:")){
                    storeId(parseTransactionID(USSDText));
                    BankCredentials.setStep(11);
                }else if(ParseError.parse(USSDText) || ParseError.parseService(USSDText)){
                    //If network failed buy the card
                    BankCredentials.setStep(11);
                }
            }else if (BankCredentials.getStep() == 9) { // charged
                if (USSDText.contains("Available Balance")) {
                    Log.d("9th step: ", "Available");
                    if (!BankCredentials.getTransactionListIsParsed()) {
                        Log.d("9th step: ", "Gonna parse checked");
                        BankCredentials.setPicker(BankCredentials.parseTransactionList(USSDText));
                        BankCredentials.setTransactionListIsParsed(true);
                    }
                    if(BankCredentials.getPicker().size()>0 && BankCredentials.getTransactionListCounter() < BankCredentials.getPicker().size()) {//if the parsed list doesn't return empty list
                        BankCredentials.setStep(10);
                    }else{
                        receiveDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?",BankCredentials.getCBESendCodeAndNetwork(MainActivity.getPurchaseAmount()), true);
                        //resetVars();
                    }
                } else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                    Log.d("9th step: ", "please try again");
                    //Dialogue for trying again 3 times then pausing the purchase and saving the date
                    BankCredentials.setStep(11);
                }
            }else if (BankCredentials.getStep() == 10) { // charged
                if (USSDText.contains(paymentReceiver) && parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) && USSDText.contains("debited from")) {
                    //check we are money receivers and amount
                    Log.d("10th step: ", "money is receiver");
                    if (USSDText.contains("Press any key except 1 and 2 for more")) {
                        Log.d("10th step: ", "press any key");
                        BankCredentials.setStep(8);
                    } else {
                        //store id
                        storeId(parseTransactionID(USSDText));
                        BankCredentials.setStep(11);
                    }
                } else if (!USSDText.contains(paymentReceiver) || !parseTransactionAmount(USSDText).equals(MainActivity.getPurchaseAmount()) || !USSDText.contains("debited from") && (USSDText.contains("credited to") || USSDText.contains("debited from"))) {
                    //This is not the purchase we are looking for
                    BankCredentials.setStep(9);
                } else if (ParseError.parse(USSDText) || ParseError.parseService(USSDText)) {
                    Log.d("10th step: ", "please try again");
                    BankCredentials.setStep(11);
                }
            }else if (BankCredentials.getStep() == 11) { // charged
                if (USSDText.contains("Press")) {
                    Log.d("11th step: ", "Press");
                    //display message
                    BankCredentials.setAccessibilityPortalOpen(false);
                    //resetVars();
                } else if (USSDText.contains("Wrong voucher")) {
                    Log.d("11th step: ", "Wrong voucher");
                    //submit form for complaint and contact number
                    //resetVars();
                } else {
                    Log.d("11th step: ", "else");
                    receiveDialogue(0, "The ethio telecom network isn't working to recharge your airtime. The number you purchased is " + BankCredentials.getCardNumber() +". You can fill it manually.",BankCredentials.getRechargeBalance(MainActivity.getSecretNumber(), BankCredentials.getFriendsNumber()), true);
                    //resetVars();
                }
            }else{
                BankCredentials.setStep(0);
            }


            AccessibilityNodeInfo source = getRootInActiveWindow();

            if (source != null) {
                if (BankCredentials.getStep() == 6) {//input the last step of the transaction
                    inputValueProgrammatically(source, "4*1*" + BankCredentials.getReceiverAccount() + "*1*" + MainActivity.getPurchaseAmount() + "*0*1");
                } else if (BankCredentials.getStep() == 1) {//input the last step of the transaction
                    inputValueProgrammatically(source, "1");
                } else if (BankCredentials.getStep() == 2) {//make a call to the latest transaction details
                    chooseButton(USSDText, source);
                    makeACall(BankCredentials.getCBETransactionId("1"));
                } else if (BankCredentials.getStep() == 3) {//return one step
                    inputValueProgrammatically(source, "*");
                } else if (BankCredentials.getStep() == 4) {//input the parsed list of transaction programmatically
                    Log.d("parse list trans: ", BankCredentials.getPicker().get(1));
                    //if all transactions are not checked
                    inputValueProgrammatically(source, BankCredentials.getPicker().get(BankCredentials.getTransactionListCounter()));
                    BankCredentials.incrementTransactionListCounter();
                } else if (BankCredentials.getStep() == 5) {//input 1
                    inputValueProgrammatically(source, "1");
                }else if (BankCredentials.getStep() == 7) { //
                    chooseButton(USSDText, source);
                    makeACall(BankCredentials.getCBETransactionId("1"));
                }else if (BankCredentials.getStep() == 8){
                    inputValueProgrammatically(source, "1");
                }else if (BankCredentials.getStep() == 9){
                    inputValueProgrammatically(source, "*");
                }else if (BankCredentials.getStep() == 10){
                    inputValueProgrammatically(source,BankCredentials.getPicker().get(BankCredentials.getTransactionListCounter()));
                    BankCredentials.incrementTransactionListCounter();
                } else if (BankCredentials.getStep() == 11) {
                        /*Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put(MainActivity.getSecretNumber() + "/State", MainActivity.transcribeAmount(MainActivity.getPurchaseAmount()) + "_filled");
                        myRef1.updateChildren(hopperUpdates);
                        BankCredentials.setSharedpreferences(getSharedPreferences("myPref", Context.MODE_PRIVATE));*/
                        /*if (BankCredentials.getSharedpreferences().contains("fetchedSerialNumber")) {
                            Log.d("1st step: ", "delete the stored card serial number");
                            //Delete the stored shared preference for first timers
                            BankCredentials.getSharedpreferences().edit().remove("fetchedSerialNumber").commit();
                        }*/
                    //progressDoalog.dismiss();
                    chooseButton(USSDText, source);
                    makeACall(BankCredentials.getRechargeBalance(MainActivity.getSecretNumber(), BankCredentials.getFriendsNumber()));
                }
            }
            tempUSSDStorage=USSDText;
        }

    }

    private static boolean accessibilityMustRun=false;

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        Log.d("onAccessibilityEvent: ", "Just checking");
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }


    private String processUSSDText(List<CharSequence> eventText){
        for(CharSequence s: eventText){
            String text = String.valueOf(s);
            return text;
        }
        return null;
    }

    //get the ID of the transaction from the list of transactions
    private String parseTransactionID(String transactionDetails){
        String[] words = transactionDetails.split(" ");
        String lastWord = words[words.length-1].trim();
        if(lastWord.contains(".")) {
            String dotRemover = lastWord.substring(0,lastWord.length()-2);
            return dotRemover;
        }
        return lastWord;
    }
    //parse transaction amount on the window that show transaction ID
    private String parseTransactionAmount(String transactionDetails){
        Log.d("10th step: ", transactionDetails);
        String[] words = transactionDetails.split("\n");
        for(int j=0; j<words.length; j++){
            String[] words2 = words[j].split(" ");
            for(int i=0; i<words2.length; i++) {
                if (words2[i].trim().equals("ETB")) {
                    i++;
                    String[] decimals = words2[i].trim().split("\\.");
                    return decimals[0];
                }
            }
        }
        return "";
    }
    //parse transaction
    private String checkID(String sentID){
        BankCredentials.setSharedpreferences(getSharedPreferences("myPref", Context.MODE_PRIVATE));
        if (BankCredentials.getSharedpreferences().contains(MainActivity.getPurchaseAmount())) {
            if(BankCredentials.getStep() == 2 && !USSDText.contains(paymentReceiver)){
                Log.d("2nd step: ", BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(), ""));
                Log.d("2nd step: ", sentID);
                //Toast.makeText(ResponseAccessibilityService.this, BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(), ""), Toast.LENGTH_SHORT).show();
                //Toast.makeText(ResponseAccessibilityService.this, sentID, Toast.LENGTH_LONG).show();
            }
            if (BankCredentials.getSharedpreferences().getString(MainActivity.getPurchaseAmount(), "").equals(sentID)) {
                return "ID matches";
            } else{
                return "ID is new";
            }
        }
        return "";
    }
    //storing ID locally
    private void storeId(String receivedID){
        SharedPreferences.Editor editor = BankCredentials.getSharedpreferences().edit();
        editor.putString(MainActivity.getPurchaseAmount(), receivedID);
        Log.d("onStoringId: ", "ID stored");
        editor.apply();
    }

    public static boolean getAccessibilityMustRun(){
        return accessibilityMustRun;
    }
    public static void setAccessibilityMustRun(boolean runningState){
        accessibilityMustRun=runningState;
    }
    //choose which button to click
    public static void chooseButton(String receivedUSSD, AccessibilityNodeInfo receivedSource){
        if (ParseError.parse(receivedUSSD) || ParseError.parseService(receivedUSSD)) {
            List<AccessibilityNodeInfo> list = receivedSource.findAccessibilityNodeInfosByText("OK");
            for (AccessibilityNodeInfo node : list) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }else if (receivedUSSD.contains("Exceeded maximum PIN attempts!") || receivedUSSD.contains("Have you changed your PIN?") || receivedUSSD.contains("Your Acct is not Sufficiently funded to effect the Txn")) {
        } else{
            List<AccessibilityNodeInfo> list = receivedSource.findAccessibilityNodeInfosByText("CANCEL");
            for (AccessibilityNodeInfo node : list) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        //start accessibility service
        BankCredentials.setAccessibilityPortalOpen(true);
    }
    //programmatically input one
    public static void inputValueProgrammatically(AccessibilityNodeInfo receivedSource, String receivedValue){
        AccessibilityNodeInfo inputNode = receivedSource.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (inputNode != null) {//prepare your text then fill it using ACTION_SET_TEXT
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, receivedValue);
            inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
        List<AccessibilityNodeInfo> list = receivedSource.findAccessibilityNodeInfosByText("SEND");
        for (AccessibilityNodeInfo node : list) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        //start accessibility service
        BankCredentials.setAccessibilityPortalOpen(true);
    }
    //make a call
    public void makeACall(String phoneNumber){
        Intent intent = new Intent("android.intent.action.CALL",
                Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //start accessibility service
        BankCredentials.setAccessibilityPortalOpen(true);
    }
    //reset everything at the end of the steps
    public static void resetVars(){
        Log.d("12th step: ", "final step");
        //set pin to null
        BankCredentials.setPin(null);
        //set pinSet to false
        BankCredentials.setPINSet(false);
        //set card number to null
        BankCredentials.setCardNumber(null);
        //set steps to 0
        BankCredentials.setStep(0);
        //set transaction list counter to 0
        BankCredentials.setTransactionListCounter(0);
        //set transaction list is parsed to false
        BankCredentials.setTransactionListIsParsed(false);
        //set ArrayList to null
        BankCredentials.setPicker(null);

        BankCredentials.setAccessibilityPortalOpen(false);
        BankCredentials.setRequestGranted(false);
        //BankCredentials.setCardButtonClicked(false);
        BankCredentials.setCardButtonClickedCounter(0);
        BankCredentials.setWhichDialogDismiss(0);
    }
    //handle exceptional messages from triggering any event
    public void handleExceptionalMessages(){
        if (USSDText.contains("Exceeded maximum PIN attempts!") || USSDText.contains("Have you changed your PIN?") || USSDText.contains("Your Acct is not Sufficiently funded to effect the Txn") || USSDText.contains("Please request access to ussd banking.")) {
            BankCredentials.setAccessibilityPortalOpen(false);
            Log.d("onException: ", "ID stored");
            //receiveDialogue(0,"fhgjkl","0911",false);
        }
    }
    //
    public void receiveDialogue(int level, String message, String USSDNumber, boolean continuable){
        AccessibilityNodeInfo source = getRootInActiveWindow();
        if (source != null) {
            chooseButton(USSDText, source);
            RetryActivityParameter.setLevel(level);
            RetryActivityParameter.setMessage(message);
            RetryActivityParameter.setUSSDNumber(USSDNumber);
            RetryActivityParameter.setContinuable(continuable);
            BankCredentials.setStep(0);
            Intent dialogueIntent = new Intent(this, RetryActivity.class);
            dialogueIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogueIntent);
        }
    }

    //check if the messages are allowed
    public void handleAllowedMessages(){
        //if (ParseError.parse(USSDText) || ParseError.parseService(USSDText) || (USSDText.contains("Amount:") && USSDText.contains("Reason:")) || USSDText.contains("Complete") ||) {
    }
}


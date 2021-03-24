package com.example.hp.lomidemoe;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class BankCredentials {

    private static String PIN = null;
    private static boolean PINSet = false;
    private static String cardNumber = null;
    private static int steps = 0;
    private static String receiverAccount = "1000138317227";
    //private static String receiverAccount = "1000142818373";
    private static int transactionListCounter = 0;
    private static boolean transactionListIsParsed = false;
    private static ArrayList<String> picker = null;
    private static SharedPreferences sharedpreferences;
    private static boolean buyingForFriend = false;
    private static String friendsNumber = "";
    private static boolean accessibilityPortalOpen=false;
    private static boolean requestGranted = false;
    private static boolean cardButtonClicked = false;
    private static int cardButtonClickedCounter = 0;
    private static int whichDialogDismiss = 0;



    private static Double[] preTransactions = new Double[]{
            0.0, 0.0, 0.0, 0.0
    };
    private static Double[] postTransactions = new Double[]{
            0.0, 0.0, 0.0, 0.0
    };

    // Pin getter setter
    public static void setPin(String pin){
        PIN = pin;
    }
    public static String getPin() { return PIN; }

    // pinSet getter setter
    public static boolean getPINSet() {
        return PINSet;
    }
    public static void setPINSet(boolean pinStatus) {
        PINSet = pinStatus;
    }

    // card number getter setter
    public static void setCardNumber(String number){ cardNumber = number;}
    public static String getCardNumber() { return cardNumber;}
    public static String getChargeBalance() {
        return "*805*" + cardNumber + Uri.encode("#");
    }

    public static int getStep() {
        return steps;
    }
    //step setter
    public  static void setStep(int newValue){
        //stop accessibility service when message is received
        steps=newValue;
    }

    // CBE credentials
    public static String getCBEBalanceCode() {
        return "*889*1*" + getPin() + "*1*1" + Uri.encode("#");
    }
    //Transaction ID
    public static String getCBETransactionId(String parsedValue) {
        return "*889*1*" + getPin() + "*1*1*"+ parsedValue + Uri.encode("#");
    }

    //make the call to transaction until one step is left
    public static String validatePin() {
        //"*805"
        return "*889*1*" + getPin() + Uri.encode("#");
    }

    //make the call to transaction until one step is left
    public static String getCBESendCodeAndNetwork(String amount) {
        //"*805"
        return "*889*1*" + getPin() + "*4*1*" + receiverAccount + "*1*" + amount + "*0*1" + Uri.encode("#");
    }
    //full transaction
    public static String fullTransaction(String amount) {
        return "*889*1*" + getPin() + "*4*1*" + receiverAccount + "*1*" + amount + "*0*1*1" + Uri.encode("#");
    }

    //Recharge air time for one self
    public static String getRechargeBalance(String numberToBeFilled, String friendsPhoneNumber) {
        if(getBuyingForFriend()){
            return "*805*" + numberToBeFilled + "*" + friendsPhoneNumber + Uri.encode("#");
        }else{
            return "*805*" + numberToBeFilled + Uri.encode("#");
        }
    }
    //Recharge air time for friend
    //public static String getRechargeBalanceForFriend(String numberToBeFilled, String friendsPhoneNumber) {
      //  return "*805*" + numberToBeFilled + "*" + friendsPhoneNumber + Uri.encode("#");
    //}
    //check if id is new
    //get transaction list counter
    public static int getTransactionListCounter() {
        return transactionListCounter;
    }
    //increment transaction list counter
    public static void incrementTransactionListCounter() {
        transactionListCounter++;
    }
    public static void setTransactionListCounter(int newValue) {
        transactionListCounter = newValue;
    }
    public static boolean getTransactionListIsParsed() {
        return transactionListIsParsed;
    }
    public static void setTransactionListIsParsed(boolean newValue) {
        transactionListIsParsed = newValue;
    }
    //parsing transaction list
    public static ArrayList<String> parseTransactionList(String transactionMessage){
        ArrayList<String> transactionList = new ArrayList<>();
        String[] lines = transactionMessage.trim().split("\n");
        boolean listStarted=false;
        for (int i = 0; i < lines.length; i++){
            //transactionList.add(Integer.toString(i));
            Log.d("parse list: ", lines[i]);
            if(lines[i].trim().equals("Transactions")){
                i=i+2;
                listStarted=true;
                Log.d("parse list: ", Integer.toString(i));
            }
            if(lines[i].contains("More options") && listStarted){
                listStarted = false;
            }
            if(listStarted){
                String[] words = lines[i].split(" ");
                String[] formation = lines[i].split(":");
                String[] sentAmount = words[words.length-1].trim().split("\\.");
                if(sentAmount[0].trim().equals(MainActivity.getPurchaseAmount()) && words[1].trim().equals("-")){
                    //add the parsed list value to the list
                    transactionList.add(formation[0]);
                }
            }
        }
        return transactionList;
    }
    //get array list of transactions
    public static ArrayList<String> getPicker() {
        return picker;
    }
    public static SharedPreferences getSharedpreferences() {
        return sharedpreferences;
    }
    public static void setPicker(ArrayList<String> newValue){
        picker=newValue;
    }
    public static void setSharedpreferences(SharedPreferences newValue){
        sharedpreferences=newValue;
    }
    public static boolean getBuyingForFriend(){
        return buyingForFriend;
    }
    public static void setBuyingForFriend(boolean switchValue){
        buyingForFriend=switchValue;
    }
    public static String getFriendsNumber(){
        return friendsNumber;
    }
    public static void setFriendsNumber(String cardReceiver){
        friendsNumber=cardReceiver;
    }
    public static boolean getAccessibilityPortalOpen(){
        return accessibilityPortalOpen;
    }
    public static void setAccessibilityPortalOpen(boolean accessibilityState){
        accessibilityPortalOpen=accessibilityState;
    }
    public static boolean getRequestGranted(){
        return requestGranted;
    }
    public static void setRequestGranted(boolean isItGranted){
        requestGranted=isItGranted;
    }
    public static boolean getCardButtonClicked(){
        return cardButtonClicked;
    }
    public static void setCardButtonClicked(boolean isItClicked){
        cardButtonClicked=isItClicked;
    }
    public static int getCardButtonClickedCounter(){
        return cardButtonClickedCounter;
    }
    public static void setCardButtonClickedCounter(int howManyCounted){
        cardButtonClickedCounter=howManyCounted;
    }
    public static void incrementCardButtonClickedCounter(){
        cardButtonClickedCounter=cardButtonClickedCounter+1;
    }
    public static int getWhichDialogDismiss(){
        return whichDialogDismiss;
    }
    public static void setWhichDialogDismiss(int whichOneDismissed){
        whichDialogDismiss=whichOneDismissed;
    }
    public static String getReceiverAccount(){
        return receiverAccount;
    }
}

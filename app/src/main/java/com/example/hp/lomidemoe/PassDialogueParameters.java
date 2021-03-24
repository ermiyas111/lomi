package com.example.hp.lomidemoe;

/**
 * Created by hp on 12/25/2018.
 */

public class PassDialogueParameters {
    public static void receiveDialogue(int level, String message, String USSDNumber, boolean continuable){
        RetryActivityParameter.setLevel(level);
        RetryActivityParameter.setMessage(message);
        RetryActivityParameter.setUSSDNumber(USSDNumber);
        RetryActivityParameter.setContinuable(continuable);
        BankCredentials.setAccessibilityPortalOpen(false);
    }
}

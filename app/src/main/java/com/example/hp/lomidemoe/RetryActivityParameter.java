package com.example.hp.lomidemoe;

/**
 * Created by hp on 12/25/2018.
 */

public class RetryActivityParameter {
    //step, message, callActivity, continuable
    private static int level = 0;
    private static String message  = null;
    private static String USSDNumber = null;
    private static boolean continuable = false;

    //
    public static int getLevel() {
        return level;
    }
    //step setter
    public  static void setLevel(int newValue){
        level = newValue;
    }

    //
    public static String getMessage() {
        return message;
    }
    //step setter
    public  static void setMessage(String newValue){
        message = newValue;
    }

    //
    public static String getUSSDNumber() {
        return USSDNumber;
    }
    //step setter
    public  static void setUSSDNumber(String newValue){
        USSDNumber = newValue;
    }

    //
    public static boolean getContinuable() {
        return continuable;
    }
    //step setter
    public  static void setContinuable(boolean newValue){
        continuable = newValue;
    }
}

package com.example.hp.lomidemoe.parser.error;

/**
 * Created by hp on 12/21/2018.
 */

public class ParseError {
    public static boolean parse(String receivedMessage){
        if (receivedMessage.contains("Please try again")){
            return true;
        }
        return false;
    }
    public static boolean parseService(String receivedMessage){
        if (receivedMessage.contains("This service is not available, please try later.") || receivedMessage.contains("Connection problem or invalid MMI code.")){
            return true;
        }
        return false;
    }
}

package com.example.hp.lomidemoe.background.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.example.hp.lomidemoe.background.services.UpdateCardService;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent background = new Intent(context, UpdateCardService.class);
//        context.startService(background);
        boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();
            Log.d("onReceive: ", "Internet Connection Lost");
        }
        else{
            Log.d("onReceive: ", "Internet");
            Intent background = new Intent(context, UpdateCardService.class);
            context.startService(background);
        }
    }
}

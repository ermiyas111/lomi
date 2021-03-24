package com.example.hp.lomidemoe;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by hp on 12/24/2018.
 */

public class IntroActivity extends Activity {
    Button yes;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
        //Ask permissions
        ActivityCompat.requestPermissions(IntroActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 6);



        yes = (Button) findViewById(R.id.btn_yes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, IntroActivity2.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";

        final int currentVersionCode = BuildConfig.VERSION_CODE;
        switch (requestCode) {
            case 6: {
                final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    //BankCredentials.setRequestGranted(true);
                    //AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
                    //caller.show();
                    //Ask read contacts permission
                    ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 240);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //BankCredentials.setRequestGranted(true);
                    //AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
                    //caller.show();
                    ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 240);
                }
                return;
            }
            case 240: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    //BankCredentials.setRequestGranted(true);
                    //AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
                    //caller.show();
                    //Ask read contacts permission
                    isAccessibilitySettingsOn2(this);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //BankCredentials.setRequestGranted(true);
                    //AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
                    //caller.show();
                    isAccessibilitySettingsOn2(this);
                }
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    private boolean isAccessibilitySettingsOn2(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + ResponseAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("dfs", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("gb", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("dgv", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("fdv", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("klk", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("yui", "***ACCESSIBILITY IS DISABLED***");
        }
        AccessibilityTurnOn caller = new AccessibilityTurnOn(IntroActivity.this);
        caller.show();
        return false;
    }
}

package com.example.hp.lomidemoe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hp.lomidemoe.PinDialogue.showDetail;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Context context = this;

    private DatabaseReference myRef;

    private static String pathString;

    private static String purchaseAmount;

    private static boolean progressDialogDismissed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirstRun();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        myRef = FirebaseDatabase.getInstance().getReference();

        progressDialogDismissed = false;
    }

    @Override
    protected void onPause() {
        Log.d("on start: ", "main ac paused");

        //stopService(new Intent(context, ResponseAccessibilityService.class));
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("on start: ", "main ac started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("on resume: ", "main ac resumed");
    }

    @Override
    protected void onDestroy() {
        Log.d("on start: ", "main ac destroyed");
        //stopService(new Intent(context, ResponseAccessibilityService.class));
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentCard(), "Card");
        adapter.addFragment(new FragmentTeleServices(), "TeleServices");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //Send purchase amount for response accessibility service
    public static String getPurchaseAmount() {
        return purchaseAmount;
    }
    //pass the path of the card being filled
    public static String getSecretNumber(){
        return pathString;
    }


    public void buy(final View view) {

        //reset all variables so that we can start new purchase
        ResponseAccessibilityService.resetVars();

        BankCredentials.setWhichDialogDismiss(0);
        Log.d("progress dismissed: ", "true");
        Log.d("when int count1: ", Integer.toString(BankCredentials.getCardButtonClickedCounter()));
        //BankCredentials.incrementCardButtonClickedCounter();
        Log.d("when int count: ", Integer.toString(BankCredentials.getCardButtonClickedCounter()));
        //if(BankCredentials.getCardButtonClickedCounter() == 0) {
            //BankCredentials.setCardButtonClickedCounter(1);
            //set the card button to it clicked to prevent it from being clicked again
            if (isAccessibilitySettingsOn(context) && checkCallingPermission()) {

                //purchaseAmount="1";
                purchaseAmount = view.getTag().toString();
                //Log.d("when card fetched: ", transcribeAmount(purchaseAmount));

                //Step 1 check for internet connection
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) { // Connected to a network

                    final ProgressDialog progressDoalog = new ProgressDialog(context);
                    progressDoalog.setMessage("Fetching Data...");
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDoalog.setCancelable(false);
                    progressDoalog.show();

                    progressDialogDismissed = false;

                    new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinish) {
                            //TODO Auto-generated method stub
                        }

                        @Override
                        public void onFinish() {
                            //TODO Auto-generated method stub
                            progressDoalog.dismiss();
                            progressDialogDismissed = true;
                            if(BankCredentials.getWhichDialogDismiss()==0){
                                OfflineDialogue caller3 = new OfflineDialogue(MainActivity.this);
                                caller3.show();
                            }
                            //BankCredentials.setCardButtonClickedCounter(0);
                            Log.d("progress dismissed: ", "true");
                            //show dialog for unreliable connection
                        }
                    }.start();

                    if (!progressDialogDismissed) {
                    BankCredentials.setSharedpreferences(getSharedPreferences("myPref", Context.MODE_PRIVATE));
                    if (BankCredentials.getSharedpreferences().contains("fetchedSerialNumber")) {
                        Log.d("when shared found: ", BankCredentials.getSharedpreferences().getString("fetchedSerialNumber", ""));
                        myRef.orderByChild("serialNumber").equalTo(BankCredentials.getSharedpreferences().getString("fetchedSerialNumber", "")).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                for (DataSnapshot postSnapChat2 : dataSnapshot2.getChildren()) {
                                    Log.d("when shared found: ", "found");
                                    Map<String, Object> hopperUpdates2 = new HashMap<>();
                                    hopperUpdates2.put(postSnapChat2.getKey().toString() + "/State", transcribeAmount(purchaseAmount) + "_not filled");
                                    myRef.updateChildren(hopperUpdates2);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("when card fetched: ", "failed");
                                Log.d("Fire data:", databaseError.getMessage());
                            }
                        });
                        //Delete the stored shared preference for first timers
                        BankCredentials.getSharedpreferences().edit().remove("fetchedSerialNumber").commit();
                    }

                    // Req for card number
                    myRef.orderByChild("State").equalTo(transcribeAmount(purchaseAmount) + "_not filled").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        //Log.d("when card fetched: ", BankCredentials.getCardNumber())
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(MainActivity.this, BankCredentials.getCardNumber(), Toast.LENGTH_LONG).show();
                            for (DataSnapshot postSnapChat : dataSnapshot.getChildren()) {
                                pathString = postSnapChat.getKey().toString();
                                //set card number
                                BankCredentials.setCardNumber(postSnapChat.getKey().toString());
                                Log.d("when card fetched: ", postSnapChat.getKey().toString());
                                Map<String, Object> hopperUpdates = new HashMap<>();
                                hopperUpdates.put(postSnapChat.getKey().toString() + "/State", transcribeAmount(purchaseAmount) + "_ready to fill");
                                myRef.updateChildren(hopperUpdates);

                                //store the fetched cards serial number locally so that we can update the firebase
                                SharedPreferences.Editor editorFetched = BankCredentials.getSharedpreferences().edit();
                                editorFetched.putString("fetchedSerialNumber", postSnapChat.child("serialNumber").getValue().toString());
                                editorFetched.apply();
                                Log.d("when shared stored: ", /*postSnapChat.child("serial number").getValue().toString()*/BankCredentials.getSharedpreferences().getString("fetchedSerialNumber", ""));
                                //myRef.child(postSnapChat.getKey().toString()).child("State").setValue("ready to fill");

                                progressDoalog.dismiss();
                                BankCredentials.setWhichDialogDismiss(1);

                                //BankCredentials.setCardButtonClickedCounter(0);

                                PinDialogue caller = new PinDialogue(MainActivity.this);
                                caller.show();

                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("when card fetched: ", "fg");
                            progressDoalog.dismiss();
                            Log.d("Fire data:", databaseError.getMessage());
                        }
                    });
                }

                } else {
                    // Not connected to a network
                    //showConnectionDialog();
                    OfflineDialogue caller2 = new OfflineDialogue(MainActivity.this);
                    caller2.show();//Show connect to WIFI or data dialog
                }
            }
            BankCredentials.setCardButtonClickedCounter(0);
        }
    }

    //make the description about safety of pin visible or not
    public void showDescription(View view) {
        if(PinDialogue.getTextVisibility()){
            PinDialogue.getText().setVisibility(View.GONE);
            PinDialogue.setTextVisibility(false);
        }else{
            showDetail.setVisibility(View.VISIBLE);
            PinDialogue.setTextVisibility(true);
        }
    }


    /*public void showConnectionDialog() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle("Connect To Internet");
    alertDialog.setMessage("Please connect to a WIFI or turn on mobile data (mobile" +
    "data will cost additional charges)");
    alertDialog.setPositiveButton("WIFI", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
//                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
    }
    });
    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    dialog.cancel();
                }
            });
            alertDialog.show();
    }*/

    //change numbers to letters
    public static String transcribeAmount(String digits){
        if(digits.equals("5")){
            return "Five";
        }else if(digits.equals("10")){
            return "Ten";
        }else if(digits.equals("15")){
            return "Fifteen";
        }else if(digits.equals("25")){
            return "TwentyFive";
        }else if(digits.equals("50")){
            return "Fifty";
        }else if(digits.equals("100")){
            return "OneHundred";
        }
        return null;
    }
    //check if permission is granted or not
    public boolean checkCallingPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 5);
            return false;
        }else{
            return true;
        }
    }

    //
    public boolean checkContactReadPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 239);
            return false;
        }else{
            return true;
        }
    }

    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
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
        AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
        caller.show();
        return false;
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);

        } else if (savedVersionCode == DOESNT_EXIST) {

            finish();

            // TODO This is a new install (or the user cleared the shared preferences)
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }

        // Update the shared preferences with the current version code
        //prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    //public static void callDialogue(int level) {
        //RetryDialogue caller3 = new RetryDialogue(0, "The mobile banking network isn't working properly. Would you like to try again?", BankCredentials.getCBETransactionId("1")/*BankCredentials.getCBESendCodeAndNetwork(purchaseAmount)*/, true);
        //caller3.show();
    //}
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";

        final int currentVersionCode = BuildConfig.VERSION_CODE;
        switch (requestCode) {
            case 5: {
                final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    //BankCredentials.setRequestGranted(true);
                    //AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
                    //caller.show();
                    //make the card buttons available for another click
                    //BankCredentials.setCardButtonClickedCounter(0);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //BankCredentials.setRequestGranted(true);
                    //AccessibilityTurnOn caller = new AccessibilityTurnOn(MainActivity.this);
                    //caller.show();
                    //make the card buttons available for another click
                    //BankCredentials.setCardButtonClickedCounter(0);
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }


}
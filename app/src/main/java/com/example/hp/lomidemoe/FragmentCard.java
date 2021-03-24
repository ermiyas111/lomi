package com.example.hp.lomidemoe;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class FragmentCard extends Fragment {

    Switch friendSwitch;

    EditText cardTaker;

    Button contacts;

    private static String receiveCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        friendSwitch = (Switch) rootView.findViewById(R.id.switch1);
        cardTaker = (EditText) rootView.findViewById(R.id.editText3);
        contacts = (Button) rootView.findViewById(R.id.contactsButton);
        receiveCard = cardTaker.getText().toString();

        cardTaker.setVisibility(View.GONE);
        contacts.setVisibility(View.GONE);

        friendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BankCredentials.setBuyingForFriend(isChecked);
                Log.w("onFriendSwitchs", "switch");
                if(isChecked){
                    //make input number visible
                    cardTaker.setVisibility(View.VISIBLE);
                    contacts.setVisibility(View.VISIBLE);
                    Log.w("onFriendSwitchs", "isChecked");
                    BankCredentials.setBuyingForFriend(true);
                }else{
                    cardTaker.setText(null);
                    cardTaker.setVisibility(View.GONE);
                    contacts.setVisibility(View.GONE);
                    BankCredentials.setFriendsNumber(null);
                    BankCredentials.setBuyingForFriend(false);
                }
            }
        });

        /*contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
       // @Override public void onActivityResult(int reqCode, int redsultCode, Intent data){

        //}
        /*@Override
        public void onActivityResult(int reqCode, int resultCode, Intent data){
            super.onActivityResult(reqCode, resultCode, data);
        }
        onActivityResult(int reqCode, int resultCode, PICK_CONTACT){

        }*/
        return rootView;

    }

    //
    public static String getCardReceiver(){
        return receiveCard;
    }
}

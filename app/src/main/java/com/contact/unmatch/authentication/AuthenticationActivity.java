package com.contact.unmatch.authentication;

import androidx.appcompat.app.AppCompatActivity;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.contact.unmatch.R;
import com.contact.unmatch.home.HomeActivity;


public class AuthenticationActivity extends AppCompatActivity {

    private Button create_btn;
    private Button signin_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        create_btn = (Button) findViewById(R.id.create_button);
        create_btn.setOnClickListener(create_listener);
        signin_btn = (Button) findViewById(R.id.signin_button);
        signin_btn.setOnClickListener(signin_listener);

    }

    private View.OnClickListener create_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
            startActivity(intent);
            /*TelecomManager telecomManager = getSystemService(TelecomManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startActivity(telecomManager.createManageBlockedNumbersIntent(), null);
            }*/
        }
    };
    private View.OnClickListener signin_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AuthenticationActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };
}
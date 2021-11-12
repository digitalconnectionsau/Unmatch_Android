package com.contact.unmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.contact.unmatch.model.MessageCategory;

import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

public class MainActivity extends AppCompatActivity {

    public Bundle m_savedInstanceState;
    private static final int READ_CONTACT_REQUEST_CODE = 101;
    private static final String DEVICE_DEFAULT_SMS_PACKAGE_KEY = "com.example.smsexample.deviceDefaultSmsPackage";
    private static final String INVALID_PACKAGE = "invalid_package";
    private static final int SMS_PERMISSION_CONSTANT = 100;
    SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private static final int REQUEST_PERMISSION_SETTING = 102;
    public static int REQUEST_PERMISSION = 0;
    public int service_status = 0;
    public MessageCategory messageCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        m_savedInstanceState = savedInstanceState;
        Intent i = getIntent();
        messageCategory = (MessageCategory)i.getSerializableExtra("message_category");
        Log.d("----category_name", messageCategory.getCategory());

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        setUpViews();
//        saveDeviceDefaultSmsPackage();
//        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Need SMS Permission");
//                builder.setMessage("This app needs SMS permission to send Messages.");
//                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CONSTANT);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else if (permissionStatus.getBoolean(Manifest.permission.SEND_SMS, false)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Need SMS Permission");
//                builder.setMessage("This app needs SMS permission to send Messages.");
//                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        sentToSettings = true;
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(getBaseContext(),
//                                "Go to Permissions to Grant SMS permissions", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}
//                        , SMS_PERMISSION_CONSTANT);
//            }
//
//            SharedPreferences.Editor editor = permissionStatus.edit();
//            editor.putBoolean(Manifest.permission.SEND_SMS, true);
//            editor.commit();
//
//        }

        checkPermission();
//        setDeviceDefaultSmsPackage(getPackageName());
        offerReplacingDefaultDialer();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (m_savedInstanceState == null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("message_category", messageCategory);
                Fragment contactFragment = new ContactFragment();
                contactFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, contactFragment).commit();
            }
        } else {
            readcontact_RequestPermission();
        }
        return true;
    }
    private void readcontact_RequestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_CONTACT_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(m_savedInstanceState == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("message_category", messageCategory);
                    Fragment contactFragment = new ContactFragment();
                    contactFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, contactFragment).commit();
                }
            }
        } else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

//    private void setUpViews() {
//        findViewById(R.id.set_as_default).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setDeviceDefaultSmsPackage(getPackageName());
//            }
//        });
//
//        findViewById(R.id.restore_default).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setDeviceDefaultSmsPackage(getPreviousSmsDefaultPackage());
//            }
//        });
//    }

    public void setDeviceDefaultSmsPackage(String packageName) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
        startActivityForResult(intent, 3);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == -1){
//            service_status = resultCode;
//        }
//        else{
//            service_status = 0;
//        }
        Log.d("----service_result:", String.valueOf(resultCode));
    }
    private String getPreviousSmsDefaultPackage() {
        return getPreferences(MODE_PRIVATE).getString(DEVICE_DEFAULT_SMS_PACKAGE_KEY, INVALID_PACKAGE);
    }

    private void saveDeviceDefaultSmsPackage() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (hasNoPreviousSmsDefaultPackage(preferences)) {
            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this);
            preferences.edit().putString(DEVICE_DEFAULT_SMS_PACKAGE_KEY, defaultSmsPackage).apply();
        }
    }
    private boolean hasNoPreviousSmsDefaultPackage(SharedPreferences preferences) {
        return !preferences.contains(DEVICE_DEFAULT_SMS_PACKAGE_KEY);
    }
    private void offerReplacingDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        if(!getPackageName().equals(telecomManager.getDefaultDialerPackage())){
            Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER).putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivityForResult(intent, 3);
        } else {
            PackageManager manager = getPackageManager();
            ComponentName component = new ComponentName(getPackageName(), "com.contact.unmatch.CallService");
            manager.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
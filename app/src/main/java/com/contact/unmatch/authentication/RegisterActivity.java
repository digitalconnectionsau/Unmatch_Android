package com.contact.unmatch.authentication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.contact.unmatch.R;
import com.contact.unmatch.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    private Button register_btn;
    private EditText firstName_edit;
//    private EditText lastName_edit;
    private DatePicker birthday_picker;
    private EditText email_edit;
    private EditText password_edit;
    private EditText confirm_pass_edit;
//    private CountryCodePicker ccp;
//    private String country_code = "";
//    private EditText phone_number_edit;
    private String birthday;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_btn = (Button) findViewById(R.id.register_button);
        register_btn.setOnClickListener(register_listener);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firstName_edit = (EditText) findViewById(R.id.firstname_edit);
        //lastName_edit = (EditText) findViewById(R.id.lastname_edit);
        email_edit = (EditText)findViewById(R.id.register_email_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        confirm_pass_edit = (EditText) findViewById(R.id.confirm_password_edit);

        progressBar = findViewById(R.id.progressBar);
        //ccp = findViewById(R.id.ccp);
        //ccp.hideNameCode(true);
        //ccp.setCountryPreference("US,CA");
        //country_code = ccp.getDefaultCountryCodeWithPlus();
        //ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
        //  @Override
        //      public void onCountrySelected() {
        //          country_code = ccp.getSelectedCountryCodeWithPlus();
        //      }
        //  });
        //phone_number_edit = (EditText)findViewById(R.id.editAccountNumber);
        birthday_picker = (DatePicker) findViewById(R.id.birthday_picker);
        birthday_picker.setMaxDate(System.currentTimeMillis() - 1000);
    }

    private View.OnClickListener register_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if(invalidate()) {
                    String year = String.valueOf(birthday_picker.getYear());
                    String month = String.valueOf(birthday_picker.getMonth());
                    if(birthday_picker.getMonth() < 10){
                        month = "0" + month;
                    }
                    String day = String.valueOf(birthday_picker.getDayOfMonth());
                    if(birthday_picker.getDayOfMonth() < 10){
                        day = "0" + day;
                    }
                    birthday = year + "-" + month + "-" + day;
                    Date d = new Date();
                    SimpleDateFormat format_time = new SimpleDateFormat("'T'HH:mm:ss.SSS'Z'");
                    String time = format_time.format(d);
                    birthday = birthday + time;

                    JSONObject obj = new JSONObject();
                    obj.put("name", firstName_edit.getText().toString());
                    obj.put("email", email_edit.getText().toString());
                    obj.put("password", password_edit.getText().toString());
                    obj.put("confirmPassword", confirm_pass_edit.getText().toString());
                    obj.put("dateOfBirth", birthday);

                    String url = HttpUtils.API_URL + "Authentication/Registration";
                    progressBar.setVisibility(View.VISIBLE);

                    HttpUtils.post(getApplicationContext(), null, url, obj, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            Log.d("api_result", "" + statusCode + " : " + response.toString());
                            progressBar.setVisibility(View.GONE);
                            if(statusCode == 200){
                                Toast.makeText(getApplicationContext(), R.string.message_success_register, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                if(!errorResponse.getBoolean("isSuccessfulRegistration")){
                                    //String[] error_msg_list = errorResponse.getString("errors").split(",");
                                    //String error_msg = error_msg_list[1].substring(0, error_msg_list[1].length() - 1);
                                    String errorString = errorResponse.getString("errors");
                                    Type type = new TypeToken<List<String>>(){}.getType();
                                    List<String> errorList = new Gson().fromJson(errorString, type);
                                    String error_msg = TextUtils.join("\n", errorList);
                                    Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("api_result", "" + statusCode + " : " + responseString);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.message_unknown_error, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean invalidate() {
        if(firstName_edit.getText().toString().isEmpty() || firstName_edit.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input first name", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if(lastName_edit.getText().toString().isEmpty() || lastName_edit.getText().toString().equals("")){
//            Toast.makeText(getApplicationContext(), "please input last name", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if(email_edit.getText().toString().isEmpty() || email_edit.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            if(!isEmailValid(email_edit.getText().toString())){
                Toast.makeText(getApplicationContext(), "Please input correct email type", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(password_edit.getText().toString().isEmpty() || password_edit.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_pass_edit.getText().toString().isEmpty() || confirm_pass_edit.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password_edit.getText().toString().equals(confirm_pass_edit.getText().toString())){
            Toast.makeText(getApplicationContext(), "password is not matched", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!checkUpperCase(password_edit.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please input at least 1 Capital letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean checkUpperCase(String str){
        char ch;
        boolean uppercase_flag = false;
        for(int i = 0; i < str.length(); i++){
            ch = str.charAt(i);
            if(Character.isUpperCase(ch)){
                uppercase_flag = true;
                break;
            }
        }
        return uppercase_flag;
    }
}
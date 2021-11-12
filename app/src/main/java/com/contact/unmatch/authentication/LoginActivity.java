package com.contact.unmatch.authentication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.contact.unmatch.R;
import com.contact.unmatch.home.HomeActivity;
import com.contact.unmatch.model.User;
import com.contact.unmatch.utils.HttpUtils;
import com.contact.unmatch.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText email_edit;
    private EditText password_edit;
    private Button signin_btn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        email_edit = (EditText) findViewById(R.id.login_email_edit);
        password_edit = (EditText) findViewById(R.id.login_password_edit);
        signin_btn = (Button) findViewById(R.id.signin_button);
        signin_btn.setOnClickListener(signin_listener);
        progressBar = findViewById(R.id.progressBar);

        email_edit.setText(Utils.loadPreferences("USER_EMAIL"));
        password_edit.setText(Utils.loadPreferences("USER_PASS"));

        //email_edit.setText("aaa@test.com");
        //password_edit.setText("Glowglow");
    }

    private View.OnClickListener signin_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strEmail = email_edit.getText().toString().trim();
            String strPassword = password_edit.getText().toString().trim();
            try {
                if (invalidate(strEmail, strPassword)) {
                    JSONObject obj = new JSONObject();
                    obj.put("email", strEmail);
                    obj.put("password", strPassword);
                    String url = HttpUtils.API_URL + "Authentication/Login";
                    progressBar.setVisibility(View.VISIBLE);
                    HttpUtils.post(getApplicationContext(), null, url, obj, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("api_result", "" + statusCode + " : " + response.toString());
                            try {
                                progressBar.setVisibility(View.GONE);
                                if (response.getBoolean("isAuthSuccessful")) {
                                    String access_token = response.getString("accessToken");
                                    String refresh_token = response.getString("refreshToken");

                                    Utils.setAccessToken(access_token);
                                    Utils.setRefreshToken(refresh_token);
                                    Utils.savePreferences("USER_EMAIL", strEmail);
                                    Utils.savePreferences("USER_PASS", strPassword);

                                    User user = User.getInstance(true);
                                    user.setEmail(strEmail);
                                    user.setPassword(strPassword);
                                    User.saveUser(user);

                                    Toast.makeText(getApplicationContext(), R.string.message_success_login, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                if(!errorResponse.getBoolean("isAuthSuccessful")){
                                    String error_msg = errorResponse.getString("errorMessage");
                                    Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("api_result", "" + statusCode + " : " + responseString);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
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

    public boolean invalidate(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please input valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), "Please input correct email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please input valid password", Toast.LENGTH_SHORT).show();
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
}